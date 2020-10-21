package com.martynov.service

import com.martynov.FILE_LOG
import com.martynov.dto.AutorIdeaRequest
import com.martynov.dto.IdeaResponseDto
import com.martynov.model.IdeaModel
import com.martynov.model.UserModel
import com.martynov.repository.IdeaRepository
import com.martynov.repository.UserRepository
import io.ktor.features.*
import java.io.File

class IdeaService(
    val repoUser: UserRepository,
    val repo: IdeaRepository,
) {
    suspend fun getNewIdea(idea: IdeaResponseDto, user: UserModel?): List<IdeaModel> {
        val autor = AutorIdeaRequest(id = user?.id, username = user?.username, attachment = user?.attachment)
        val response = repo.newIdea(
            IdeaModel(
                autor = autor,
                date = idea.date,
                ideaText = idea.ideaText,
                attachment = idea.attachment,
                like = idea.like,
                disLike = idea.disLike,
                url = idea.url
            )
        ) ?: throw NotFoundException()
        return response
    }

    suspend fun dislike(id: Long, user: UserModel?): IdeaModel? {
        val idea = repo.disLike(id, user) ?: throw NotFoundException()
        if (idea.disLike > 99 && idea.like == 0L) {
            File(FILE_LOG).appendText("Сработало + \n")
            repoUser.userToReadOnly(idea.autor.id)
        }
        return idea
    }

    suspend fun like(id: Long, user: UserModel?): IdeaModel? {
        val idea = repo.like(id, user) ?: throw NotFoundException()
        repoUser.userNotReadOnly(idea.autor.id)
        return idea
    }

    suspend fun getAllIdea(id: Long?): List<IdeaModel> {
        val newIdeaList = ArrayList<IdeaModel>()
        val listIdea = repo.getAll(id)
        val userList = repoUser.getAllUser()
        for (idea in listIdea) {
            val index = userList.indexOfFirst { idea.autor.id == it.id }
            if (index > -1) {
                newIdeaList.add(idea.copy(autor = AutorIdeaRequest.fromModel(userList[index])))
            } else {
                newIdeaList.add(idea)
            }
        }
        return newIdeaList
    }

    suspend fun getCountIdea(id: Long?, idEndIdea: Long): List<IdeaModel> {
        val listIdea = repo.getAll(id)
        if (idEndIdea == -1L) {
            return listIdea.take(20)

        } else {
            val listTempCount = listIdea.filter {
                it.id < idEndIdea
            }
            val newIdeaList = ArrayList<IdeaModel>()
            val userList = repoUser.getAllUser()
            for (idea in listTempCount.takeLast(20)) {
                val index = userList.indexOfFirst { idea.autor.id == it.id }
                if (index > -1) {
                    newIdeaList.add(idea.copy(autor = AutorIdeaRequest.fromModel(userList[index])))
                } else {
                    newIdeaList.add(idea)
                }
            }

            return newIdeaList
        }
    }

    suspend fun getIdeaId(id: Long?): IdeaModel? {
        val idea = repo.getIdeaId(id)
        val userList = repoUser.getAllUser()
        val index = userList.indexOfFirst { idea?.autor?.id == it.id }
        return idea?.copy(autor = AutorIdeaRequest.fromModel(userList[index]))

    }
}

