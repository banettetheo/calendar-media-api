package com.calendar.media.domain.service;

import com.calendar.media.domain.model.Media;
import com.calendar.media.domain.model.MediaType;
import com.calendar.media.domain.port.FileStorage;
import com.calendar.media.domain.port.MediaEventPublisher;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class MediaService {

    private final FileStorage fileStorage;

    //private final MediaEventPublisher eventPublisher;

    public MediaService(FileStorage fileStorage/*MediaEventPublisher eventPublisher*/) {
        this.fileStorage = fileStorage;

       // this.eventPublisher = eventPublisher;
    }

    public Mono<Media> updateProfilePicture(String userId, Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(filePart -> {
            String s3Key = "profiles/" + userId + "/" + UUID.randomUUID() + "-" + filePart.filename();

            return fileStorage.storeObject(filePart, s3Key) // Retourne Mono<String> (l'URL)
                    .flatMap(publicUrl -> {
                        Media media = new Media(
                                UUID.randomUUID(),
                                userId,
                                publicUrl,
                                MediaType.PROFILE_PICTURE,
                                filePart.headers().getContentType().toString(),
                                Instant.now()
                        );

                        return Mono.just(media);
                    });
        });
    }
}