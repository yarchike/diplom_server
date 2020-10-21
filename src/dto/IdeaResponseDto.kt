package com.martynov.dto

import com.martynov.model.AttachmentModel
import com.martynov.model.IdeaModel
import com.martynov.model.LikeAndDislike

data class IdeaResponseDto(
    val id: Long = 0,
    val date: Long = 0,
    val ideaText:String,
    val attachment: AttachmentModel? = null,
    val like: Long,
    val disLike: Long,
    val ideaIsLike: ArrayList<LikeAndDislike> = ArrayList(),
    //val ideaIsDisLike: ArrayList<LikeAndDislike> = ArrayList(),
    var isLike:Boolean = false,
    var isDisLike:Boolean = false,
    val url:String =""
) {
    companion object{
        fun fromModel(model: IdeaModel)  =IdeaResponseDto (
         id = model.id,
         date= model.date,
         ideaText= model.ideaText,
         attachment= model.attachment,
         like= model.like,
         disLike= model.disLike,
         ideaIsLike= model.ideaIsLike,
         isLike= model.isLike,
         isDisLike= model.isDisLike,
         url= model.url
        )
    }
}