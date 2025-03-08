package com.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for representing the click statistics of a shortened URL.
 */
@Getter
@Setter
@AllArgsConstructor
public class ShortUrlClickStatsResponseDto {
    /**
     * The shortened URL.
     */
    private String shortUrl;

    /**
     * The number of times the shortened URL has been clicked.
     */
    private int clickCount;
}
