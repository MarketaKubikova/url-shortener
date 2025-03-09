package com.urlshortener.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UrlUtilTest {

    @ParameterizedTest
    @CsvSource({
            "http://example.com, http://example.com",
            "https://example.com, https://example.com",
            "example.com, https://example.com",
            "www.example.com, https://www.example.com"
    })
    void normalizeUrl(String input, String expected) {
        String normalizedUrl = UrlUtil.normalizeUrl(input);

        Assertions.assertThat(normalizedUrl).isEqualTo(expected);
    }
}
