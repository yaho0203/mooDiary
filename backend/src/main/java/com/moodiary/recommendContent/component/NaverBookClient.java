package com.moodiary.recommendContent.component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;


// 모든 메서드를 빈으로 등록하는 Component
@Component
public class NaverBookClient {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;


//    private String clientId = "XVuorZhB1AQFMjaVvvj1";
//    private String clientSecret = "deJ3mebRP3";

    public String getBookImageUrl(String bookTitle) {
        if (bookTitle == null || bookTitle.isEmpty()) {
            return "/images/default-book.jpg";
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            // 항상 결과가 나오도록 테스트용 검색어 사용
            // URI 안전하게 생성 (한글 포함)
            String apiUrl = "https://openapi.naver.com/v1/search/book.json?query="
                    + bookTitle
                    + "&display=1";

            System.out.println(apiUrl);


            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", clientId);
            headers.set("X-Naver-Client-Secret", clientSecret);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, JsonNode.class);

            // 디버깅 로그
            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            JsonNode body = response.getBody();
            if (body != null) {
                JsonNode itemsNode = body.path("items"); // null-safe 접근
                if (itemsNode.isArray() && itemsNode.size() > 0) {
                    JsonNode firstItem = itemsNode.get(0);
                    JsonNode imageNode = firstItem.path("image");
                    if (!imageNode.isMissingNode()) {
                        return imageNode.asText();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("네이버 도서 API 호출 실패: " + e.getMessage());
        }

        // 검색 결과 없거나 예외 발생 시 기본 이미지
        return "/images/default-book.jpg";
    }
}