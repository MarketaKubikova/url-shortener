package com.urlshortener.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for URL-related operations.
 */
@Slf4j
public class UrlUtil {
    private UrlUtil() {
    }

    /**
     * Normalizes the given URL by ensuring it has a scheme (http or https).
     * If the URL does not start with "http://" or "https://", "https://" is prepended.
     *
     * @param url the URL to normalize
     * @return the normalized URL with a scheme
     */
    public static String normalizeUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            log.warn("Missing scheme in URL: {}. Prepending 'https://'", url);
            return "https://" + url;
        }
        return url;
    }
}
