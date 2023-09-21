package com.jaehl.gameTool.common.ui.screens.collectionDetails

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.ItemRecipeNode
import com.jaehl.gameTool.common.data.repo.CollectionRepo
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.extensions.toItemModel
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel

class CollectionDetailsScreenModel (
    val jobDispatcher : JobDispatcher,
    val collectionRepo : CollectionRepo,
    val itemRepo : ItemRepo,
    val appConfig : AppConfig,
    val authProvider: AuthProvider

) : ScreenModel {

    var title = mutableStateOf("")
    var groups = mutableStateListOf<GroupsViewModel>()

    private lateinit var config : Config

    fun setup(config : Config) {
        this.config = config

        launchIo(
            jobDispatcher = jobDispatcher,
            onException = {
                System.err.println(it.message)
            }
        ) {
            collectionRepo.getCollectionFlow(config.collectionId).collect{ collection ->
                title.value = collection.name
                groups.postSwap(
                    collection.groups.map { group ->
                        group.toGroupsViewModel(
                            itemRepo,
                            appConfig,
                            authProvider
                        )
                    }
                )
            }
        }
    }

    data class Config(
        val gameId : Int,
        val collectionId : Int
    )

    data class GroupsViewModel(
        val id : Int,
        val name : String,
        var collapseIngredientList : Boolean,
        var showBaseCrafting : Boolean,
        val itemList : List<ItemAmountViewModel>,
        val nodes : List<ItemRecipeNode>,
        val baseNodes : List<ItemRecipeNode>
    )
}

fun Collection.Group.toGroupsViewModel(
    itemRepo : ItemRepo,
    appConfig : AppConfig,
    authProvider: AuthProvider
) : CollectionDetailsScreenModel.GroupsViewModel {
    return CollectionDetailsScreenModel.GroupsViewModel(
        id = this.id,
        name = this.name,
        collapseIngredientList =  false,
        showBaseCrafting = false,
        itemList = this.itemAmounts.map {
            ItemAmountViewModel(
                itemModel = itemRepo.getItem(it.itemId)?.toItemModel(appConfig, authProvider) ?: throw Exception("Item Not Found : ${it.itemId}"),
                amount = it.amount
            )
        },
        nodes = listOf(),
        baseNodes = listOf()
    )
}