package com.jaehl.gameTool.common.data.local

import com.jaehl.gameTool.common.data.model.User

interface UserLocalSource {
    suspend fun getUserSelf() : User?
    suspend fun setUserSelf(user : User)

    suspend fun getUsers() : List<User>
    suspend fun getUser(userId : Int) : User?

    suspend fun updateUser(user: User)
    suspend fun updateUsers(users : List<User>)
}

class UserLocalSourceInMemory : UserLocalSource {

    private var userSelf : User? = null
    private val userMap = hashMapOf<Int, User>()

    override suspend fun getUserSelf(): User? {
        return userSelf
    }

    override suspend fun setUserSelf(user: User) {
        userSelf = user
    }

    override suspend fun getUsers(): List<User> {
        return userMap.values.toList()
    }

    override suspend fun getUser(userId: Int): User? {
        return userMap[userId]
    }

    override suspend fun updateUser(user: User) {
        userMap[user.id] = user
    }

    override suspend fun updateUsers(users: List<User>) {
        userMap.clear()
        users.forEach { user ->
            userMap[user.id] = user
        }
    }
}