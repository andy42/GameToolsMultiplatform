package com.jaehl.gameTool.common.ui.screens.backupList

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.ui.componets.AppBar
import com.jaehl.gameTool.common.ui.componets.CustomVerticalScrollbar
import com.jaehl.gameTool.common.ui.componets.WarningDialog

class BackupListScreen(

) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<BackupListScreenModel>()

        LifecycleEffect(
            onStarted = {
                screenModel.setup()
            }
        )

        BackupListPage(
            backupList = screenModel.backupList,
            onBackClick = {
                navigator.pop()
            },
            onCreateBackup = {
                screenModel.createBackup()
            },
            onApplyBackupClick = { backupId ->
                screenModel.openDialog(
                    BackupListScreenModel.DialogData.ApplyDialog(
                        backupId = backupId
                    )
                )
            }
        )

        if(screenModel.dialogData.value is BackupListScreenModel.DialogData.ApplyDialog){
            WarningDialog(
                title = "Apply Backup",
                message = "are you sure you want to remove all data and apply this backup?",
                positiveText = "Apply Backup",
                negativeText = "No",
                onPositiveClick = {
                    screenModel.closeDialog()
                    screenModel.applyBackup((screenModel.dialogData.value as BackupListScreenModel.DialogData.ApplyDialog).backupId)
                },
                onNegativeClick = {
                    screenModel.closeDialog()
                }
            )
        }
    }
}

@Composable
fun BackupListPage(
    backupList : List<BackupListScreenModel.BackupViewModel>,
    onBackClick : () -> Unit,
    onCreateBackup : () -> Unit,
    onApplyBackupClick : (backupId : String) -> Unit
) {
    val state : ScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        AppBar(
            title = "Backups",
            backButtonEnabled = true,
            onBackClick = {
                onBackClick()
            }
        )
        Box(
            Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(state)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {
                        onCreateBackup()
                    }) {
                        Text("Create New Backup")
                    }
                }
                BackupList(
                    modifier = Modifier
                        .padding(top = 10.dp),
                    backupList = backupList,
                    onApplyBackupClick = onApplyBackupClick
                )
            }
            CustomVerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                scrollState = state
            )
        }
    }
}

@Composable
fun BackupList(
    modifier: Modifier,
    backupList : List<BackupListScreenModel.BackupViewModel>,
    onApplyBackupClick : (backupId : String) -> Unit
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            backupList.forEachIndexed { index, backupViewModel ->
                BackupRow(
                    modifier = Modifier,
                    backup = backupViewModel,
                    onApplyBackupClick = onApplyBackupClick
                )
            }
        }
    }
}


@Composable
fun BackupRow(
    modifier: Modifier,
    backup : BackupListScreenModel.BackupViewModel,
    onApplyBackupClick : (backupId : String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .clickable {  onApplyBackupClick(backup.id) }
    ) {
        Text(
            modifier = Modifier
                .padding(10.dp),
            text = backup.name
        )
    }
}