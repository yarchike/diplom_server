package com.martynov.repository

import com.google.gson.Gson
import com.martynov.FILE_USER
import com.martynov.data.UserData
import com.martynov.dto.AutorIdeaRequest
import com.martynov.model.AttachmentModel
import com.martynov.model.LikeAndDislike
import com.martynov.model.UserModel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class UserRepositoryInMemoryWithMutexImpl : UserRepository {
    private val iteams = UserData.getDataBase()
    private val mutex = Mutex()


    override suspend fun getById(id: Long): UserModel? {
        mutex.withLock {
            return iteams.find { it.id == id }
        }
    }

    override suspend fun getByUsername(username: String): UserModel? {
        mutex.withLock {
            return iteams.find { it.username == username }
        }
    }

    override suspend fun save(iteam: UserModel): UserModel {
        mutex.withLock {
            return when (val index = iteams.indexOfFirst { it.id == iteam.id }) {
                -1 -> {
                    val copy = iteam.copy(id = iteams.size.toLong())
                    iteams.add(copy)
                    File(FILE_USER).writeText(Gson().toJson(iteams))
                    copy
                }
                else -> {
                    val copy = iteams[index].copy(username = iteam.username, password = iteam.password)
                    iteams.add(index, copy)
                    File(FILE_USER).writeText(Gson().toJson(iteams))
                    copy
                }
            }
        }
    }

    override suspend fun addUser(iteam: UserModel): Boolean {
        mutex.withLock {
            return when (val index = iteams.indexOfFirst { it.username == iteam.username }) {
                -1 -> {
                    val copy = iteam.copy(id = iteams.size.toLong(), readOnlyIdea = false)
                    iteams.add(copy)
                    File(FILE_USER).writeText(Gson().toJson(iteams))
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun getSizeListUser(): Int {
        return iteams.size
    }

    override suspend fun getUsersBy(itemLikeDislik: ArrayList<LikeAndDislike>): ArrayList<AutorIdeaRequest> {
        mutex.withLock {
            val users = ArrayList<AutorIdeaRequest>()
            for (likeDislik in itemLikeDislik) {
                val index = iteams.indexOfFirst { it.id == likeDislik.autor.id }
                val user = iteams[index]
                users.add(AutorIdeaRequest(id = user.id, username = user.username, attachment = user.attachment))
            }
            return users
        }
    }

    override suspend fun userToReadOnly(id: Long?) {
        mutex.withLock {
            val index = iteams.indexOfFirst { it.id == id }
            val user = iteams[index]
            val copyUser = user.copy(readOnlyIdea = true)
            iteams[index] = copyUser
            File(FILE_USER).writeText(Gson().toJson(iteams))
        }

    }

    override suspend fun userNotReadOnly(id: Long?) {
        mutex.withLock {
            val index = iteams.indexOfFirst { it.id == id }
            val user = iteams[index]
            val copyUser = user.copy(readOnlyIdea = false)
            iteams[index] = copyUser
            File(FILE_USER).writeText(Gson().toJson(iteams))
        }
    }

    override suspend fun userChangeImg(id: Long, attachmentModel: AttachmentModel):Boolean {
        mutex.withLock {
            val index = iteams.indexOfFirst { it.id == id }
            val user = iteams[index]
            val copy = user.copy(attachment = attachmentModel)
            iteams[index] = copy
            File(FILE_USER).writeText(Gson().toJson(iteams))
        }
        return true
    }

    override suspend fun getAllUser(): List<UserModel> {
        return iteams
    }

    override suspend fun addTokenDevice(id: Long?, tokenDevice: String): UserModel {
        mutex.withLock {
            val index = iteams.indexOfFirst { it.id == id }
            val copyUser = iteams[index].copy(tokenDevice = tokenDevice)
            iteams[index] = copyUser
            File(FILE_USER).writeText(Gson().toJson(iteams))
            return copyUser
        }
    }

    override fun findTokenDevice(id: Long?): String {
        val index = iteams.indexOfFirst { it.id == id }
        return iteams[index].tokenDevice
    }


}