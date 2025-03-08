package com.urlshortener.config;

import com.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Scheduler configuration class for cleaning up expired URLs.
 * This class is managed by Spring as a component.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerConfig {
    private static final int FIXED_RATE = 86400000; // 24 hours

    private final UrlRepository urlRepository;

    /**
     * Scheduled task to clean up expired URLs.
     * This method is executed at a fixed interval defined by FIXED_RATE.
     * It deletes URLs from the repository that have an expiration date before the current date and time.
     */
    @Scheduled(fixedRate = FIXED_RATE)
    public void cleanupExpiredUrls() {
        log.info("Cleaning up expired urls.");
        urlRepository.deleteByExpirationDateBefore(LocalDateTime.now());
    }
}
