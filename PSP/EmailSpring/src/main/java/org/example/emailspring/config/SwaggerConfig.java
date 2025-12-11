package org.example.emailspring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Email Spring API")
                        .version("1.0")
                        .description("API para gestión de entrenamientos y autenticación")
                        .contact(new Contact()
                                .name("Tu nombre")
                                .email("tu@email.com")));
    }
}
