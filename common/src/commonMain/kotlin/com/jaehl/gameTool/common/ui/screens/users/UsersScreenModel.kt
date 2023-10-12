package com.jaehl.gameTool.common.ui.screens.users

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.repo.UserRepo
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.extensions.postSwap
import kotlinx.coroutines.launch

class UsersScreenModel(
    val jobDispatcher : JobDispatcher,
    val userRepo: UserRepo
) : ScreenModel {
    var users = mutableStateListOf<UserModel>()
    var pageLoading = mutableStateOf<Boolean>(false)
    val dialogConfig = mutableStateOf<DialogConfig>(DialogConfig.None)

    init {
        coroutineScope.launch {
            dataRefresh()
        }
    }

    suspend fun dataRefresh() {
        launchIo(
            jobDispatcher,
            onException = ::onException
        ){
            val users = userRepo.getUsers().map {
                UserModel(
                    id = it.id,
                    name = it.userName,
                    role = it.role.name
                )
            }
            this.users.postSwap(users)
            this.pageLoading.value = false
        }
    }

    fun onUserRoleClick(userId : Int){
        dialogConfig.value = DialogConfig.RolePickerConfig(userId)
    }

    fun closeRolePicker(){
        dialogConfig.value = DialogConfig.None
    }

    private fun onException(t: Throwable){
        System.err.println(t.message)
        pageLoading.value = false
    }

    fun changeUserRole(userId : Int, role : User.Role) = launchIo(jobDispatcher, ::onException) {
        userRepo.changeUserRole(userId = userId, role = role)
        dataRefresh()
    }

    sealed class DialogConfig {
        data object None : DialogConfig()
        data class RolePickerConfig(val userId : Int) : DialogConfig()

    }
}

