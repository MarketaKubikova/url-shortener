package com.urlshortener.exception;

/**
 * Custom exception thrown when a URL has expired.
 */
public class UrlExpiredException extends RuntimeException {
    /**
     * Constructs a new UrlExpiredException with the specified detail message.
     *
     * @param message the detail message
     */
    public UrlExpiredException(String message) {
        super(message);
    }
}
