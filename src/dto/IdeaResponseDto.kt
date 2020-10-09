package com.martynov.dto

import com.martynov.model.AttachmentModel

data class IdeaResponseDto(
    val id: Long = 0,
    val date: Long = 0,
    val ideaText:String,
    val attachment: AttachmentModel? = null
)