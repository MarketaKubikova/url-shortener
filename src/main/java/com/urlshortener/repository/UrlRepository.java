package com.urlshortener.repository;

import com.urlshortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for URL entities.
 * Extends JpaRepository to provide CRUD operations.
 */
@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    /**
     * Finds a URL entity by its original URL.
     *
     * @param originalUrl the original URL
     * @return an Optional containing the found URL entity, or empty if not found
     */
    Optional<Url> findByOriginalUrl(String originalUrl);

    /**
     * Finds a URL entity by its shortened URL.
     *
     * @param shortUrl the shortened URL
     * @return an Optional containing the found URL entity, or empty if not found
     */
    Optional<Url> findByShortUrl(String shortUrl);
}
