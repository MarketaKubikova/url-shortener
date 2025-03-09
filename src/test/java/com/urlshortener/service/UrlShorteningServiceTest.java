package com.urlshortener.service;

import com.urlshortener.dto.UrlShortenedRequestDto;
import com.urlshortener.dto.UrlShortenedResponseDto;
import com.urlshortener.exception.UrlExpiredException;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.model.Url;
import com.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShorteningServiceTest {
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private UrlShorteningService urlShorteningService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    UrlShortenedRequestDto requestDto = new UrlShortenedRequestDto();

    @Test
    void shortenUrl_originalUrlIsNotCachedOrInDatabase_shouldReturnShortenedUrl() {
        requestDto.setOriginalUrl("https://google.com");

        when(valueOperations.get(anyString())).thenReturn(null);
        when(urlRepository.findByOriginalUrl(anyString())).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UrlShortenedResponseDto responseDto = urlShorteningService.shortenUrl(requestDto);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getShortUrl()).isInstanceOf(String.class);
        assertThat(responseDto.getShortUrl()).hasSize(6);
        verify(valueOperations, times(2)).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void shortenUrl_originalUrlIsCached_shouldReturnShortenedUrl() {
        requestDto.setOriginalUrl("http://example.com");

        when(valueOperations.get(anyString())).thenReturn("abc123");

        UrlShortenedResponseDto responseDto = urlShorteningService.shortenUrl(requestDto);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getShortUrl()).isEqualTo("abc123");
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
        verifyNoInteractions(urlRepository);
    }

    @Test
    void shortenUrl_originalUrlIsInDatabase_shouldReturnShortenedUrl() {
        requestDto.setOriginalUrl("http://example.com");

        Url url = new Url();
        url.setShortUrl("abc123");
        url.setOriginalUrl("http://example.com");

        when(valueOperations.get(anyString())).thenReturn(null);
        when(urlRepository.findByOriginalUrl(anyString())).thenReturn(Optional.of(url));

        UrlShortenedResponseDto responseDto = urlShorteningService.shortenUrl(requestDto);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getShortUrl()).isEqualTo("abc123");
        verify(valueOperations, times(1)).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void getOriginalUrl_shortUrlIsCached_shouldReturnOriginalUrl() {
        when(valueOperations.get(anyString())).thenReturn("http://example.com");

        Url url = urlShorteningService.getOriginalUrl("abc123");

        assertThat(url).isNotNull();
        assertThat(url.getOriginalUrl()).isEqualTo("http://example.com");
        verify(valueOperations, times(1)).increment(anyString(), eq(1L));
    }

    @Test
    void getOriginalUrl_shortUrlIsNotFound_shouldThrowUrlNotFoundException() {
        when(valueOperations.get(anyString())).thenReturn(null);
        when(urlRepository.findByShortUrl(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlShorteningService.getOriginalUrl("nonexistent"))
                .isInstanceOf(UrlNotFoundException.class);
    }

    @Test
    void getOriginalUrl_shortUrlIsExpired_shouldThrowUrlExpiredException() {
        Url url = new Url();
        url.setExpirationDate(LocalDateTime.now().minusDays(1));

        when(valueOperations.get(anyString())).thenReturn(null);
        when(urlRepository.findByShortUrl(anyString())).thenReturn(Optional.of(url));

        assertThatThrownBy(() -> urlShorteningService.getOriginalUrl("expired"))
                .isInstanceOf(UrlExpiredException.class);
    }

    @Test
    void getClickCount_shortUrlExists_shouldReturnClickCount() {
        Url url = new Url();
        url.setClickCount(5);

        when(urlRepository.findByShortUrl(anyString())).thenReturn(Optional.of(url));

        int clickCount = urlShorteningService.getClickCount("abc123");

        assertThat(clickCount).isEqualTo(5);
    }

    @Test
    void getClickCount_shortUrlDoesNotExist_shouldThrowUrlNotFoundException() {
        when(urlRepository.findByShortUrl(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlShorteningService.getClickCount("nonexistent"))
                .isInstanceOf(UrlNotFoundException.class);
    }
}
