package com.urlshortener.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity class representing a URL.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "urls")
public class Url {
    /**
     * The unique identifier for the URL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The original URL to be shortened.
     */
    @Column(nullable = false, unique = true)
    private String originalUrl;

    /**
     * The shortened URL.
     */
    @Column(nullable = false, unique = true)
    private String shortUrl;

    /**
     * The date and time when the URL was created.
     */
    private LocalDateTime createdAt;

    /**
     * The date and time when the URL will expire.
     */
    private LocalDateTime expirationDate;
}
