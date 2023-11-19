package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.ui.util.UiException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepoMock : UserRepo{

    var userSelf : User? = null
    var users : ArrayList<User> = arrayListOf()
    var getUsersError : Throwable? = null
    var getUserError : Throwable? = null

    var loginError : Throwable? = null
    var registerError : Throwable? = null

    override suspend fun login(userName: String, password: String) {
        loginError?.let { throw it }
    }

    override suspend fun register(userName: String, email: String, password: String) {

    }

    override suspend fun getUserSelFlow(): Flow<Resource<User>> = flow {
        val userSelf = this@UserRepoMock.userSelf
        if(userSelf == null){
            emit(Resource.Error(Exception("")))
        }
        else {
            emit(Resource.Success(userSelf))
        }
    }

    override suspend fun getUserFlow(userId: Int): Flow<Resource<User>> = flow {
        val getUsersError = this@UserRepoMock.getUsersError
        if(getUsersError != null){
            emit(Resource.Error(getUsersError))
        }
        else {
            val user = users.firstOrNull { it.id == userId }
            if(user == null){
                emit(Resource.Error(UiException.NotFound("user not found")))
            }
            else {
                emit(Resource.Success(user))
            }
        }
    }

    override suspend fun getUsersFlow(): Flow<Resource<List<User>>> = flow {
        val usersError = this@UserRepoMock.getUsersError
        if(usersError != null){
            emit(Resource.Error(usersError))
        }
        else {
            emit(Resource.Success(users))
        }
    }

    override suspend fun changeUserRole(userId: Int, role: User.Role): User {
        val userIndex = users.indexOfFirst { it.id == userId }
        if(userId == -1) throw UiException.NotFound("user not found : $userId")
        users[userIndex].let {
            users[userIndex] = it.copy(
                role = role
            )
            return users[userIndex]
        }
    }

    override suspend fun changePassword(userId: Int, password: String) {

    }

    override suspend fun clearData() {
        userSelf = null
        users = arrayListOf()
    }

    fun clear() {
        userSelf = null
        users = arrayListOf()
        loginError = null
        registerError = null
        getUsersError = null
        getUserError = null
    }
}