package dev.joguenco.roqui.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

/**
 * Maps Keycloak realm roles (the `realm_access.roles` claim) of a JWT to Spring Security
 * authorities.
 *
 * Each Keycloak role is prefixed with `ROLE_` and uppercased, so a Keycloak role named `user`
 * becomes `ROLE_USER` and can be checked with `hasRole("USER")`.
 */
class KeycloakRoleConverter : Converter<Jwt, Collection<GrantedAuthority>> {

    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        val realmAccess = jwt.claims["realm_access"] as? Map<*, *> ?: return emptyList()
        val roles = realmAccess["roles"] as? Collection<*> ?: return emptyList()

        return roles.filterIsInstance<String>().map {
            SimpleGrantedAuthority("ROLE_${it.uppercase()}")
        }
    }
}
