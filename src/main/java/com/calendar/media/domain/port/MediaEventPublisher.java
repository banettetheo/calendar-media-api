package com.calendar.media.domain.port;
import reactor.core.publisher.Mono;

public interface MediaEventPublisher {
    // Événement spécifique pour notifier le changement de photo
    Mono<Void> publishProfilePictureUpdated(Long userId, String mediaUrl);
}
