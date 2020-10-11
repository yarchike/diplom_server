package com.martynov.repository

import com.google.gson.Gson
import com.martynov.FILE_IDEA
import com.martynov.data.IdeaData
import com.martynov.exception.ActionProhibitedException
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
            iteams.add(iteam.copy(id = iteams.size.toLong()))
            File(FILE_IDEA).writeText(Gson().toJson(iteams))
            iteams
        }

    override suspend fun getAll(id: Long?): List<IdeaModel> =
        mutex.withLock {
            var ideaIteam = ArrayList<IdeaModel>()

            for (idea in iteams) {
                idea.isLike = false
                idea.isDisLike = false

                if(id in idea.ideaIsLike){
                    idea.isLike = true
                }
                if( id in idea.ideaIsDisLike){
                    idea.isDisLike = true
                }

                ideaIteam.add(idea)

            }
            ideaIteam.reversed()
        }

    override suspend fun like(id: Long, userId: Long?): IdeaModel? =
        mutex.withLock {
            val index = iteams.indexOfFirst { it.id == id }
            if (index < 0) {
                return@withLock null
            }
            val idea = iteams[index]
            if (idea.ideaIsLike.contains(userId)) {
                return throw ActionProhibitedException("действие запрешено")
            }
            val likeIsIdea = idea.ideaIsLike
            if (userId != null) {
                likeIsIdea.add(userId)
            }
            val newIdea = idea.copy(like = idea.like.inc(), ideaIsLike = likeIsIdea, isLike = true)
            iteams[index] = newIdea
            File(FILE_IDEA).writeText(Gson().toJson(iteams))
            newIdea
        }

    override suspend fun disLike(id: Long, userId: Long?): IdeaModel? =
        mutex.withLock {
            val index = iteams.indexOfFirst { it.id == id }
            if (index < 0) {
                return@withLock null
            }
            val idea = iteams[index]
            if (idea.ideaIsDisLike.contains(userId)) {
                return throw ActionProhibitedException("действие запрешено")
            }
            val disLikeIsIdea = idea.ideaIsDisLike
            if (userId != null) {
                disLikeIsIdea.add(userId)
            }
            val newIdea = idea.copy(disLike = idea.like.inc(), ideaIsLike = disLikeIsIdea, isDisLike = true)
            iteams[index] = newIdea
            File(FILE_IDEA).writeText(Gson().toJson(iteams))
            newIdea
        }


}