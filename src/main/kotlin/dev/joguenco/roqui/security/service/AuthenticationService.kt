package dev.joguenco.roqui.security.service

import dev.joguenco.roqui.security.config.JwtProperties
import dev.joguenco.roqui.security.dto.LoginDto
import dev.joguenco.roqui.security.dto.UserDto
import dev.joguenco.roqui.security.repository.RefreshTokenRepository
import java.util.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    fun authentication(authenticationRequest: UserDto): LoginDto {
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authenticationRequest.username,
                authenticationRequest.password,
            )
        )
        val user = userDetailsService.loadUserByUsername(authenticationRequest.username.toString())

        val accessToken = createAccessToken(user)
        val refreshToken = createRefreshToken(user)

        refreshTokenRepository.save(refreshToken, user)

        return LoginDto(accessToken = accessToken, refreshToken = refreshToken)
    }

    fun refreshAccessToken(refreshToken: String): String? {
        val extractedEmail = tokenService.extractEmail(refreshToken)

        return extractedEmail?.let { email ->
            val currentUserDetails = userDetailsService.loadUserByUsername(email)
            val refreshTokenUserDetails =
                refreshTokenRepository.findUserDetailsByToken(refreshToken)

            if (
                !tokenService.isExpired(refreshToken) &&
                    refreshTokenUserDetails?.username == currentUserDetails.username
            )
                createAccessToken(currentUserDetails)
            else null
        }
    }

    private fun createAccessToken(user: UserDetails) =
        tokenService.generate(userDetails = user, expirationDate = getAccessTokenExpiration())

    private fun createRefreshToken(user: UserDetails) =
        tokenService.generate(userDetails = user, expirationDate = getRefreshTokenExpiration())

    private fun getAccessTokenExpiration(): Date =
        Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)

    private fun getRefreshTokenExpiration(): Date =
        Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
}
