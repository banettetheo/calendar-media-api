package com.calendar.media.domain.port;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface FileStorage {
    // Retourne l'URL du fichier uploadé
    Mono<String> storeObject(FilePart filePart, String key);

}
