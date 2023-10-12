package com.jaehl.gameTool.common.ui.screens.accountDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.ui.componets.AppBar
import com.jaehl.gameTool.common.ui.screens.login.LoginScreen

class AccountDetailsScreen(

) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<AccountDetailsScreenModel>()

        LaunchedEffect(screenModel.logoutEvent.value){
            if(screenModel.logoutEvent.value){
                navigator.popAll()
                navigator.push(LoginScreen())
                screenModel.logoutEvent.value = false
            }
        }

        LifecycleEffect(
            onStarted = {
                screenModel.setup()
            }
        )

        AccountDetailsPage(
            viewModel = screenModel.viewModel.value,
            onBackClick = {
                navigator.pop()
            },
            onLogoutClick = {
                screenModel.logoutClick()
            }
        )
    }
}

@Composable
fun AccountDetailsPage(
    viewModel : AccountDetailsScreenModel.ViewModel,
    onBackClick : () -> Unit,
    onLogoutClick : () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Gray)
    ) {
        AppBar(
            title = "Home",
            backButtonEnabled = true,
            onBackClick = {
                onBackClick()
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()

        ) {


            UserCard(
                modifier = Modifier
                    .width(400.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp),
                viewModel = viewModel
            )
            Row(
                modifier = Modifier
                    .width(400.dp)
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier
                        .padding(top = 10.dp),
                    onClick = {
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                ){
                    Text(
                        text ="Logout",
                        color = MaterialTheme.colors.onError
                    )
                }
            }
        }
    }
}

@Composable
fun UserCard(
    modifier: Modifier,
    viewModel : AccountDetailsScreenModel.ViewModel,
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.secondary),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 12.dp, bottom = 12.dp),
                    color = MaterialTheme.colors.onSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "User"
                )
            }
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colors.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                text = "UserName : ${viewModel.userName}"
            )
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colors.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                text = "Role : ${viewModel.role}"
            )
        }
    }
}