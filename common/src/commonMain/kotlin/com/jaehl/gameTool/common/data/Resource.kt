package com.jaehl.gameTool.common.data

sealed class Resource<out T: Any>  {
    data class Success<out T: Any>(val data: T): Resource<T>()
    data class Loading<out T: Any>(val data: T): Resource<T>()
    data class Error(val exception: Throwable): Resource<Nothing>()

    fun getDataOrThrow() : T {
        return when(this){
            is Success -> data
            is Loading -> data
            is Error -> throw this.exception
        }
    }
}