package com.martynov.service

import org.springframework.security.crypto.password.PasswordEncoder

class UserService(
    private val repo: UserService,
    private val tokenService: JWTTokenService,
    private val passwordEncoder: PasswordEncoder
) {
}