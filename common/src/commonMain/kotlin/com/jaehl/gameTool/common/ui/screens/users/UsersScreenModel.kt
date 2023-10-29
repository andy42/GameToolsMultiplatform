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
import com.jaehl.gameTool.common.ui.screens.home.HomeScreenModel
import kotlinx.coroutines.launch

class UsersScreenModel(
    val jobDispatcher : JobDispatcher,
    val userRepo: UserRepo
) : ScreenModel {
    var users = mutableStateListOf<UserModel>()
    var pageLoading = mutableStateOf<Boolean>(false)
    val dialogConfig = mutableStateOf<DialogConfig>(DialogConfig.ClosedDialog)

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
                    email = it.email,
                    role = it.role.name
                )
            }
            this.users.postSwap(users)
            this.pageLoading.value = false
        }
    }

    fun closeDialog(){
        dialogConfig.value = DialogConfig.ClosedDialog
    }

    private fun onException(t: Throwable){
        System.err.println(t.message)
        pageLoading.value = false
    }

    sealed class DialogConfig {
        data object ClosedDialog : DialogConfig()
        data class ErrorDialog(
            val title : String,
            val message : String
        ) : DialogConfig()
    }
}

