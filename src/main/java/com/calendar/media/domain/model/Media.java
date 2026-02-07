package com.calendar.media.domain.model;
import java.time.Instant;
import java.util.UUID;

public record Media(UUID id, String userId, String url, MediaType type, String mimeType, Instant createdAt) {

}
