package com.martynov.dto

import com.martynov.model.AttachmentModel

data class AutorIdeaRequest(
    val id: Long? = 0,
    val username: String? = null,
    val attachment: AttachmentModel?= null
)