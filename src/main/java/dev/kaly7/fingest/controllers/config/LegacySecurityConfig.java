package dev.kaly7.fingest.controllers.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

// This class is superseded by dev.kaly7.fingest.auth.SecurityConfig
public class LegacySecurityConfig {

    // Remove @Bean annotation to avoid conflict with auth.SecurityConfig
    public SecurityFilterChain legacySecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/v3/api-docs.yaml"
                        ).permitAll()  // ✅ Allow Swagger UI access without authentication
                        .anyRequest().authenticated()  // 🔒 Protect other endpoints
                )
                .formLogin(withDefaults()) // Keep login authentication
                .csrf(AbstractHttpConfigurer::disable); // Disable CSRF if needed for testing

        return http.build();
    }
}
