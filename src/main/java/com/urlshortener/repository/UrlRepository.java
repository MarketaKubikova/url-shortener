package com.urlshortener.repository;

import com.urlshortener.model.Url;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    /**
     * Deletes URL entities with expiration dates before a given date and time.
     *
     * @param dateTime the date and time to compare against
     */
    void deleteByExpirationDateBefore(LocalDateTime dateTime);

    /**
     * Increments the click count of a URL entity by one.
     *
     * @param shortUrl the shortened URL
     */
    @Modifying
    @Transactional
    @Query("UPDATE Url u SET u.clickCount = u.clickCount + :clickCount WHERE u.shortUrl = :shortUrl")
    void incrementClickCount(@Param("shortUrl") String shortUrl, @Param("clickCount") int clickCount);
}
