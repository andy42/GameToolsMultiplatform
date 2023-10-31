package com.jaehl.gameTool.common.ui.screens.collectionList

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.repo.CollectionRepo
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.ui.screens.launchIo

class CollectionListScreenModel (
    val jobDispatcher : JobDispatcher,
    val collectionRepo : CollectionRepo
) : ScreenModel {

    private lateinit var config : Config
    var pageLoading = mutableStateOf(false)

    var collections = mutableStateListOf<CollectionViewModel>()

    val dialogConfig = mutableStateOf<DialogConfig>(DialogConfig.Closed)

    fun setup(config : Config) {
        this.config = config

        loadCollection()
    }

    fun loadCollection() {
        launchIo(
            jobDispatcher = jobDispatcher,
            onException = ::onException
        ){
            pageLoading.value = true

            collectionRepo.getCollectionsFlow(config.gameId).collect { collectionsResource ->
                updateUi(collectionsResource)
            }
        }
    }

    private suspend fun updateUi(
        collectionsResource : Resource<List<Collection>>
    ) {
        this.pageLoading.value = collectionsResource is Resource.Loading
        if(collectionsResource is Resource.Error){
            this.onException(collectionsResource.exception)
            return
        }

        this.collections.postSwap(
            collectionsResource.getDataOrThrow().map { it.toCollectionViewModel() }
        )
    }

    fun onException(t : Throwable) {
        System.err.println(t.message)
        pageLoading.value = false
    }

    fun onCollectionDelete(collectionId : Int) = launchIo(jobDispatcher =jobDispatcher, onException = ::onException) {
        pageLoading.value = true
        collectionRepo.deleteCollection(collectionId = collectionId)
        loadCollection()
        closeDialog()
        pageLoading.value = false
    }

    fun onCollectionDeleteClick(collectionId : Int) {
        dialogConfig.value = DialogConfig.DeleteWarningDialog(collectionId = collectionId)
    }

    fun closeDialog() {
        dialogConfig.value = DialogConfig.Closed
    }

    data class Config(
        val gameId : Int
    )

    data class CollectionViewModel(
        val id : Int,
        val name : String
    )

    sealed class DialogConfig {
        data object Closed : DialogConfig()
        data class DeleteWarningDialog(
            val collectionId : Int
        ) : DialogConfig()
        data class ErrorDialog(
            val title : String,
            val message : String
        ) : DialogConfig()
    }
}

fun Collection.toCollectionViewModel() : CollectionListScreenModel.CollectionViewModel{
    return CollectionListScreenModel.CollectionViewModel(
        id = this.id,
        name = this.name
    )
}