package com.martynov.repository

import com.martynov.model.IdeaModel

interface IdeaRepository {
    suspend fun newIdea(iteam: IdeaModel): List<IdeaModel>
    suspend fun getAll(id: Long?): List<IdeaModel>
    suspend fun like(id: Long, userId : Long?):IdeaModel?
    suspend fun disLike(id: Long, userId : Long?):IdeaModel?
}