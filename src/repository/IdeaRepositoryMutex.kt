package com.martynov.repository

import com.google.gson.Gson
import com.martynov.FILE_IDEA
import com.martynov.FILE_IDEA3
import com.martynov.data.IdeaData
import com.martynov.dto.AutorIdeaRequest
import com.martynov.exception.ActionProhibitedException
import com.martynov.model.IdeaModel
import com.martynov.model.LikeAndDislike
import com.martynov.model.TypeLikeDisLike
import com.martynov.model.UserModel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

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
                for (likeDislike in idea.ideaIsLike) {
                    if (likeDislike.autor.id == id && likeDislike.type == TypeLikeDisLike.LIKE) {
                        idea.isLike = true
                    }
                    if (likeDislike.autor.id == id && likeDislike.type == TypeLikeDisLike.DISLIKE) {
                        idea.isDisLike = true
                    }
                }
                ideaIteam.add(idea)

            }
            ideaIteam.reversed()
        }

    override suspend fun like(id: Long, user: UserModel?): IdeaModel? =
        mutex.withLock {
            val index = iteams.indexOfFirst { it.id == id }
            if (index < 0) {
                return@withLock null
            }
            val idea = iteams[index]
            for(likeDislike in idea.ideaIsLike){
                if(likeDislike.autor.id == user?.id && likeDislike.type == TypeLikeDisLike.LIKE){
                    return throw ActionProhibitedException("действие запрешено")
                }
            }
            val likeIsIdea = idea.ideaIsLike
            if (user != null) {
                likeIsIdea.add(LikeAndDislike(AutorIdeaRequest(id = user.id, username = user.username, attachment = user.attachment), Calendar.getInstance().timeInMillis, TypeLikeDisLike.LIKE))
            }
            val newIdea = idea.copy(like = idea.like.inc(), ideaIsLike = likeIsIdea, isLike = true)
            iteams[index] = newIdea
            File(FILE_IDEA).writeText(Gson().toJson(iteams))
            newIdea
        }

    override suspend fun disLike(id: Long, user: UserModel?): IdeaModel? =
        mutex.withLock {
            val index = iteams.indexOfFirst { it.id == id }
            if (index < 0) {
                return@withLock null
            }
            val idea = iteams[index]
            for(likeDislike in idea.ideaIsLike){
                if(likeDislike.autor.id == user?.id && likeDislike.type == TypeLikeDisLike.DISLIKE){
                    return throw ActionProhibitedException("действие запрешено")
                }
            }
            val likeIsIdea = idea.ideaIsLike
            if (user != null) {
                likeIsIdea.add(LikeAndDislike(AutorIdeaRequest(id = user.id, username = user.username, attachment = user.attachment), Calendar.getInstance().timeInMillis, TypeLikeDisLike.DISLIKE))
            }
            val newIdea = idea.copy(disLike = idea.disLike.inc(), ideaIsLike = likeIsIdea, isDisLike = true)
            iteams[index] = newIdea
            File(FILE_IDEA).writeText(Gson().toJson(iteams))
            newIdea
        }


}