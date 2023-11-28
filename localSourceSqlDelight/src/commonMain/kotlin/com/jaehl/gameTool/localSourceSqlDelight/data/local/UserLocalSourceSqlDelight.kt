package com.jaehl.gameTool.localSourceSqlDelight.data.local

import com.jaehl.gameTool.common.data.local.UserLocalSource
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.localSourceSqlDelight.Database

class UserLocalSourceSqlDelight(
    private val database: Database
) : UserLocalSource {

    override suspend fun getUserSelf(): User? {
        val userId = database.usersQueries.getUserSelf().executeAsOneOrNull() ?: return null
        return database.usersQueries.getUser(userId).executeAsOne().toUser()
    }

    override suspend fun setUserSelf(user: User) {
        database.usersQueries.insertUser(
            UserEntity(
                id = user.id,
                userName = user.userName,
                email = user.email,
                role = user.role.name
            )
        )
        database.usersQueries.deleteUserSelf()
        database.usersQueries.insertUserSelf(
            UserSelfEntity(
                user_id = user.id
            )
        )
    }

    override suspend fun getUsers(): List<User> {
        return database.usersQueries.getAllUsers().executeAsList().map { it.toUser() }
    }

    override suspend fun getUser(userId: Int): User? {
        return database.usersQueries.getUser(userId).executeAsOne().toUser()
    }

    override suspend fun updateUser(user: User) {
        database.usersQueries.insertUser(
            UserEntity(
                id = user.id,
                userName = user.userName,
                email = user.email,
                role = user.role.name
            )
        )
    }

    override suspend fun updateUsers(users: List<User>) {
        users.forEach {  user ->
            database.usersQueries.insertUser(
                UserEntity(
                    id = user.id,
                    userName = user.userName,
                    email = user.email,
                    role = user.role.name
                )
            )
        }
    }

    override suspend fun clear() {
        database.usersQueries.deleteAllUsers()
    }
}

fun UserEntity.toUser() : User{
    return User(
        id = id,
        userName = userName,
        email = email,
        role = User.Role.valueOf(role)
    )
}