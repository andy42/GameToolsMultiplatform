package com.jaehl.gameTool.common.ui.util

import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.model.ItemRecipeNode
import com.jaehl.gameTool.common.data.model.Recipe
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.data.repo.RecipeRepo
import com.jaehl.gameTool.common.extensions.toItemModel
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel
import java.lang.ref.WeakReference
import kotlin.math.ceil

class ItemRecipeNodeUtil(
    private val itemRepo : ItemRepo,
    private val recipeRepo : RecipeRepo,
    private val appConfig : AppConfig,
    private val authProvider: AuthProvider
) {
    private suspend fun itemAmountViewModel(itemAmount : ItemAmount, multiplier : Int = 1) : ItemAmountViewModel{
        val item = itemRepo.getItem(itemAmount.itemId) ?: throw ItemNotFoundException(itemAmount.itemId)
        return ItemAmountViewModel(
            itemModel = item.toItemModel(appConfig, authProvider),
            amount = itemAmount.amount * multiplier
        )
    }

    private fun getOutputItemAmountFor(itemId : Int, recipe : Recipe) : ItemAmount {
        return recipe.output.firstOrNull { it.itemId == itemId } ?: throw ItemNotFoundException(itemId)
    }

    private suspend fun getByProductFor(itemAmount : ItemAmountViewModel, recipe : Recipe, multiplier : Int) : ArrayList<ItemAmountViewModel> {
        return ArrayList(recipe.output.filter { it.itemId != itemAmount.itemModel.id }.map { itemAmountViewModel(it, it.amount * multiplier) })
    }

    private fun recipeMultiplier(itemAmount : ItemAmountViewModel, recipeItemAmount : ItemAmount) : Int {
        return ceil(itemAmount.amount/recipeItemAmount.amount.toDouble()).toInt()
    }

    suspend fun buildTree(itemAmount : ItemAmount, parentNode : ItemRecipeNode? = null, recipeId : Int? = null) : ItemRecipeNode?{
        return buildTree(itemAmountViewModel(itemAmount), parentNode, recipeId)
    }

    suspend fun buildTree(itemAmount : ItemAmountViewModel, parentNode : ItemRecipeNode? = null, recipeId : Int? = null) : ItemRecipeNode?{
//        if (itemAmount.item.categories.contains(ItemCategory.Resources) && recipeId == null){
//            return ItemRecipeNode(
//                recipe = null,
//                parentNode = WeakReference(parentNode),
//                itemAmount = itemAmount)
//        }
        val recipes = recipeRepo.getRecipesForOutput(itemAmount.itemModel.id)

        if(recipes.isEmpty()){
            return ItemRecipeNode(
                recipe = null,
                parentNode = WeakReference(parentNode),
                itemAmount = itemAmount)
        }

        val recipe = if(recipeId == null){
            recipes.firstOrNull() ?: return null
        } else {
            recipes.firstOrNull { it.id ==recipeId } ?: return null
        }

        val recipeOutputAmount = getOutputItemAmountFor(itemAmount.itemModel.id, recipe)
        val recipeMultiplier = recipeMultiplier(itemAmount, recipeOutputAmount)

        val node = ItemRecipeNode(
            recipe = recipe,
            parentNode = WeakReference(parentNode),
            itemAmount = itemAmount,
            byProducts = getByProductFor(itemAmount, recipe, recipeMultiplier),
            alternativeRecipe = (recipes.size > 1)
        )

        val inputs = recipe.input.mapNotNull { inputItemAmount ->
            buildTree(
                itemAmountViewModel(
                    inputItemAmount,
                    recipeMultiplier
                ),
                node)
        }

        node.inputs = ArrayList(inputs)

        return node
    }
}