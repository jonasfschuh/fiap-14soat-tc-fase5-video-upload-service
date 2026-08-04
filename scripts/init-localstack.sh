#!/bin/bash
# =============================================================================
# init-localstack.sh
# Master LocalStack initialization script — fiap-14soat-tc-fase5
#
# This script runs automatically when LocalStack starts (ready.d hook).
# It creates ALL AWS resources (S3 buckets, SQS queues, SNS topics) needed
# by every microservice in the local stack.
#
# ⚠️  When adding a new microservice, append its resources to this file.
#     Each section is clearly labeled with the owning service.
# =============================================================================

set -e

AWS_ENDPOINT="http://localhost:4566"
REGION="us-east-1"
AWS_CMD="awslocal"

echo ""
echo "============================================================"
echo "  FIAP5 — LocalStack Init Script"
echo "============================================================"

# ─── Helper functions ────────────────────────────────────────────────────────

create_queue() {
  local name=$1
  echo "  [SQS] Creating queue: $name"
  $AWS_CMD sqs create-queue \
    --queue-name "$name" \
    --region "$REGION" \
    --output text > /dev/null
}

create_dlq_with_redrive() {
  local main_queue=$1
  local dlq_name="${main_queue}-dlq"

  echo "  [SQS] Creating DLQ: $dlq_name"
  DLQ_URL=$($AWS_CMD sqs create-queue \
    --queue-name "$dlq_name" \
    --region "$REGION" \
    --query 'QueueUrl' \
    --output text)

  DLQ_ARN=$($AWS_CMD sqs get-queue-attributes \
    --queue-url "$DLQ_URL" \
    --attribute-names QueueArn \
    --query 'Attributes.QueueArn' \
    --output text)

  echo "  [SQS] Setting redrive policy on: $main_queue → $dlq_name"
  MAIN_URL=$($AWS_CMD sqs get-queue-url \
    --queue-name "$main_queue" \
    --query 'QueueUrl' \
    --output text)

  $AWS_CMD sqs set-queue-attributes \
    --queue-url "$MAIN_URL" \
    --attributes "{\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"$DLQ_ARN\\\",\\\"maxReceiveCount\\\":\\\"3\\\"}\"}" \
    --region "$REGION" > /dev/null
}

create_s3_bucket() {
  local bucket=$1
  echo "  [S3 ] Creating bucket: $bucket"
  $AWS_CMD s3 mb "s3://$bucket" --region "$REGION" > /dev/null
  $AWS_CMD s3api put-bucket-cors \
    --bucket "$bucket" \
    --cors-configuration '{
      "CORSRules": [{
        "AllowedHeaders": ["*"],
        "AllowedMethods": ["GET","PUT","POST","DELETE","HEAD"],
        "AllowedOrigins": ["*"],
        "ExposeHeaders": ["ETag"]
      }]
    }' > /dev/null
}

# ─── video-upload-service ────────────────────────────────────────────────────
echo ""
echo "[ video-upload-service ]"

create_s3_bucket "fiap-video-uploads"
create_queue "video-uploaded"
create_dlq_with_redrive "video-uploaded"

# ─── video-processing-service ────────────────────────────────────────────────
echo ""
echo "[ video-processing-service ]"

create_queue "video-events"
create_dlq_with_redrive "video-events"

# ─── (future services — add queues/buckets below this line) ──────────────────
# echo ""
# echo "[ notification-service ]"
# create_queue "notifications"
# create_dlq_with_redrive "notifications"

# ─── Summary ─────────────────────────────────────────────────────────────────
echo ""
echo "============================================================"
echo "  Resources created:"
echo ""
echo "  S3 Buckets:"
$AWS_CMD s3 ls --region "$REGION" | awk '{print "    s3://"$3}'

echo ""
echo "  SQS Queues:"
$AWS_CMD sqs list-queues --region "$REGION" --query 'QueueUrls[]' --output text \
  | tr '\t' '\n' | awk -F'/' '{print "    "$NF}'

echo ""
echo "  Access via StackPort: http://localhost:8080"
echo "============================================================"
echo ""
