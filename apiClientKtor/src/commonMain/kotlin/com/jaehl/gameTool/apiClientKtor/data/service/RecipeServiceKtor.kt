package com.jaehl.gameTool.apiClientKtor.data.service

import com.jaehl.gameTool.apiClientKtor.data.model.AddRecipeRequest
import com.jaehl.gameTool.apiClientKtor.data.model.RecipeAmountRequest
import com.jaehl.gameTool.apiClientKtor.data.model.UpdateRecipeRequest
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.model.Recipe
import com.jaehl.gameTool.common.data.service.RecipeService
import io.ktor.http.*

class RecipeServiceKtor(
    private val requestUtil : RequestUtil
) : RecipeService{

    override suspend fun getRecipes(gameId: Int): List<Recipe> {
        return requestUtil.createRequest(
            url = "recipes?gameId=$gameId",
            HttpMethod.Get
        )
    }

    override suspend fun getRecipes(): List<Recipe> {
        return requestUtil.createRequest(
            url = "recipes",
            HttpMethod.Get
        )
    }

    override suspend fun getRecipe(recipeId: Int): Recipe {
        return requestUtil.createRequest(
            url = "recipes/$recipeId",
            HttpMethod.Get
        )
    }

    override suspend fun createRecipes(
        gameId: Int,
        craftedAt: List<Int>,
        input: List<ItemAmount>,
        output: List<ItemAmount>
    ): Recipe {
        return requestUtil.createRequest(
            url = "recipes/new",
            HttpMethod.Post,
            requestBody = AddRecipeRequest(
                gameId = gameId,
                craftedAt = craftedAt,
                input = input.map { RecipeAmountRequest(
                    itemId = it.itemId,
                    amount = it.amount)
                },
                output = output.map { RecipeAmountRequest(
                    itemId = it.itemId,
                    amount = it.amount)
                },
            )
        )
    }

    override suspend fun updateRecipes(
        recipeId: Int,
        gameId: Int,
        craftedAt: List<Int>,
        input: List<ItemAmount>,
        output: List<ItemAmount>
    ): Recipe {
        return requestUtil.createRequest(
            url = "recipes/new",
            HttpMethod.Post,
            requestBody = UpdateRecipeRequest(
                gameId = gameId,
                craftedAt = craftedAt,
                input = input.map { RecipeAmountRequest(
                    itemId = it.itemId,
                    amount = it.amount)
                },
                output = output.map { RecipeAmountRequest(
                    itemId = it.itemId,
                    amount = it.amount)
                },
            )
        )
    }

    override suspend fun deleteRecipe(recipeId: Int) {
        requestUtil.createRequestNoResponse(
            url = "recipes/$recipeId",
            HttpMethod.Delete
        )
    }
}