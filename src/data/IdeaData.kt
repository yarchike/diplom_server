package com.martynov.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.martynov.FILE_IDEA
import com.martynov.model.IdeaModel
import java.io.File

object IdeaData {
    fun getDataBase(): ArrayList<IdeaModel> {
        try {
            val type = object : TypeToken<List<IdeaModel>>() {}.type
            val result: ArrayList<IdeaModel> = Gson().fromJson(File(FILE_IDEA).readText(), type)
            return result
        }
        catch (e:Exception){
            return ArrayList<IdeaModel>()
        }


    }

}