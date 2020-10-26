package com.martynov.dto

import com.martynov.model.AttachmentModel
import com.martynov.model.UserModel
import com.martynov.model.UserType

data class AutorIdeaRequest(
    val id: Long? = 0,
    val username: String? = null,
    val attachment: AttachmentModel? = null,
    val readOnlyIdea: Boolean = false,
    val userType: UserType = UserType.NORMAL
) {
    companion object {
        fun fromModel(model: UserModel) = AutorIdeaRequest(
            id = model.id,
            username = model.username,
            attachment = model.attachment,
            readOnlyIdea = model.readOnlyIdea,
            userType = model.userType
        )
    }
}
