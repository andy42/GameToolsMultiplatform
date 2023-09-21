package com.jaehl.gameTool.common.ui.screens.itemList

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.ui.componets.ImageResource
import kotlinx.coroutines.launch

class ItemListScreenModel(
    val jobDispatcher : JobDispatcher,
    val authProvider: AuthProvider,
    val config : Config,
    val appConfig: AppConfig,
    val itemRepo: ItemRepo
) : ScreenModel {

    var pageLoading = mutableStateOf<Boolean>(false)
        private set

    var items = mutableStateListOf<ItemRowModel>()
        private set

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
            itemRepo.getItems(config.gameId).collect { newItems ->
                this.items.postSwap(
                    newItems.map {item ->
                        item.toItemRowModel(appConfig, authProvider)
                    }
                )
            }
//            val items = itemRepo.getItems(config.gameId).map {
//                ItemRowModel(
//                    id = it.id,
//                    name = it.name,
//                    imageResource = ImageResource.ImageLocalResource("")
//                )
//            }
//            this.items.postSwap(items)
            this.pageLoading.value = false
        }
    }

    private fun onException(t: Throwable){
        System.err.println(t.message)
        pageLoading.value = false
    }

    data class Config(
        val gameId : Int
    )
}

fun Item.toItemRowModel(appConfig: AppConfig, authProvider: AuthProvider) : ItemRowModel {
    return ItemRowModel(
        id = this.id,
        name = this.name,
        imageResource = ImageResource.ImageApiResource(
            url = "${appConfig.baseUrl}/images/${this.image}",
            authHeader = authProvider.getBearerToken()
        )
    )
}