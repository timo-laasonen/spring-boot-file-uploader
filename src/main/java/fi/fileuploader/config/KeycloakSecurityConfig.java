package fi.fileuploader.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableMethodSecurity
@ConditionalOnProperty(
    name = "file-uploader.keycloak.enabled",
    havingValue = "true",
    matchIfMissing = true
)
@Profile("!testing")
public class KeycloakSecurityConfig {

    @Value("${file-uploader.keycloak.urls}")
    private List<String> keyCloakUrls;

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver() {
        final Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();

        keyCloakUrls
            .forEach(issuer -> {
                final JwtAuthenticationProvider authenticationProvider =
                    new JwtAuthenticationProvider(
                        JwtDecoders.fromOidcIssuerLocation(issuer)
                    );
                authenticationProvider.setJwtAuthenticationConverter(
                    this.getJwtAuthenticationConverter()
                );
                authenticationManagers.put(issuer, authenticationProvider::authenticate);
            });

        return new JwtIssuerAuthenticationManagerResolver(authenticationManagers::get);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
            .cors()
            .and()
            .authorizeHttpRequests(auth ->
                auth.requestMatchers(
                        "/api/health-check",
                        "/api/frontend-config"
                    )
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            )
            .oauth2ResourceServer(oauth2ResourceServer ->
                oauth2ResourceServer
                    .authenticationManagerResolver(
                        this.authenticationManagerResolver()));

        return http.build();
    }

    Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
        final JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(
            new DelegatingJwtGrantedAuthoritiesConverter(this.roles())
        );
        return converter;
    }

    private JwtGrantedAuthoritiesConverter roles() {
        final JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix("ROLE_");
        converter.setAuthoritiesClaimName("roles");
        return converter;
    }
}
