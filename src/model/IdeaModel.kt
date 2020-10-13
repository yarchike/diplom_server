package com.martynov.model

import com.martynov.dto.AutorIdeaRequest

data class IdeaModel(
    val id: Long = 0,
    val autor: AutorIdeaRequest,
    val date: Long = 0,
    val ideaText: String,
    val attachment: AttachmentModel? = null,
    val like: Long,
    val disLike: Long,
    val ideaIsLike: ArrayList<LikeAndDislike> = ArrayList(),
    //val ideaIsDisLike: ArrayList<Long> = ArrayList(),
    var isLike:Boolean = false,
    var isDisLike:Boolean = false
)