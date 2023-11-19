package com.jaehl.gameTool.common.ui.screens.home

import com.jaehl.gameTool.common.JobDispatcherTest
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.GameMockData
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.UserMockData
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.repo.*
import com.jaehl.gameTool.common.ui.UiExceptionHandlerImp
import com.jaehl.gameTool.common.ui.componets.ImageResource
import com.jaehl.gameTool.common.domain.useCase.GetUserPermissionsUseCaseImp
import com.jaehl.gameTool.common.ui.util.UiException
import com.jaehl.gameTool.common.ui.viewModel.ErrorDialogViewModel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HomeScreenModelTest {

    private val dispatcher = StandardTestDispatcher ()

    private val itemCategoriesMap = HashMap<Int, ItemCategory>()
    private val baseUrl = "https://test.com"

    private val gameRepoMock = GameRepoMock()
    private val userRepoMock = UserRepoMock()
    private val getUserPermissionsUseCase = GetUserPermissionsUseCaseImp(userRepoMock)
    private val itemRepoMock = ItemRepoMock(itemCategoriesMap)
    private val recipeRepoMock = RecipeRepoMock()
    private val collectionRepoMock = CollectionRepoMock()
    private val tokenProviderMock = TokenProviderMock()
    private val uiExceptionHandler = UiExceptionHandlerImp()

    private fun buildHomeScreenModel() : HomeScreenModel {
        return HomeScreenModel(
            JobDispatcherTest(dispatcher),
            gameRepoMock,
            userRepoMock,
            itemRepoMock,
            recipeRepoMock,
            collectionRepoMock,
            getUserPermissionsUseCase,
            tokenProviderMock,
            AppConfig(baseUrl = baseUrl),
            uiExceptionHandler
        )
    }

    @Before
    fun before(){
        tokenProviderMock.clear()
        tokenProviderMock.isRefreshTokenValid = true
        gameRepoMock.clear()
        userRepoMock.clear()
        itemRepoMock.clear()
        recipeRepoMock.clear()
        collectionRepoMock.clear()
    }

    @Test
    fun `show admin tools for admin account`() = runTest(dispatcher) {
        tokenProviderMock.isRefreshTokenValid = true

        userRepoMock.userSelf = UserMockData.adminUser
        val screenModel = buildHomeScreenModel()
        screenModel.setup()
        advanceUntilIdle()
        assertTrue(screenModel.showAdminTools)
    }

    @Test
    fun `do not show admin tools for user account`() = runTest(dispatcher) {
        tokenProviderMock.isRefreshTokenValid = true
        userRepoMock.userSelf = UserMockData.standardUser
        val screenModel = buildHomeScreenModel()
        screenModel.setup()
        advanceUntilIdle()
        assertFalse(screenModel.showAdminTools)
    }

    @Test
    fun `show Unverified for an unverified account`() = runTest(dispatcher) {
        tokenProviderMock.isRefreshTokenValid = true
        userRepoMock.userSelf = UserMockData.unverifiedUser
        val screenModel = buildHomeScreenModel()
        screenModel.setup()
        advanceUntilIdle()
        assertFalse(screenModel.showAdminTools)
        assertTrue(screenModel.userUnverified)
    }

    @Test
    fun `show games`() = runTest(dispatcher) {
        tokenProviderMock.isRefreshTokenValid = true
        tokenProviderMock.refreshToken = "refreshToken"
        tokenProviderMock.accessToken = "accessToken"
        userRepoMock.userSelf = UserMockData.standardUser
        val testGame = GameMockData.createGame(1)
        gameRepoMock.gameList = arrayListOf(testGame)
        val screenModel = buildHomeScreenModel()
        screenModel.setup()
        advanceUntilIdle()

        val gameModel = screenModel.games.first()

        assertEquals(testGame.id, gameModel.id)
        assertEquals(testGame.name, gameModel.name)
        assertEquals(
            ImageResource.ImageApiResource(url ="$baseUrl/images/${testGame.icon}", authHeader = tokenProviderMock.refreshToken),
            gameModel.icon
        )
        assertEquals(
            ImageResource.ImageApiResource(url ="$baseUrl/images/${testGame.banner}", authHeader = tokenProviderMock.refreshToken),
            gameModel.banner
        )
    }

    @Test
    fun `show error`() = runTest(dispatcher) {
        userRepoMock.userSelf = UserMockData.standardUser
        gameRepoMock.gameListResourceError = Resource.Error(UiException.GeneralError())
        val screenModel = buildHomeScreenModel()
        screenModel.setup()
        advanceUntilIdle()

        assertEquals(
            ErrorDialogViewModel(title = "Error", message = "Oops something went wrong"),
            screenModel.dialogViewModel
        )
    }

}