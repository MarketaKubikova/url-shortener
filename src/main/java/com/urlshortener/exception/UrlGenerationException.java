package com.urlshortener.exception;

/**
 * Custom exception thrown when there is an error generating the short URL.
 */
public class UrlGenerationException extends RuntimeException {
    /**
     * Constructs a new UrlGenerationException with the specified detail message.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public UrlGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
