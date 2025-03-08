package com.urlshortener.config;

import com.urlshortener.repository.UrlRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Scheduler configuration class for cleaning up expired URLs.
 * This class is managed by Spring as a component.
 */
@Slf4j
@Component
@AllArgsConstructor
public class SchedulerConfig {
    private final RedisTemplate<String, String> redisTemplate;
    private final UrlRepository urlRepository;

    /**
     * Scheduled task to clean up expired URLs.
     * This method is executed at a fixed interval defined by FIXED_RATE.
     * It deletes URLs from the repository that have an expiration date before the current date and time.
     */
    @Scheduled(fixedRate = 86400000) // Every 24 hours
    public void cleanupExpiredUrls() {
        log.info("Cleaning up expired urls.");
        urlRepository.deleteByExpirationDateBefore(LocalDateTime.now());
    }

    /**
     * Scheduled task to flush click counts to the database.
     * This method is executed at a fixed interval defined by FIXED_RATE.
     * It retrieves click counts from Redis, increments the corresponding URL entities in the database, and clears the counts from Redis.
     */
    @Scheduled(fixedRate = 60000) // Every 1 minute
    public void flushClickCountsToDatabase() {
        Set<String> keys = redisTemplate.keys("clicks:*");

        log.info("Flushing click counts to database for total {} urls.", keys.size());

        for (String key : keys) {
            String shortUrl = key.replace("clicks:", "");
            String count = redisTemplate.opsForValue().get(key);
            if (count != null) {
                int clickCount = Integer.parseInt(count);
                urlRepository.incrementClickCount(shortUrl, clickCount);
                redisTemplate.delete(key); // Clear from Redis after persisting
            }
        }
    }
}
