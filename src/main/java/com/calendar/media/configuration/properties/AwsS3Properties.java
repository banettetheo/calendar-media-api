package com.calendar.media.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
public record AwsS3Properties(
        String bucketName,
        String region,
        String accessKeyId,
        String secretAccessKey
) {}
