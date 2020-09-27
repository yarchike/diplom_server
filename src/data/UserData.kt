package com.martynov.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.martynov.model.UserModel
import java.io.File

object UserData {
    fun getDataBase(): MutableList<UserModel> {
        val fileName = "user.json"

        val type = object : TypeToken<List<UserModel>>() {}.type
        try {
            val result: MutableList<UserModel> = Gson().fromJson(File(fileName).readText(), type)

            return result

        } catch (e: Exception) {
            return mutableListOf<UserModel>()
        }


    }
}