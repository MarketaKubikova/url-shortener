package com.urlshortener.service;

import com.urlshortener.dto.UrlShortenedRequestDto;
import com.urlshortener.dto.UrlShortenedResponseDto;
import com.urlshortener.exception.UrlExpiredException;
import com.urlshortener.exception.UrlGenerationException;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.model.Url;
import com.urlshortener.repository.UrlRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class for URL shortening operations.
 */
@Service
@AllArgsConstructor
@Slf4j
public class UrlShorteningService {
    private static final int EXPIRATION_MONTHS = 3;
    private static final int SHORT_URL_LENGTH = 7;
    private static final Duration CACHE_EXPIRATION = Duration.ofHours(1);

    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Shortens the given original URL.
     *
     * @param requestDto the request DTO containing the original URL
     * @return the response DTO containing the shortened URL
     */
    public UrlShortenedResponseDto shortenUrl(UrlShortenedRequestDto requestDto) {
        String originalUrl = requestDto.getOriginalUrl();

        // Check if the original URL is already shortened in cache
        String cachedShortUrl = redisTemplate.opsForValue().get(originalUrl);
        if (cachedShortUrl != null) {
            log.info("Original url {} found in cache.", originalUrl);
            return new UrlShortenedResponseDto(cachedShortUrl);
        }

        // Check if the original URL is already shortened in database
        Optional<Url> existingUrl = urlRepository.findByOriginalUrl(originalUrl);
        if (existingUrl.isPresent()) {
            log.info("Url {} already exists.", originalUrl);
            String shortUrl = existingUrl.get().getShortUrl();
            redisTemplate.opsForValue().set(originalUrl, shortUrl, CACHE_EXPIRATION);

            return new UrlShortenedResponseDto(shortUrl);
        }

        String shortUrl = generateShortUrl(originalUrl);
        LocalDateTime expiration = LocalDateTime.now().plusMonths(EXPIRATION_MONTHS);

        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(shortUrl);
        url.setCreatedAt(LocalDateTime.now());
        url.setExpirationDate(expiration);

        log.info("Short url {} created.", shortUrl);
        urlRepository.save(url);

        log.info("Storing url {} in cache", originalUrl);
        redisTemplate.opsForValue().set(originalUrl, shortUrl, CACHE_EXPIRATION);
        redisTemplate.opsForValue().set(shortUrl, originalUrl, CACHE_EXPIRATION);

        return new UrlShortenedResponseDto(shortUrl);
    }

    /**
     * Retrieves the original URL for the given shortened URL.
     *
     * @param shortUrl the shortened URL
     * @return the original URL
     * @throws UrlNotFoundException if the shortened URL is not found
     * @throws UrlExpiredException if the shortened URL has expired
     */
    public Url getOriginalUrl(String shortUrl) {
        // Check Redis cache first
        String cachedOriginalUrl = redisTemplate.opsForValue().get(shortUrl);
        if (cachedOriginalUrl != null) {
            log.info("Short url {} found in cache.", shortUrl);
            incrementClickCount(shortUrl);
            return new Url(null, cachedOriginalUrl, shortUrl, null, null, 0);
        }

        Url url = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> {
                    log.error("Short url {} not found.", shortUrl);
                    return new UrlNotFoundException("Short URL " + shortUrl + " not found in db.");
                });

        if (isUrlExpired(url)) {
            log.error("Url {} expired.", shortUrl);
            throw new UrlExpiredException("URL has expired for: " + shortUrl);
        }

        // Store the URL in cache, be sure not to store it for longer than is the url expiration date
        Duration urlValidity = Duration.between(LocalDateTime.now(), url.getExpirationDate());
        if (urlValidity.getSeconds() > CACHE_EXPIRATION.getSeconds()) {
            log.info("Storing url {} in cache with default cache expiration time.", shortUrl);
            redisTemplate.opsForValue().set(shortUrl, url.getOriginalUrl(), CACHE_EXPIRATION);
        } else {
            log.info("Url validity expires soon. Storing url {} in cache with shortened cache expiration time.", shortUrl);
            redisTemplate.opsForValue().set(shortUrl, url.getOriginalUrl(), Duration.ofSeconds(urlValidity.getSeconds()));
        }

        incrementClickCount(shortUrl);

        return url;
    }

    /**
     * Retrieves the click count for the given shortened URL.
     *
     * @param shortUrl the shortened URL
     * @return the click count
     * @throws UrlNotFoundException if the shortened URL is not found
     */
    public int getClickCount(String shortUrl) {
        log.info("Retrieving click count for short url {}.", shortUrl);
        return urlRepository.findByShortUrl(shortUrl)
                .map(Url::getClickCount)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for: " + shortUrl));
    }

    /**
     * Generates a shortened URL for the given original URL.
     *
     * @param originalUrl the original URL
     * @return the shortened URL
     * @throws UrlGenerationException if there is an error generating the shortened URL
     */
    private String generateShortUrl(String originalUrl) {
        try {
            log.info("Generating short url for {}", originalUrl);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(originalUrl.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02X", b));
            }

            return hexString.substring(0, SHORT_URL_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate short url", e);
            throw new UrlGenerationException("Error when generating short url", e);
        }
    }

    /**
     * Checks if the URL is expired.
     *
     * @param url the URL entity
     * @return true if the URL is expired, false otherwise
     */
    private boolean isUrlExpired(Url url) {
        return url.getExpirationDate().isBefore(LocalDateTime.now());
    }

    /**
     * Increments the click count by 1 of the shortened URL.
     *
     * @param shortUrl the shortened URL
     */
    private void incrementClickCount(String shortUrl) {
        log.info("Incrementing click count by 1 for short url {}.", shortUrl);
        redisTemplate.opsForValue().increment("clicks:" + shortUrl, 1);
    }
}
