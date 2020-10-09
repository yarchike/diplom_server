package com.martynov.repository

import com.martynov.model.IdeaModel

interface IdeaRepository {
    suspend fun newIdea(iteam: IdeaModel): List<IdeaModel>
    suspend fun getAll(): List<IdeaModel>
}