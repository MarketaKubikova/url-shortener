package com.urlshortener.dto;

import com.urlshortener.validation.ValidUrl;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "Original URL cannot be empty")
    @ValidUrl
    private String originalUrl;
}
