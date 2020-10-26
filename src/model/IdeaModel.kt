package com.martynov.model

import com.martynov.dto.AutorIdeaRequest

data class IdeaModel(
    val id: Long = 0,
    val autor: AutorIdeaRequest,
    val date: Long = 0,
    val ideaText: String,
    val attachment: AttachmentModel? = null,
    val like: Long = 0,
    val disLike: Long = 0,
    val ideaIsLike: ArrayList<LikeAndDislike> = ArrayList(),
    var isLike: Boolean = false,
    var isDisLike: Boolean = false,
    val url: String = ""
)