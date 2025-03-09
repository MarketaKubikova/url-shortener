package com.urlshortener.controller;

import com.urlshortener.dto.UrlShortenedRequestDto;
import com.urlshortener.dto.UrlShortenedResponseDto;
import com.urlshortener.exception.GlobalExceptionHandler;
import com.urlshortener.exception.UrlExpiredException;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.model.Url;
import com.urlshortener.service.UrlShorteningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)
class UrlShorteningControllerTest {
    private static final String BASE_URL = "/api";

    private MockMvc mockMvc;

    @Mock
    private UrlShorteningService urlShorteningService;

    @InjectMocks
    private UrlShorteningController urlShorteningController;


    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(urlShorteningController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shortenUrl_createsShortenedUrl() throws Exception {
        when(urlShorteningService.shortenUrl(any(UrlShortenedRequestDto.class))).thenReturn(new UrlShortenedResponseDto("abc123"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\": \"https://example.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortUrl").value("abc123"));
    }

    @Test
    void shortenUrl_invalidUrl_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\": \"invalid-url\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void redirectToOriginalUrl_redirectsToOriginalUrl() throws Exception {
        when(urlShorteningService.getOriginalUrl("abc123")).thenReturn(new Url(null, "http://example.com", "abc123", null, null, 0));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/abc123"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.header().string("Location", "http://example.com"))
                .andExpect(MockMvcResultMatchers.redirectedUrl("http://example.com"));
    }

    @Test
    void redirectToOriginalUrl_urlNotFound_returnsNotFound() throws Exception {
        when(urlShorteningService.getOriginalUrl("nonexistent")).thenThrow(new UrlNotFoundException("URL not found"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/nonexistent"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("URL not found"));
    }

    @Test
    void redirectToOriginalUrl_urlExpired_returnsGone() throws Exception {
        when(urlShorteningService.getOriginalUrl("expired")).thenThrow(new UrlExpiredException("URL expired"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/expired"))
                .andExpect(MockMvcResultMatchers.status().isGone())
                .andExpect(MockMvcResultMatchers.content().string("URL expired"));
    }

    @Test
    void getStats_returnsClickStats() throws Exception {
        when(urlShorteningService.getClickCount("abc123")).thenReturn(10);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/abc123/stats"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortUrl").value("abc123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.clickCount").value(10));
    }

    @Test
    void getStats_urlNotFound_returnsNotFound() throws Exception {
        when(urlShorteningService.getClickCount("nonexistent")).thenThrow(new UrlNotFoundException("URL not found"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/nonexistent/stats"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("URL not found"));
    }
}
