package com.milkpudding.api.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.milkpudding.api.client.JsonPlaceholderClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "external.api.jsonplaceholder.url=http://localhost:8089"
})
class ExternalApiServiceTest {

    @Autowired
    private ExternalApiService externalApiService;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().port(8089));
        wireMockServer.start();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testGetPostWithFeign() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/posts/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "id": 1,
                                    "title": "Test Title",
                                    "body": "Test Body",
                                    "userId": 1
                                }
                                """)));

        // When
        Map<String, Object> result = externalApiService.getPostWithFeign(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("id")).isEqualTo(1);
        assertThat(result.get("title")).isEqualTo("Test Title");
    }

    @Test
    void testGetPostWithWebClient() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/posts/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "id": 1,
                                    "title": "Test Title Reactive",
                                    "body": "Test Body Reactive",
                                    "userId": 1
                                }
                                """)));

        // When
        Map result = externalApiService.getPostWithWebClient(1L).block();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("id")).isEqualTo(1);
        assertThat(result.get("title")).isEqualTo("Test Title Reactive");
    }

    @Test
    void testFallbackWhenServiceDown() {
        // Given - WireMock server is not configured to respond

        // When
        Map<String, Object> result = externalApiService.getPostWithFeign(1L);

        // Then - Should return fallback response
        assertThat(result).isNotNull();
        assertThat(result.get("fallback")).isEqualTo(true);
        assertThat(result.get("title")).isEqualTo("Service Unavailable");
    }
}
