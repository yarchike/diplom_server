package com.martynov.dto

import com.martynov.model.AttachmentModel
import com.martynov.model.UserType

data class AutorIdeaResponseDto(
    val id: Long = 0,
    val username: String,
    val attachment: AttachmentModel? = null,
    val readOnly: Boolean = false,
    val userType: UserType = UserType.NORMAL

)