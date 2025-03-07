package com.urlshortener.controller;

import com.urlshortener.dto.UrlShortenedRequestDto;
import com.urlshortener.dto.UrlShortenedResponseDto;
import com.urlshortener.model.Url;
import com.urlshortener.service.UrlShorteningService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UrlShorteningController {
    private final UrlShorteningService urlShorteningService;

    @PostMapping("/api/shorten")
    public ResponseEntity<UrlShortenedResponseDto> shortenUrl(@RequestBody UrlShortenedRequestDto requestDto) {
        UrlShortenedResponseDto responseDto = urlShorteningService.shortenUrl(requestDto);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{shortenUrl}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortenUrl) {
        Url originalUrl = urlShorteningService.getOriginalUrl(shortenUrl);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", originalUrl.getOriginalUrl())
                .build();
    }
}
