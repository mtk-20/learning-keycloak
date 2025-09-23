package com.example.keycloak_demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Value("${jwt.auth.converter.principal-Attribute}")
    private String principalAttribute;
    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    private final JwtGrantedAuthoritiesConverter jwtConverter = new JwtGrantedAuthoritiesConverter();


    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {

        Collection<GrantedAuthority> auth = Stream.concat(
                jwtConverter.convert(source).stream(),
                extractRoles(source).stream()
        ).collect(Collectors.toList());
        return new JwtAuthenticationToken(
                source, auth, getPrincipal(source)
        );
    }

    private String getPrincipal(Jwt source) {
        String name = JwtClaimNames.SUB;
        if (principalAttribute != null) {
            name = principalAttribute;
        }
        return source.getClaim(name);
    }

    private Collection<? extends GrantedAuthority> extractRoles(Jwt source) {
        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRole;
        if (source.getClaim("resource_access") == null) {
            return Collections.emptySet();
        }
        resourceAccess = source.getClaim("resource_access");
        if (resourceAccess.get(resourceId) == null) {
            return Collections.emptySet();
        }
        resource = (Map<String, Object>) resourceAccess.get(resourceId);
        resourceRole = (Collection<String>) resourceAccess.get("roles");
        return resourceRole.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).collect(Collectors.toList());
    }
}
