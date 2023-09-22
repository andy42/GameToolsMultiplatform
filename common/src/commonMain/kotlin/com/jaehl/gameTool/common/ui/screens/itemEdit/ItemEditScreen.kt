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
import com.jaehl.gameTool.common.ui.screens.itemEdit.componets.RecipeColumn

class ItemEditScreen(
    private val gameId : Int,
    private val itemId : Int?
): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<ItemEditScreenModel>()

        var isItemCategoryPickOpen = remember { mutableStateOf(false) }

        LaunchedEffect(itemId) {
            screenModel.setup(
                ItemEditScreenModel.Config(
                    gameId = gameId,
                    itemId = itemId
                )
            )
        }

        ItemEditPage(
            title = screenModel.title.value,
            viewModel = screenModel.viewModel.value,
            onBackClick = {
                navigator.pop()
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

            },
            onItemCategoryDeleteClick = { itemCategory ->
                screenModel.onItemCategoryDelete(itemCategory)
            },
            onIconChange = { filePath ->
                screenModel.onIconChange(filePath)
            },

            openItemPicker = { recipeId, isInput, itemId ->  
                             
            },
            onItemAmountChange = { recipeId, isInput, itemId, amount ->  
                                 
            },
            onItemAmountDelete = { recipeId, isInput, itemId ->  
                                 
            },
            onRecipeCraftedAtDelete = { recipeId, itemId ->  
                                      
            },
            onRecipeDelete = {recipeId ->  
                
            }
        )

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
                        viewModel.icon,
                        viewModel.iconError,
                        onIconChange = onIconChange
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
                    viewModel.recipeList.forEachIndexed { index, recipeViewModel ->
                        if (!recipeViewModel.isDeleted) {
                            RecipeColumn(
                                recipeIndex = index,
                                recipe = recipeViewModel,
                                openItemPicker = openItemPicker,
                                onItemAmountChange = onItemAmountChange,
                                onItemAmountDelete = onItemAmountDelete,
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


