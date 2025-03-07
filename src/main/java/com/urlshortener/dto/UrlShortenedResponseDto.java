package com.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for URL shortening responses.
 */
@Getter
@Setter
@AllArgsConstructor
public class UrlShortenedResponseDto {
    /**
     * The shortened URL.
     */
    private String shortUrl;
}
