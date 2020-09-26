package com.martynov.model

import io.ktor.auth.*

data class UserModel(
    val id: Long = 0,
    val username: String,
    val password: String
): Principal