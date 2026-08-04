package br.com.fiap.infrastructure.adapters.storage;

import br.com.fiap.domain.exceptions.VideoStorageException;
import br.com.fiap.domain.ports.out.VideoStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

public class S3StorageAdapter implements VideoStoragePort {

    private static final Logger log = LoggerFactory.getLogger(S3StorageAdapter.class);

    private final S3Client s3Client;
    private final String bucket;

    public S3StorageAdapter(S3Client s3Client, String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    @Override
    public String store(String key, InputStream stream, long sizeBytes, String mimeType) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(mimeType)
                    .contentLength(sizeBytes)
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(stream, sizeBytes));
            log.info("[S3-STORAGE] Uploaded to s3://{}/{}", bucket, key);
            return key;
        } catch (Exception e) {
            throw new VideoStorageException("Failed to upload to S3: " + key, e);
        }
    }
}
