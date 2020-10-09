package com.martynov.repository

import com.google.gson.Gson
import com.martynov.FILE_IDEA
import com.martynov.data.IdeaData
import com.martynov.model.IdeaModel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class IdeaRepositoryMutex : IdeaRepository {
    private var nextId = 1L
    private val iteams = IdeaData.getDataBase()
    private val mutex = Mutex()
    override suspend fun newIdea(iteam: IdeaModel): List<IdeaModel> =
        mutex.withLock {
            iteams.add(iteam)
            File(FILE_IDEA).writeText(Gson().toJson(iteams))
            iteams
        }

    override suspend fun getAll(): List<IdeaModel> =
        mutex.withLock {
            iteams.reversed()
        }


}