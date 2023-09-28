package com.jaehl.gameTool.common.ui.screens.itemEdit

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.ui.componets.*
import com.jaehl.gameTool.common.ui.screens.itemEdit.componets.ItemCategoryChip
import com.jaehl.gameTool.common.ui.screens.itemEdit.componets.RecipeCard

sealed class ItemPickerType {
    data class ItemRecipePicker(
        val recipeId: Int,
        val isInput : Boolean,
        val itemId : Int?
    ) : ItemPickerType()
    data object ItemPickerClosed : ItemPickerType()
    data class ItemCraftedAtPicker(
        val recipeId: Int
    ) : ItemPickerType()
}

class ItemEditScreen(
    private val gameId : Int,
    private val itemId : Int?
): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<ItemEditScreenModel>()

        var isItemCategoryPickOpen = remember { mutableStateOf(false) }
        var itemPickerType = remember { mutableStateOf<ItemPickerType>(ItemPickerType.ItemPickerClosed) }
        var itemPickerSearchText = remember { mutableStateOf("") }

        LaunchedEffect(itemId) {
            screenModel.setup(
                ItemEditScreenModel.Config(
                    gameId = gameId,
                    itemId = itemId
                )
            )
        }

        LaunchedEffect(screenModel.closePageEvent.value){
            if(screenModel.closePageEvent.value){
                navigator.pop()
            }
        }

        ItemEditPage(
            title = screenModel.title.value,
            viewModel = screenModel.viewModel.value,
            onBackClick = {
                //navigator.pop()
                screenModel.onBackClick()
            },
            onItemNameChange = { value ->
                screenModel.onItemNameChange(value)
            },
            onSaveClick = {
                screenModel.save()
            },
            onOpenItemCategoriesPicker = {
                isItemCategoryPickOpen.value = true
            },
            onRecipeAddClick = {
                screenModel.onRecipeAdd()
            },
            onItemCategoryDeleteClick = { itemCategory ->
                screenModel.onItemCategoryDelete(itemCategory)
            },
            onIconChange = { filePath ->
                screenModel.onIconChange(filePath)
            },

            openItemPicker = { recipeId, isInput, itemId ->
                itemPickerType.value = ItemPickerType.ItemRecipePicker(
                    recipeId = recipeId,
                    isInput = isInput,
                    itemId = itemId
                )
            },
            onItemAmountChange = { recipeId, isInput, itemId, amount ->
                screenModel.onUpdateItemAmountAmount(recipeId, isInput,itemId, amount)
            },
            onItemAmountDelete = { recipeId, isInput, itemId ->
                screenModel.onDeleteItemAmount(recipeId, isInput,itemId)
            },
            onAddRecipeCraftedAtClick = { recipeId ->
                itemPickerType.value = ItemPickerType.ItemCraftedAtPicker(
                    recipeId = recipeId
                )
            },
            onRecipeCraftedAtDelete = { recipeId, itemId ->
                screenModel.onDeleteCreatedAtItem(recipeId, itemId)
            },
            onRecipeDelete = {recipeId ->
                screenModel.onDeleteRecipe(recipeId)
            }
        )

        if(screenModel.showExitSaveDialog.value){
            WarningDialog(
                title = "Unsaved changes",
                message = "You have unsaved changes, do you want to save changes?",
                positiveText = "Save",
                negativeText = "Discard",
                onPositiveClick = {
                    screenModel.showExitSaveDialog.value = false
                    screenModel.save()
                },
                onNegativeClick = {
                    screenModel.showExitSaveDialog.value = false
                    navigator.pop()
                }
            )
        }

        if(isItemCategoryPickOpen.value){
            ItemCategoryPickDialog(
                title = "Category",
                categoryList = screenModel.itemCategories,
                onCategoryClick = { itemCategory ->
                    screenModel.onItemCategoryAdd(itemCategory)
                    isItemCategoryPickOpen.value = false
                },
                onClose = {
                    isItemCategoryPickOpen.value = false
                }
            )
        }
        if(itemPickerType.value !is ItemPickerType.ItemPickerClosed){
            ItemPickDialog(
                title = "",
                itemList = screenModel.items.filter {
                    it.name.contains(itemPickerSearchText.value, true)
                },
                isClearable = false,
                onItemClick = { itemId : Int? ->
                    if(itemId == null) return@ItemPickDialog
                    when (val pickerType = itemPickerType.value) {
                        is ItemPickerType.ItemRecipePicker -> {
                            if(pickerType.itemId == null){
                                screenModel.onAddItemAmount(
                                    recipeId = pickerType.recipeId,
                                    isInput = pickerType.isInput,
                                    itemId = itemId)
                            } else {
                                screenModel.onUpdateItemAmountItem(
                                    recipeId = pickerType.recipeId,
                                    isInput = pickerType.isInput,
                                    oldItemId = pickerType.itemId,
                                    newItemId = itemId)
                            }
                        }
                        is ItemPickerType.ItemCraftedAtPicker -> {
                            screenModel.onAddCreatedAtItem(pickerType.recipeId, itemId)
                        }
                        else -> {}
                    }

                    itemPickerType.value = ItemPickerType.ItemPickerClosed
                    itemPickerSearchText.value = ""
                },
                searchText = itemPickerSearchText.value,
                onSearchChange = { text ->
                    itemPickerSearchText.value = text
                },
                onClose = {
                    itemPickerType.value = ItemPickerType.ItemPickerClosed
                    itemPickerSearchText.value = ""
                }

            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemEditPage(
    title : String,
    viewModel : ItemEditScreenModel.ViewModel,
    onBackClick : ()-> Unit,
    onItemNameChange : (value : String) -> Unit,
    onSaveClick : () -> Unit,
    onOpenItemCategoriesPicker : () -> Unit,
    onRecipeAddClick : () -> Unit,
    onItemCategoryDeleteClick : (itemCategory : ItemCategory) -> Unit,
    onIconChange : (filePath : String) -> Unit,

    openItemPicker : (recipeId : Int, isInput : Boolean, itemId : Int?) -> Unit,
    onItemAmountChange : (recipeId : Int, isInput : Boolean, itemId : Int, amount : String) -> Unit,
    onItemAmountDelete : (recipeId : Int, isInput : Boolean, itemId : Int) -> Unit,
    onAddRecipeCraftedAtClick : (recipeId : Int) -> Unit,
    onRecipeCraftedAtDelete : (recipeId : Int, itemId : Int) -> Unit,
    onRecipeDelete : (recipeId : Int) -> Unit

) {
    val state : ScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        AppBar(
            title = title,
            backButtonEnabled = true,
            onBackClick = {
                onBackClick()
            },
            actions = {
                Button(
                    onClick = {
                        onSaveClick()
                    },
                    modifier = Modifier
                ) {
                    Text("Save")
                }
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 10.dp, top = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colors.surface)
                        .padding(10.dp)
                ) {
                    StyledOutlinedTextField(
                        viewModel.itemName,
                        modifier = Modifier,
                        label = { Text("Name") },
                        onValueChange = { value ->
                            onItemNameChange(value)
                        }
                    )
                    ImageEdit(
                        modifier = Modifier
                            .padding(top = 10.dp),
                        title = "Icon",
                        viewModel.icon,
                        viewModel.iconError,
                        onIconChange = onIconChange,
                        width = 150.dp,
                        height = 150.dp
                    )


                    Text(text = "Categories", modifier = Modifier.padding(top = 20.dp))
                    FlowRow(modifier = Modifier, verticalArrangement = Arrangement.Center) {
                        viewModel.itemCategories.forEach { itemCategory ->
                            ItemCategoryChip(
                                itemCategory,
                                onItemCategoryDeleteClick = onItemCategoryDeleteClick)
                        }
                        Button(
                            onClick = {
                                onOpenItemCategoriesPicker()
                            },
                            modifier = Modifier
                        ) {
                            Text("Add")
                        }
                    }
                }

                Column {
                    viewModel.recipeList
                        .filter {
                            !it.isDeleted
                        }
                        .forEachIndexed { index, recipeViewModel ->
                            if (!recipeViewModel.isDeleted) {
                                RecipeCard(
                                    recipeIndex = index,
                                    recipe = recipeViewModel,
                                    openItemPicker = openItemPicker,
                                    onItemAmountChange = onItemAmountChange,
                                    onItemAmountDelete = onItemAmountDelete,
                                    onAddRecipeCraftedAtClick = onAddRecipeCraftedAtClick,
                                    onRecipeCraftedAtDelete = onRecipeCraftedAtDelete,
                                    onRecipeDelete = onRecipeDelete
                                )
                            }
                        }
                }
                if (viewModel.allowAddRecipes) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp, top = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colors.surface)
                            .padding(10.dp)
                    ) {
                        Button(onClick = {
                            onRecipeAddClick()

                        }) {
                            Text("Add Recipe")
                        }
                    }
                }
            }
            CustomVerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                scrollState = state
            )
        }
    }
}


