package com.jaehl.gameTool.common.ui.screens.collectionList

import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.repo.CollectionRepo
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.ui.screens.launchIo

class CollectionListScreenModel (
    val jobDispatcher : JobDispatcher,
    val collectionRepo : CollectionRepo
) : ScreenModel {

    private lateinit var config : Config

    var collections = mutableStateListOf<CollectionViewModel>()

    fun setup(config : Config) {
        this.config = config

        launchIo(
            jobDispatcher = jobDispatcher,
            onException = ::onError
        ){
            loadCollection()
        }
    }

    private suspend fun loadCollection(){
        collectionRepo.getCollections(config.gameId).collect { collections ->
            val collectionViewModels = collections.map { it.toCollectionViewModel() }
            this.collections.postSwap(collectionViewModels)
        }
    }

    fun onCollectionDeleteClick(collectionId : Int) = launchIo(jobDispatcher =jobDispatcher, onException = ::onError) {
        collectionRepo.deleteCollection(collectionId = collectionId)
        loadCollection()
    }

    private fun onError(t : Throwable) {
        System.err.println(t.message)
    }

    data class Config(
        val gameId : Int
    )

    data class CollectionViewModel(
        val id : Int,
        val name : String
    )
}

fun Collection.toCollectionViewModel() : CollectionListScreenModel.CollectionViewModel{
    return CollectionListScreenModel.CollectionViewModel(
        id = this.id,
        name = this.name
    )
}