package com.jaehl.gameTool.common.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.jaehl.gameTool.common.ui.TestTags
import com.jaehl.gameTool.common.ui.componets.ImageResource
import com.jaehl.gameTool.common.ui.viewModel.ClosedDialogViewModel
import com.jaehl.gameTool.common.ui.viewModel.DialogViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HomePageTest {
    @get:Rule
    val compose = createComposeRule()

    private val homePageInterface = HomePageInterface()

    @Before
    fun before(){
        homePageInterface.clear()
    }

    @Test
    fun `game row test`(){

        val gameModel = GameModel(
            id = 1,
            name = "name1",
            icon = ImageResource.ImageLocalResource(""),
            banner = ImageResource.ImageLocalResource("")
        )

        compose.setContent {
            HomePageBuilder()
                .setGames(listOf(
                    gameModel
                ))
                .build()
        }

        val rows = compose.onAllNodes(
            hasTestTag(TestTags.Home.game_row)
        )
        rows.onFirst().performClick()
        assertEquals(gameModel.id, homePageInterface.gameClickedId)
        rows.onFirst().assertTextEquals(gameModel.name)
    }

    @Test
    fun `game row edit test`(){

        val gameModel = GameModel(
            id = 1,
            name = "name1",
            icon = ImageResource.ImageLocalResource(""),
            banner = ImageResource.ImageLocalResource("")
        )

        compose.setContent {
            HomePageBuilder()
                .setGames(listOf(
                    gameModel
                ))
                .setShowEditGames(true)
                .build()
        }

        val rowEditButtons = compose.onAllNodes(
            hasTestTag(TestTags.Home.game_row_edit_button)
        )

        rowEditButtons.onFirst().performClick()
        assertEquals(gameModel.id, homePageInterface.gameEditClickedId)
    }

    @Test
    fun `Unverified user message test`(){

        val gameModel = GameModel(
            id = 1,
            name = "name1",
            icon = ImageResource.ImageLocalResource(""),
            banner = ImageResource.ImageLocalResource("")
        )

        compose.setContent {
            HomePageBuilder()
                .setGames(listOf(
                    gameModel
                ))
                .setUserUnverified(true)
                .build()
        }

        compose.onAllNodes(
            hasTestTag(TestTags.Home.game_row)
        ).assertCountEquals(0)

        compose.onNodeWithTag(TestTags.Home.user_message_card).assertExists()
    }

    @Test
    fun `admin tools test`(){

        val gameModel = GameModel(
            id = 1,
            name = "name1",
            icon = ImageResource.ImageLocalResource(""),
            banner = ImageResource.ImageLocalResource("")
        )

        compose.setContent {
            HomePageBuilder()
                .setGames(listOf(
                    gameModel
                ))
                .setShowAdminTools(true)
                .build()
        }

        compose.onAllNodes(
            hasTestTag(TestTags.Home.game_row)
        ).assertCountEquals(1)

        compose.onNodeWithTag(TestTags.Home.user_message_card).assertDoesNotExist()
        compose.onNodeWithTag(TestTags.Home.admin_tools_card).assertExists()

        compose.onNodeWithTag(TestTags.Home.admin_tools_users_button).performClick()
        assertTrue(homePageInterface.usersClick)

        compose.onNodeWithTag(TestTags.Home.admin_tools_backup_button).performClick()
        assertTrue(homePageInterface.backupClick)
    }


    inner class HomePageBuilder {
        private var loading : Boolean = false
        private var games: List<GameModel> = listOf()
        private var dialogViewModel : DialogViewModel = ClosedDialogViewModel
        private var showAdminTools : Boolean = false
        private var showEditGames : Boolean = false
        private var userUnverified : Boolean = false

        fun setLoading(loading : Boolean) = apply {
            this.loading = loading
        }

        fun setGames(games: List<GameModel>) = apply {
            this.games = games
        }

        fun setDialogViewModel(dialogViewModel : DialogViewModel) = apply {
            this.dialogViewModel = dialogViewModel
        }

        fun setShowAdminTools(showAdminTools : Boolean) = apply {
            this.showAdminTools = showAdminTools
        }

        fun setShowEditGames(showEditGames : Boolean) = apply {
            this.showEditGames = showEditGames
        }

        fun setUserUnverified(userUnverified : Boolean) = apply {
            this.userUnverified = userUnverified
        }

        @Composable
        fun build() {
            HomePage(
                loading = loading,
                games = games,
                dialogViewModel = dialogViewModel,
                showAdminTools = showAdminTools,
                showEditGames = showEditGames,
                userUnverified = userUnverified,
                onAccountClick = homePageInterface::onAccountClick,
                onUsersClick = homePageInterface::onUsersClick,
                onCreateGameClick = homePageInterface::onCreateGameClick,
                onGameClick = homePageInterface::onGameClick,
                onGameEditClick = homePageInterface::onGameEditClick,
                onBackupClick = homePageInterface::onBackupClick,
                onRefreshClick = homePageInterface::onRefreshClick,
                onCloseDialog = homePageInterface::onCloseDialog,
                onForceLogout = homePageInterface::onForceLogout
            )
        }
    }

    class HomePageInterface {

        var accountClick = false
        var usersClick = false
        var createGameClick = false
        var gameClickedId : Int? = null
        var gameEditClickedId : Int? = null
        var backupClick = false
        var refreshClick = false
        var closeDialog = false
        var forceLogout = false

        fun onAccountClick() {
            accountClick = true
        }
        fun onUsersClick() {
            usersClick = true
        }
        fun onCreateGameClick() {
            createGameClick = true
        }
        fun onGameClick(gameId: Int) {
            gameClickedId = gameId
        }
        fun onGameEditClick(gameId: Int) {
            gameEditClickedId = gameId
        }
        fun onBackupClick() {
            backupClick = true
        }
        fun onRefreshClick() {
            refreshClick = true
        }
        fun onCloseDialog() {
            closeDialog = true
        }
        fun onForceLogout() {
            forceLogout = true
        }

        fun clear(){
            accountClick = false
            usersClick = false
            createGameClick = false
            gameClickedId = null
            gameEditClickedId = null
            backupClick = false
            refreshClick = false
            closeDialog = true
            forceLogout = false
        }
    }
}