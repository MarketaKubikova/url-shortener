package com.urlshortener.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for URL shortening requests.
 */
@Getter
@Setter
public class UrlShortenedRequestDto {
    /**
     * The original URL to be shortened.
     */
    private String originalUrl;
}
