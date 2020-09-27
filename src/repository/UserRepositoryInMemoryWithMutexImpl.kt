package com.martynov.repository

import com.google.gson.Gson
import com.martynov.data.UserData
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
                    copy
                }
                else -> {
                    val copy = iteams[index].copy(username = iteam.username, password = iteam.password)
                    iteams.add(index, copy)
                    copy
                }
            }
        }
    }

    override suspend fun addUser(iteam: UserModel): Boolean {
        mutex.withLock {
            return when (val index = iteams.indexOfFirst { it.username == iteam.username }) {
                -1 -> {
                    val copy = iteam.copy(id = iteams.size.toLong())
                    iteams.add(copy)
                    val fileName = "user.json"
                    File(fileName).writeText(Gson().toJson(iteams))
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

}