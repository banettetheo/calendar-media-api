package com.calendar.media.configuration;

import com.calendar.media.domain.port.FileStorage;
import com.calendar.media.domain.port.MediaEventPublisher;
import com.calendar.media.domain.service.MediaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MediaApplicationConfig {

    @Bean
    public MediaService mediaService(
            FileStorage fileStorage
            /*MediaEventPublisher mediaEventPublisher*/){
        return new MediaService(fileStorage /*mediaEventPublisher*/);
    }
}
