package com.giao.devlogpulse.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类
 * 
 * @author giao
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DevLogPulse API 文档")
                        .description("开发日志管理系统 API 接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("giao")
                                .email("your-email@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}