package com.jaehl.gameTool.common.data.model

import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel
import java.lang.ref.WeakReference

data class ItemRecipeNode(
    var recipe: Recipe?,
    var parentNode : WeakReference<ItemRecipeNode?>,
    var itemAmount : ItemAmountViewModel,
    var inputs : ArrayList<ItemRecipeNode> = arrayListOf(),
    var byProducts : ArrayList<ItemAmountViewModel> = arrayListOf(),
    var alternativeRecipe : Boolean = false
)