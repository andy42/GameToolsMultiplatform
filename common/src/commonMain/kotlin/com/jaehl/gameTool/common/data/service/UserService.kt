package com.jaehl.gameTool.common.data.service

import com.jaehl.gameTool.common.data.model.AccessToken
import com.jaehl.gameTool.common.data.model.User

interface UserService {
    fun login(email : String, password : String) : AccessToken
    fun register(email : String, password : String) : AccessToken
    fun getSelf(bearerToken: String) : User
    fun getUsers(bearerToken: String) : List<User>
}