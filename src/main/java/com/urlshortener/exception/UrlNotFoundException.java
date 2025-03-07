package com.urlshortener.exception;

    /**
     * Custom exception thrown when a URL is not found.
     */
    public class UrlNotFoundException extends RuntimeException {
        /**
         * Constructs a new UrlNotFoundException with the specified detail message.
         *
         * @param message the detail message
         */
        public UrlNotFoundException(String message) {
            super(message);
        }
    }
