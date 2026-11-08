package dev.joguenco.roqui.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource

/**
 * OAuth2 resource server configuration that validates Keycloak-issued access tokens (JWT) and turns
 * Keycloak realm roles into Spring Security authorities.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(@Value("\${app.cors.allowed-origin}") private val allowedOrigin: String) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/ping",
                        "/docs",
                        "/docs/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                    )
                    .permitAll()
                auth.anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter())
                }
            }

        return http.build()
    }

    private fun keycloakJwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter(KeycloakRoleConverter())

        return converter
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.addAllowedOrigin(allowedOrigin)
        configuration.addAllowedMethod("GET")
        configuration.addAllowedMethod("POST")
        configuration.addAllowedMethod("PUT")
        configuration.addAllowedMethod("DELETE")
        configuration.addAllowedHeader("Authorization")
        configuration.addAllowedHeader("Content-Type")
        return CorsConfigurationSource { configuration }
    }
}
