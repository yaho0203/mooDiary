package com.moodiary.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) 설정 클래스
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MooDiary API")
                        .version("1.0.0")
                        .description("MooDiary 일기 서비스 API 문서")
                        .contact(new Contact()
                                .name("MooDiary Team")
                                .email("support@moodiary.com")));
    }
}

