package com.martynov.repository

import com.martynov.model.UserModel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UserRepositoryInMemoryWithMutexImpl : UserRepository {
    private val iteams = mutableListOf<UserModel>()
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

}