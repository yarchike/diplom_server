package com.martynov.model

import com.martynov.dto.AutorIdeaRequest

data class IdeaModel (
    val id: Long = 0,
    val autor: AutorIdeaRequest,
    val date: Long = 0,
    val ideaText:String,
    val attachment: AttachmentModel? = null
)