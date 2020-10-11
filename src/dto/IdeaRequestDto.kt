package com.martynov.dto

import com.martynov.model.AttachmentModel

data class IdeaRequestDto (
    val id: Long = 0,
    val  autor: AutorIdeaRequest,
    val date: Long = 0,
    val ideaText:String,
    val attachment: AttachmentModel? = null,
    val like: Long,
    val disLike: Long,
    val ideaIsLike: ArrayList<Long> = ArrayList(),
    val ideaIsDisLike: ArrayList<Long> = ArrayList(),
    var isLike:Boolean = false,
    var isDisLike:Boolean = false
)