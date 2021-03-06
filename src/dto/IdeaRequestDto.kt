package com.martynov.dto

import com.martynov.model.AttachmentModel
import com.martynov.model.LikeAndDislike

data class IdeaRequestDto(
    val id: Long = 0,
    val autor: AutorIdeaRequest,
    val date: Long = 0,
    val ideaText: String,
    val attachment: AttachmentModel? = null,
    val like: Long,
    val disLike: Long,
    val ideaIsLike: ArrayList<LikeAndDislike> = ArrayList(),
    //val ideaIsDisLike: ArrayList<LikeAndDislike> = ArrayList(),
    var isLike: Boolean = false,
    var isDisLike: Boolean = false,
    val url: String = ""
)