#!/bin/sh
set -e

mkdir -p /app/certs

echo "$KAFKA_TRUSTSTORE_B64" | base64 -d > /app/certs/kafka.client.truststore.jks

exec java -jar /app/app.jar
