package dev.kaly7.fingest.controllers.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI expenseAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("FinGest API")
                        .description("API for FinGest Application")
                        .version("0.0.1")
                        .license(new License()
                                .name("Apache license Version 2.0")
                                .url("https://kaly7.dev")))
                .externalDocs(new ExternalDocumentation()
                        .description("Externals documentation"));
    }
}
