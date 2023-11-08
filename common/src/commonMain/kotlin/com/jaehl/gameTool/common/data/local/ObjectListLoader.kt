package com.jaehl.gameTool.common.data.local

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.lang.reflect.Type
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.exists


interface ObjectListLoader<T> {
    fun load() : List<T> //type IS "object : TypeToken<Array<CLASS>>() {}.type"
    fun save(objects : List<T>) : Boolean
}

class  ObjectListJsonLoader<T>(
    //private val logger: Logger,
    //using Type as Gson can not use a generic as it's compiled to object at runtime
    private val type : Type, //type is "object : TypeToken<Array<CLASS>>() {}.type"
    private val projectUserDir : String,
    private val listFilePath : String
) : ObjectListLoader<T> {

    private fun getFile() : File {
        val directory = Paths.get(System.getProperty("user.home"), projectUserDir)
        if( !directory.exists()){
            directory.createDirectories()
        }

        val path = Paths.get(System.getProperty("user.home"),
            projectUserDir,
            listFilePath
        )
        return path.toFile()
    }

    override fun load() : List<T> {
        val file = getFile()

        if(!file.exists()) {
            println("ERROR : $listFilePath does not exist\n${file.absoluteFile} ")
            return listOf()
        }
        val gson = Gson().newBuilder().create()
        val fileString = file.inputStream().readBytes().toString(Charsets.UTF_8)
        return gson.fromJson<Array<T>>(fileString, type).toList()
    }

    override fun save(data : List<T>) : Boolean {
        return try {
            val file = getFile()
            println("file : ${file.absoluteFile}")
            file.createNewFile()

            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonString = gson.toJson(data)
            file.writeText(jsonString, Charsets.UTF_8)
            true
        } catch (t : Throwable){
            //logger.error("ObjectListJsonLoader "+t.message)
            false
        }
    }
}