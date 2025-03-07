package com.urlshortener.controller;

import com.urlshortener.dto.UrlShortenedRequestDto;
import com.urlshortener.dto.UrlShortenedResponseDto;
import com.urlshortener.model.Url;
import com.urlshortener.service.UrlShorteningService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling URL shortening operations.
 */
@RestController
@AllArgsConstructor
public class UrlShorteningController {
    private final UrlShorteningService urlShorteningService;

    /**
     * Endpoint to shorten a given URL.
     *
     * @param requestDto the request DTO containing the original URL
     * @return the response DTO containing the shortened URL
     */
    @PostMapping("/api/shorten")
    public ResponseEntity<UrlShortenedResponseDto> shortenUrl(@Valid @RequestBody UrlShortenedRequestDto requestDto) {
        UrlShortenedResponseDto responseDto = urlShorteningService.shortenUrl(requestDto);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Endpoint to redirect to the original URL based on the shortened URL.
     *
     * @param shortenUrl the shortened URL
     * @return a response entity with a redirection to the original URL
     */
    @GetMapping("/{shortenUrl}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortenUrl) {
        Url originalUrl = urlShorteningService.getOriginalUrl(shortenUrl);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", originalUrl.getOriginalUrl())
                .build();
    }
}
