package com.martynov.dto

import com.martynov.model.AttachmentModel

data class AutorIdeaResponseDto(
    val id: Long = 0,
    val username: String,
    val attachment: AttachmentModel?= null

)