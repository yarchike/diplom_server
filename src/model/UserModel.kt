package com.martynov.model

import io.ktor.auth.*

data class UserModel(
    val id: Long = 0,
    val username: String,
    val password: String,
    val token: String,
    val tokenDevice: String = "",
    val attachment: AttachmentModel? = null,
    val readOnlyIdea: Boolean = false,
    val userType: UserType = UserType.NORMAL,
    val numberOfLike: Long = 0,
    val numberOfDislike: Long = 0
) : Principal