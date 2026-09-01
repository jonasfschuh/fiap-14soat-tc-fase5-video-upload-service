FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
# 'sh mvnw' evita depender do shebang/bit executavel do mvnw, que pode ser
# corrompido para CRLF ao ser dado checkout num runner Windows.
RUN sh mvnw clean package -DskipTests
FROM eclipse-temurin:21-jre-alpine

# ── New Relic Java Agent ────────────────────────────────────────────────────
ARG NEW_RELIC_AGENT_VERSION=8.18.0
RUN apk add --no-cache curl \
    && mkdir -p /app/newrelic \
    && curl -sSL \
       "https://download.newrelic.com/newrelic/java-agent/newrelic-agent/${NEW_RELIC_AGENT_VERSION}/newrelic-agent-${NEW_RELIC_AGENT_VERSION}.jar" \
       -o /app/newrelic/newrelic.jar

WORKDIR /app
COPY --from=build /app/application/target/video-upload-application-1.0.0-exec.jar app.jar
COPY newrelic/newrelic.yml /app/newrelic/newrelic.yml
EXPOSE 8083
ENTRYPOINT ["java", \
  "-javaagent:/app/newrelic/newrelic.jar", \
  "-Dnewrelic.config.file=/app/newrelic/newrelic.yml", \
  "-jar", "app.jar"]
