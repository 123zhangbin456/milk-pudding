package com.milkpudding.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milkpudding.api.service.ComparisonResult;
import com.milkpudding.api.service.RetrofitComparisonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ComparisonController 集成测试
 */
@WebMvcTest(ComparisonController.class)
class ComparisonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RetrofitComparisonService comparisonService;

    @Test
    void testCompareGetPost() throws Exception {
        // Given
        Long postId = 1L;
        ComparisonResult mockResult = ComparisonResult.builder()
                .feignResponseTime(100L)
                .retrofitResponseTime(80L)
                .feignSuccess(true)
                .retrofitSuccess(true)
                .feignResult(Map.of("id", 1, "title", "Test Post"))
                .retrofitResult(Map.of("id", 1, "title", "Test Post"))
                .build();

        when(comparisonService.compareGetPost(anyLong())).thenReturn(mockResult);

        // When & Then
        mockMvc.perform(get("/api/comparison/post/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.feignResponseTime").value(100))
                .andExpect(jsonPath("$.retrofitResponseTime").value(80))
                .andExpect(jsonPath("$.feignSuccess").value(true))
                .andExpect(jsonPath("$.retrofitSuccess").value(true))
                .andExpect(jsonPath("$.feignResult.id").value(1))
                .andExpect(jsonPath("$.retrofitResult.id").value(1));
    }

    @Test
    void testCompareGetAllPosts() throws Exception {
        // Given
        ComparisonResult mockResult = ComparisonResult.builder()
                .feignResponseTime(200L)
                .retrofitResponseTime(150L)
                .feignSuccess(true)
                .retrofitSuccess(true)
                .feignResult(Map.of("count", 2))
                .retrofitResult(Map.of("count", 2))
                .build();

        when(comparisonService.compareGetAllPosts()).thenReturn(mockResult);

        // When & Then
        mockMvc.perform(get("/api/comparison/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.feignResponseTime").value(200))
                .andExpect(jsonPath("$.retrofitResponseTime").value(150))
                .andExpect(jsonPath("$.feignSuccess").value(true))
                .andExpect(jsonPath("$.retrofitSuccess").value(true));
    }

    @Test
    void testCompareCreatePost() throws Exception {
        // Given
        Map<String, Object> postData = Map.of(
                "title", "New Post",
                "body", "New Body",
                "userId", 1
        );

        ComparisonResult mockResult = ComparisonResult.builder()
                .feignResponseTime(120L)
                .retrofitResponseTime(90L)
                .feignSuccess(true)
                .retrofitSuccess(true)
                .feignResult(Map.of("id", 101, "title", "New Post"))
                .retrofitResult(Map.of("id", 101, "title", "New Post"))
                .build();

        when(comparisonService.compareCreatePost(any())).thenReturn(mockResult);

        // When & Then
        mockMvc.perform(post("/api/comparison/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.feignResponseTime").value(120))
                .andExpect(jsonPath("$.retrofitResponseTime").value(90))
                .andExpect(jsonPath("$.feignSuccess").value(true))
                .andExpect(jsonPath("$.retrofitSuccess").value(true));
    }

    @Test
    void testHealthCheck() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/comparison/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Retrofit Comparison API"))
                .andExpect(jsonPath("$.features.retrofit").value("enabled"))
                .andExpect(jsonPath("$.features.feign").value("enabled"));
    }
}
