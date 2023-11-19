package com.jaehl.gameTool.common.ui.util

import com.jaehl.gameTool.common.data.Resource

fun <T : Any, R : Any> resourceMap(resource : Resource<T>, transFormer : (input : T) -> R) : Resource<R> {
    when (resource) {
        is Resource.Error -> {
            return Resource.Error(resource.exception)
        }
        is Resource.Success -> {
            return Resource.Success(transFormer(resource.data))
        }
        is Resource.Loading -> {
            return Resource.Loading(transFormer(resource.data))
        }
    }
}