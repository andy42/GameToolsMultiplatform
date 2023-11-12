package com.jaehl.gameTool.common.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.Recipe
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.repo.*
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.ui.UiExceptionHandler
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.util.UiException
import com.jaehl.gameTool.common.ui.viewModel.ClosedDialogViewModel
import com.jaehl.gameTool.common.ui.viewModel.DialogViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine

class HomeScreenModel(
    private val jobDispatcher : JobDispatcher,
    private val gameRepo : GameRepo,
    private val userRepo: UserRepo,
    private val itemRepo: ItemRepo,
    private val recipeRepo: RecipeRepo,
    private val collectionRepo: CollectionRepo,
    private val tokenProvider: TokenProvider,
    private val appConfig: AppConfig,
    private val uiExceptionHandler : UiExceptionHandler
) : ScreenModel {

    var games = mutableStateListOf<GameModel>()
    var showAdminTools by mutableStateOf(false)
    var userUnverified by mutableStateOf(false)
    var showEditGames by mutableStateOf(false)
    var pageLoading by mutableStateOf(false)

    var logoutEvent by mutableStateOf(false)

    var dialogViewModel by mutableStateOf<DialogViewModel>(ClosedDialogViewModel)

    fun setup() = launchIo(jobDispatcher, ::onException){
        dataRefresh()
    }

    private suspend fun updateUi(
        userResource : Resource<User>,
        gamesResource : Resource<List<Game>>,
        itemSResource : Resource<List<Item>>,
        recipesResource : Resource<List<Recipe>>,
        collectionsResource : Resource<List<Collection>>

    ) {

        if(userResource is Resource.Error){
            onException(userResource.exception)
            return
        }

        val user = userResource.getDataOrThrow()
        val userUnverified = listOf(
            User.Role.Unverified
        ).contains(user.role)
        this.userUnverified = userUnverified

        showAdminTools = listOf(
            User.Role.Admin
        ).contains(user.role)

        //if userUnverified then all other api call with return forbidden errors so exit
        if(!userUnverified){
            listOf<Resource<*>>(gamesResource, itemSResource, recipesResource, collectionsResource).forEach { resource ->
                if(resource is Resource.Error){
                    onException(resource.exception)
                    return
                }
            }

            showEditGames = listOf(
                User.Role.Admin,
                User.Role.Contributor
            ).contains(user.role)

            this.games.postSwap(
                gamesResource.getDataOrThrow().map {
                    it.toGameModel(appConfig, tokenProvider)
                }
            )
        }

        pageLoading = userResource is Resource.Loading
                || gamesResource is Resource.Loading
                || itemSResource is Resource.Loading
                || recipesResource is Resource.Loading
                || collectionsResource is Resource.Loading
    }

    suspend fun dataRefresh() {
        pageLoading = true
        launchIo(jobDispatcher, ::onException){
            combine(
                userRepo.getUserSelFlow(),
                gameRepo.getGames(),
                itemRepo.getItems(),
                recipeRepo.getRecipesFlow(),
                collectionRepo.getCollectionsFlow()
            ) { user, games, itemSResource, recipesResource , collectionsResource ->
                updateUi(user, games, itemSResource, recipesResource, collectionsResource)
            }.collect()
        }
    }

    fun onRefreshClick() = launchIo(jobDispatcher, ::onException){
        dataRefresh()
    }

    fun closeDialog() {
        dialogViewModel = ClosedDialogViewModel
    }

    private fun onException(t: Throwable){
        if (t is UiException){
            dialogViewModel = uiExceptionHandler.handelException(t)
        }
        pageLoading = false
    }

    fun forceLogout() = launchIo(jobDispatcher, ::onException) {
        tokenProvider.clearTokens()
        logoutEvent = true
        closeDialog()
    }
}