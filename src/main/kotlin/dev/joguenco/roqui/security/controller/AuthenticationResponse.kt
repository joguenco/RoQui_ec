package dev.joguenco.roqui.security.controller

data class AuthenticationResponse(val accessToken: String, val refreshToken: String)
