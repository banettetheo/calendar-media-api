package com.calendar.media.infrastructure.storage;

import com.calendar.media.configuration.properties.AwsS3Properties; // À copier de users-api
import com.calendar.media.domain.port.FileStorage;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

@Component
public class S3FileStorageAdapter implements FileStorage {

    private final S3AsyncClient s3Client;
    private final AwsS3Properties s3Properties;

    public S3FileStorageAdapter(S3AsyncClient s3Client, AwsS3Properties s3Properties) {
        this.s3Client = s3Client;
        this.s3Properties = s3Properties;
    }

    @Override
    public Mono<String> storeObject(FilePart filePart, String userId) {
        // 1. Génération de la clé S3 ici (Délégation de la logique de clé)
        String extension = getExtension(filePart.filename());
        String generatedKey = String.format("profiles/%s/%s%s", userId, UUID.randomUUID(), extension);

        String contentType = Objects.requireNonNull(filePart.headers().getContentType()).toString();

        return DataBufferUtils.join(filePart.content())
                .flatMap(dataBuffer -> {
                    long contentLength = dataBuffer.readableByteCount();
                    ByteBuffer byteBuffer = dataBuffer.asByteBuffer();

                    PutObjectRequest putRequest = PutObjectRequest.builder()
                            .bucket(s3Properties.bucketName())
                            .key(generatedKey) // Utilisation de la clé générée
                            .contentType(contentType)
                            .contentLength(contentLength)
                            .build();

                    return Mono.fromFuture(s3Client.putObject(putRequest, AsyncRequestBody.fromByteBuffer(byteBuffer)))
                            .doFinally(signalType -> DataBufferUtils.release(dataBuffer))
                            .mapNotNull(response -> {
                                if (response.sdkHttpResponse().isSuccessful()) {
                                    return createPublicUrl(generatedKey);
                                } else {
                                    // Tu pourras injecter ta BusinessException ici plus tard
                                    throw new RuntimeException("S3 Upload Failed: " + response.sdkHttpResponse().statusText());
                                }
                            });
                });
    }

    private String getExtension(String fileName) {
        return fileName.lastIndexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".")) : "";
    }

    private String createPublicUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", s3Properties.bucketName(), s3Properties.region(), key);
    }
}