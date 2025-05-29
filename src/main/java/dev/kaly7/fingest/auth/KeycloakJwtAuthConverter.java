package dev.kaly7.fingest.auth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Converts JWT token claims from Keycloak to Spring Security authorities
 */
@Component
public class KeycloakJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";
    private static final String CLIENT_ID = "fingest-client";

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractResourceRoles(jwt);
        authorities.addAll(jwtGrantedAuthoritiesConverter.convert(jwt));
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
        if (resourceAccess == null || resourceAccess.isEmpty()) {
            return Collections.emptyList();
        }

        if (resourceAccess.get(CLIENT_ID) == null) {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(CLIENT_ID);
        if (clientAccess == null || !clientAccess.containsKey(ROLES)) {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        Collection<String> roles = (Collection<String>) clientAccess.get(ROLES);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
