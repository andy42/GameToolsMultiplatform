package com.jaehl.gameTool.common.data.service

import com.jaehl.gameTool.common.data.model.UserTokens
import com.jaehl.gameTool.common.data.model.User

interface UserService {
    suspend fun login(userName : String, password : String) : UserTokens
    suspend fun refreshTokens(refreshToken : String) : UserTokens
    suspend fun register(userName : String, email : String, password : String) : UserTokens
    suspend fun getSelf(bearerToken: String) : User
    suspend fun getUsers(bearerToken: String) : List<User>
    suspend fun changeUserRole(bearerToken: String, userId : Int, role: User.Role) : User
}