package com.urlshortener.service;

import com.urlshortener.dto.UrlShortenedRequestDto;
import com.urlshortener.dto.UrlShortenedResponseDto;
import com.urlshortener.model.Url;
import com.urlshortener.repository.UrlRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class for URL shortening operations.
 */
@Service
@AllArgsConstructor
@Slf4j
public class UrlShorteningService {
    private static final String BASE_URL = "http://mydomain.com/";
    private static final int EXPIRATION_MONTHS = 3;
    private static final int SHORT_URL_LENGTH = 7;

    private final UrlRepository urlRepository;

    /**
     * Shortens the given original URL.
     *
     * @param requestDto the request DTO containing the original URL
     * @return the response DTO containing the shortened URL
     */
    public UrlShortenedResponseDto shortenUrl(UrlShortenedRequestDto requestDto) {
        Optional<Url> existingUrl = urlRepository.findByOriginalUrl(requestDto.getOriginalUrl());

        if (existingUrl.isPresent()) {
            log.info("Url {} already exists.", existingUrl.get().getOriginalUrl());
            return new UrlShortenedResponseDto(existingUrl.get().getShortUrl());
        }

        String shortUrl = generateShortUrl(requestDto.getOriginalUrl());

        Url url = new Url();
        url.setOriginalUrl(requestDto.getOriginalUrl());
        url.setShortUrl(shortUrl);
        url.setCreatedAt(LocalDateTime.now());
        url.setExpirationDate(LocalDateTime.now().plusMonths(EXPIRATION_MONTHS));

        log.info("Url {} created", url.getShortUrl());
        urlRepository.save(url);

        return new UrlShortenedResponseDto(shortUrl);
    }

    /**
     * Retrieves the original URL for the given shortened URL.
     *
     * @param shortUrl the shortened URL
     * @return the original URL
     * @throws IllegalArgumentException if the shortened URL is not found
     */
    public Url getOriginalUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl).orElseThrow(() -> new IllegalArgumentException("Invalid URL"));
    }

    /**
     * Generates a shortened URL for the given original URL.
     *
     * @param originalUrl the original URL
     * @return the shortened URL
     * @throws RuntimeException if there is an error generating the shortened URL
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

            return BASE_URL + hexString.substring(0, SHORT_URL_LENGTH); // Use first 8 characters for short URL
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error when generating short url", e);
        }
    }
}
