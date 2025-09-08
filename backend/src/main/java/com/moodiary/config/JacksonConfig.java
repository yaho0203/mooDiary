package com.moodiary.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson 설정 클래스
 * LocalDateTime 등의 Java 8 시간 타입을 JSON 직렬화/역직렬화할 수 있도록 설정
 */
@Configuration
public class JacksonConfig {

    /**
     * ObjectMapper Bean을 생성하고 JSR310 모듈을 등록
     * @return JavaTimeModule이 등록된 ObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
