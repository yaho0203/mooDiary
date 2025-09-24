package com.moodiary.recommendContent.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GeminiApiResponse {

    // 하드코딩: API Key + 엔드포인트
    private static final String GEMINI_API_KEY = "AIzaSyBh-xFApx7lLIEcCowlAwvwA1loDXrknXg";
    private static final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getBookGeminiResponse(String bookTitle, String diaryEmotion) {
        String prompt = "사용자가 작성한 일기의 감정은 " + diaryEmotion + "이야 그래서 " + bookTitle + "책을 소개해주려 하는데 간단하게 줄거리를 알려주고 사용자의 감정에 맞춰 간단한 메세지를 줘";

        try {
            RestTemplate restTemplate = new RestTemplate();

            // 요청 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // URL 파라미터로 API 키 추가하는 방식으로 변경
            String urlWithKey = GEMINI_ENDPOINT + "?key=" + GEMINI_API_KEY;

            // Gemini API의 올바른 요청 형식
            Map<String, Object> bodyMap = new HashMap<>();

            // contents 배열 구조
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();

            List<Map<String, String>> parts = new ArrayList<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);
            parts.add(part);

            content.put("parts", parts);
            contents.add(content);

            bodyMap.put("contents", contents);

            String bodyJson = objectMapper.writeValueAsString(bodyMap);

            System.out.println("Request URL: " + urlWithKey);
            System.out.println("Request Body: " + bodyJson);

            HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);

            // POST 요청
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    urlWithKey,
                    HttpMethod.POST,
                    entity,
                    JsonNode.class
            );

            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            JsonNode bodyNode = response.getBody();

            // Gemini API의 실제 응답 구조에 맞게 수정
            if (bodyNode != null && bodyNode.has("candidates") && bodyNode.get("candidates").isArray()
                    && bodyNode.get("candidates").size() > 0) {
                JsonNode candidate = bodyNode.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts")
                        && candidate.get("content").get("parts").isArray()
                        && candidate.get("content").get("parts").size() > 0) {
                    return candidate.get("content").get("parts").get(0).path("text").asText();
                }
            }

            // 응답이 예상과 다른 경우를 위한 fallback
            return "응답 구조를 파싱할 수 없습니다: " + bodyNode.toString();

        } catch (HttpClientErrorException e) {
            System.err.println("HTTP 클라이언트 에러: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return "HTTP 에러 (" + e.getStatusCode() + "): " + e.getResponseBodyAsString();
        } catch (HttpServerErrorException e) {
            System.err.println("HTTP 서버 에러: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return "서버 에러 (" + e.getStatusCode() + "): " + e.getResponseBodyAsString();
        } catch (ResourceAccessException e) {
            System.err.println("네트워크 연결 에러: " + e.getMessage());
            return "네트워크 연결 실패: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("Gemini API 호출 실패: " + e.getMessage());
            e.printStackTrace();
            return "API 호출 실패: " + e.getMessage();
        }
    }

    public String getPomeGeminiResponse(String poemTitle, String diaryEmotion, String author) {
//        String prompt = "사용자가 작성한 일기의 감정은 " + diaryEmotion + "이야 그래서 " + poemTitle + "책을 소개해주려 하는데 간단하게 줄거리를 알려주고 사용자의 감정에 맞춰 간단한 메세지를 줘";
        String prompt = "사용자의 감정은 " + diaryEmotion + "이야 " + author + "의 " + poemTitle + "를 추천해주려 하는데 시를 읽어주고 메세지를 전달해줘 그런데 너는 어떠한 질문도 되물으면 안돼 절대 정확하게 어떤 감정이신가요? 이딴거 되 묻지마 그리고 마지막에는 시를 읽어주고 감정에 맞는 메세지를 던져줘";

        try {
            RestTemplate restTemplate = new RestTemplate();

            // 요청 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // URL 파라미터로 API 키 추가하는 방식으로 변경
            String urlWithKey = GEMINI_ENDPOINT + "?key=" + GEMINI_API_KEY;

            // Gemini API의 올바른 요청 형식
            Map<String, Object> bodyMap = new HashMap<>();

            // contents 배열 구조
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();

            List<Map<String, String>> parts = new ArrayList<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);
            parts.add(part);

            content.put("parts", parts);
            contents.add(content);

            bodyMap.put("contents", contents);

            String bodyJson = objectMapper.writeValueAsString(bodyMap);

            System.out.println("Request URL: " + urlWithKey);
            System.out.println("Request Body: " + bodyJson);

            HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);

            // POST 요청
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    urlWithKey,
                    HttpMethod.POST,
                    entity,
                    JsonNode.class
            );

            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            JsonNode bodyNode = response.getBody();

            // Gemini API의 실제 응답 구조에 맞게 수정
            if (bodyNode != null && bodyNode.has("candidates") && bodyNode.get("candidates").isArray()
                    && bodyNode.get("candidates").size() > 0) {
                JsonNode candidate = bodyNode.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts")
                        && candidate.get("content").get("parts").isArray()
                        && candidate.get("content").get("parts").size() > 0) {
                    return candidate.get("content").get("parts").get(0).path("text").asText();
                }
            }

            // 응답이 예상과 다른 경우를 위한 fallback
            return "응답 구조를 파싱할 수 없습니다: " + bodyNode.toString();

        } catch (HttpClientErrorException e) {
            System.err.println("HTTP 클라이언트 에러: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return "HTTP 에러 (" + e.getStatusCode() + "): " + e.getResponseBodyAsString();
        } catch (HttpServerErrorException e) {
            System.err.println("HTTP 서버 에러: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return "서버 에러 (" + e.getStatusCode() + "): " + e.getResponseBodyAsString();
        } catch (ResourceAccessException e) {
            System.err.println("네트워크 연결 에러: " + e.getMessage());
            return "네트워크 연결 실패: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("Gemini API 호출 실패: " + e.getMessage());
            e.printStackTrace();
            return "API 호출 실패: " + e.getMessage();
        }
    }


    public String getMovieGeminiResponse(String movieTitle, String diaryEmotion, String director) {
        String prompt = "사용자가 작성한 일기의 감정은 " + diaryEmotion + "이야 그래서 " + director + "작가의 " + movieTitle + "영화를 소개해주려 하는데 간단하게 줄거리를 알려주고 사용자의 감정에 맞춰 간단한 메세지를 줘";

        try {
            RestTemplate restTemplate = new RestTemplate();

            // 요청 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // URL 파라미터로 API 키 추가하는 방식으로 변경
            String urlWithKey = GEMINI_ENDPOINT + "?key=" + GEMINI_API_KEY;

            // Gemini API의 올바른 요청 형식
            Map<String, Object> bodyMap = new HashMap<>();

            // contents 배열 구조
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();

            List<Map<String, String>> parts = new ArrayList<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);
            parts.add(part);

            content.put("parts", parts);
            contents.add(content);

            bodyMap.put("contents", contents);

            String bodyJson = objectMapper.writeValueAsString(bodyMap);

            System.out.println("Request URL: " + urlWithKey);
            System.out.println("Request Body: " + bodyJson);

            HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);

            // POST 요청
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    urlWithKey,
                    HttpMethod.POST,
                    entity,
                    JsonNode.class
            );

            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            JsonNode bodyNode = response.getBody();

            // Gemini API의 실제 응답 구조에 맞게 수정
            if (bodyNode != null && bodyNode.has("candidates") && bodyNode.get("candidates").isArray()
                    && bodyNode.get("candidates").size() > 0) {
                JsonNode candidate = bodyNode.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts")
                        && candidate.get("content").get("parts").isArray()
                        && candidate.get("content").get("parts").size() > 0) {
                    return candidate.get("content").get("parts").get(0).path("text").asText();
                }
            }

            // 응답이 예상과 다른 경우를 위한 fallback
            return "응답 구조를 파싱할 수 없습니다: " + bodyNode.toString();

        } catch (HttpClientErrorException e) {
            System.err.println("HTTP 클라이언트 에러: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return "HTTP 에러 (" + e.getStatusCode() + "): " + e.getResponseBodyAsString();
        } catch (HttpServerErrorException e) {
            System.err.println("HTTP 서버 에러: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return "서버 에러 (" + e.getStatusCode() + "): " + e.getResponseBodyAsString();
        } catch (ResourceAccessException e) {
            System.err.println("네트워크 연결 에러: " + e.getMessage());
            return "네트워크 연결 실패: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("Gemini API 호출 실패: " + e.getMessage());
            e.printStackTrace();
            return "API 호출 실패: " + e.getMessage();
        }
    }

    public String getMusicGeminiResponse(String musicTitle, String description, String musicArtist) {
        String prompt = "사용자가 작성한 일기의 감정은 " + description + "이야 그래서 " + musicArtist + "의 " + musicTitle + "노래를 소개해주려 하는데 간단하게 노래의 내용을 알려주고 사용자의 감정에 맞춰 간단한 메세지를 줘";

        try {
            RestTemplate restTemplate = new RestTemplate();

            // 요청 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // URL 파라미터로 API 키 추가하는 방식으로 변경
            String urlWithKey = GEMINI_ENDPOINT + "?key=" + GEMINI_API_KEY;

            // Gemini API의 올바른 요청 형식
            Map<String, Object> bodyMap = new HashMap<>();

            // contents 배열 구조
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();

            List<Map<String, String>> parts = new ArrayList<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);
            parts.add(part);

            content.put("parts", parts);
            contents.add(content);

            bodyMap.put("contents", contents);

            String bodyJson = objectMapper.writeValueAsString(bodyMap);

            System.out.println("Request URL: " + urlWithKey);
            System.out.println("Request Body: " + bodyJson);

            HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);

            // POST 요청
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    urlWithKey,
                    HttpMethod.POST,
                    entity,
                    JsonNode.class
            );

            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            JsonNode bodyNode = response.getBody();

            // Gemini API의 실제 응답 구조에 맞게 수정
            if (bodyNode != null && bodyNode.has("candidates") && bodyNode.get("candidates").isArray()
                    && bodyNode.get("candidates").size() > 0) {
                JsonNode candidate = bodyNode.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts")
                        && candidate.get("content").get("parts").isArray()
                        && candidate.get("content").get("parts").size() > 0) {
                    return candidate.get("content").get("parts").get(0).path("text").asText();
                }
            }

            // 응답이 예상과 다른 경우를 위한 fallback
            return "응답 구조를 파싱할 수 없습니다: " + bodyNode.toString();

        } catch (HttpClientErrorException e) {
            System.err.println("HTTP 클라이언트 에러: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return "HTTP 에러 (" + e.getStatusCode() + "): " + e.getResponseBodyAsString();
        } catch (HttpServerErrorException e) {
            System.err.println("HTTP 서버 에러: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return "서버 에러 (" + e.getStatusCode() + "): " + e.getResponseBodyAsString();
        } catch (ResourceAccessException e) {
            System.err.println("네트워크 연결 에러: " + e.getMessage());
            return "네트워크 연결 실패: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("Gemini API 호출 실패: " + e.getMessage());
            e.printStackTrace();
            return "API 호출 실패: " + e.getMessage();
        }
    }
}