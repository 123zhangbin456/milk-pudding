package com.milkpudding.api.service;

import com.milkpudding.api.client.JsonPlaceholderClient;
import com.milkpudding.api.client.JsonPlaceholderRetrofitClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * RetrofitComparisonService 测试类
 */
@ExtendWith(MockitoExtension.class)
class RetrofitComparisonServiceTest {

    @Mock
    private JsonPlaceholderClient feignClient;

    @Mock
    private JsonPlaceholderRetrofitClient retrofitClient;

    @InjectMocks
    private RetrofitComparisonService comparisonService;

    @Test
    void testCompareGetPost_BothSuccess() {
        // Given
        Long postId = 1L;
        Map<String, Object> expectedResult = Map.of(
            "id", 1,
            "title", "Test Post",
            "body", "Test Body",
            "userId", 1
        );

        when(feignClient.getPost(postId)).thenReturn(expectedResult);
        when(retrofitClient.getPost(postId)).thenReturn(expectedResult);

        // When
        ComparisonResult result = comparisonService.compareGetPost(postId);

        // Then
        assertNotNull(result);
        assertTrue(result.isFeignSuccess());
        assertTrue(result.isRetrofitSuccess());
        assertNotNull(result.getFeignResult());
        assertNotNull(result.getRetrofitResult());
        assertTrue(result.getFeignResponseTime() >= 0);
        assertTrue(result.getRetrofitResponseTime() >= 0);
        
        verify(feignClient).getPost(postId);
        verify(retrofitClient).getPost(postId);
    }

    @Test
    void testCompareGetPost_FeignFails() {
        // Given
        Long postId = 1L;
        Map<String, Object> expectedResult = Map.of("id", 1, "title", "Test Post");
        
        when(feignClient.getPost(postId)).thenThrow(new RuntimeException("Feign error"));
        when(retrofitClient.getPost(postId)).thenReturn(expectedResult);

        // When
        ComparisonResult result = comparisonService.compareGetPost(postId);

        // Then
        assertNotNull(result);
        assertFalse(result.isFeignSuccess());
        assertTrue(result.isRetrofitSuccess());
        assertEquals("Feign error", result.getFeignError());
        assertNull(result.getRetrofitError());
        assertNotNull(result.getRetrofitResult());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCompareGetAllPosts() {
        // Given
        Map<String, Object>[] feignResult = new Map[]{
            Map.of("id", 1, "title", "Post 1"),
            Map.of("id", 2, "title", "Post 2")
        };
        List<Map<String, Object>> retrofitResult = List.of(
            Map.of("id", 1, "title", "Post 1"),
            Map.of("id", 2, "title", "Post 2")
        );

        when(feignClient.getAllPosts()).thenReturn(feignResult);
        when(retrofitClient.getAllPosts()).thenReturn(retrofitResult);

        // When
        ComparisonResult result = comparisonService.compareGetAllPosts();

        // Then
        assertNotNull(result);
        assertTrue(result.isFeignSuccess());
        assertTrue(result.isRetrofitSuccess());
        assertNotNull(result.getFeignResult());
        assertNotNull(result.getRetrofitResult());
        
        // 检查结果包含正确的数量信息
        Map<String, Object> feignResultMap = result.getFeignResult();
        Map<String, Object> retrofitResultMap = result.getRetrofitResult();
        assertEquals(2, feignResultMap.get("count"));
        assertEquals(2, retrofitResultMap.get("count"));
    }

    @Test
    void testCompareCreatePost() {
        // Given
        Map<String, Object> postData = Map.of(
            "title", "New Post",
            "body", "New Body",
            "userId", 1
        );
        Map<String, Object> expectedResult = Map.of(
            "id", 101,
            "title", "New Post",
            "body", "New Body",
            "userId", 1
        );

        when(feignClient.createPost(any())).thenReturn(expectedResult);
        when(retrofitClient.createPost(any())).thenReturn(expectedResult);

        // When
        ComparisonResult result = comparisonService.compareCreatePost(postData);

        // Then
        assertNotNull(result);
        assertTrue(result.isFeignSuccess());
        assertTrue(result.isRetrofitSuccess());
        assertNotNull(result.getFeignResult());
        assertNotNull(result.getRetrofitResult());
        
        verify(feignClient).createPost(postData);
        verify(retrofitClient).createPost(postData);
    }
}
