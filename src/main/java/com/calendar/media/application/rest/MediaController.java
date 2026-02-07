package com.calendar.media.application.rest;

import com.calendar.media.domain.model.Media;
import com.calendar.media.domain.service.MediaService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("profile-picture")
    public Mono<ResponseEntity<Media>> updateProfilePicture(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart("file") Mono<FilePart> filePartMono) {

        String userId = jwt.getClaimAsString("businessId");
        return mediaService.updateProfilePicture(userId, filePartMono)
                .flatMap(media -> Mono.just(ResponseEntity.created(URI.create("media/profile-picture")).body(media)));
    }
}