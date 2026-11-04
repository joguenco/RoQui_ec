package dev.joguenco.roqui.security

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.Scopes
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Publishes the Keycloak OAuth2 authorization-code scheme in the OpenAPI document, so the Swagger UI
 * "Authorize" button can sign in against Keycloak and send the issued access token with each call.
 */
@Configuration
class OpenApiConfig(
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") private val issuerUri: String,
    @Value("\${app.version}") private val appVersion: String,
) {

    @Bean
    fun openApi(): OpenAPI {
        val realmUrl = issuerUri.trimEnd('/')

        return OpenAPI()
            .info(Info().title("RoQui E-Invoicing API").version(appVersion))
            .components(
                Components().addSecuritySchemes(SECURITY_SCHEME, keycloakSecurityScheme(realmUrl))
            )
            .addSecurityItem(SecurityRequirement().addList(SECURITY_SCHEME))
    }

    private fun keycloakSecurityScheme(realmUrl: String): SecurityScheme =
        SecurityScheme()
            .type(SecurityScheme.Type.OAUTH2)
            .flows(
                OAuthFlows()
                    .authorizationCode(
                        OAuthFlow()
                            .authorizationUrl("$realmUrl/protocol/openid-connect/auth")
                            .tokenUrl("$realmUrl/protocol/openid-connect/token")
                            .scopes(Scopes().addString("openid", "OpenID Connect"))
                    )
            )

    private companion object {
        const val SECURITY_SCHEME = "keycloak"
    }
}
