package com.martynov.repository

import com.martynov.dto.AutorIdeaRequest
import com.martynov.model.LikeAndDislike
import com.martynov.model.UserModel

interface UserRepository {
    suspend fun getById(id: Long): UserModel?
    suspend fun getByUsername(username: String): UserModel?
    suspend fun save(iteam: UserModel): UserModel
    suspend fun addUser(iteam: UserModel): Boolean
    fun getSizeListUser(): Int
    suspend fun getUsersBy(itemLikeDislik: ArrayList<LikeAndDislike>):ArrayList<AutorIdeaRequest>
}