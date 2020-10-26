package com.martynov.repository

import com.martynov.model.IdeaModel
import com.martynov.model.LikeAndDislike
import com.martynov.model.UserModel

interface IdeaRepository {
    suspend fun newIdea(iteam: IdeaModel): List<IdeaModel>
    suspend fun getAll(id: Long?): List<IdeaModel>
    suspend fun like(id: Long, user: UserModel?): IdeaModel?
    suspend fun disLike(id: Long, user: UserModel?): IdeaModel?
    suspend fun getIdeaId(id: Long?): IdeaModel?
}