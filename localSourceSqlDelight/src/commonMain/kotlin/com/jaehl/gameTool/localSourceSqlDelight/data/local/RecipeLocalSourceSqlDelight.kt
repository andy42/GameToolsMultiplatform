package com.jaehl.gameTool.localSourceSqlDelight.data.local

import com.jaehl.gameTool.common.data.local.RecipeLocalSource
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.model.Recipe
import com.jaehl.gameTool.localSourceSqlDelight.Database

class RecipeLocalSourceSqlDelight(
    private val database: Database
) : RecipeLocalSource {

    private fun convertRecipeEntity(recipeEntity: RecipeEntity) : Recipe{
        return Recipe(
            id = recipeEntity.id,
            gameId = recipeEntity.game_id,
            craftedAt = database.recipesQueries.getCraftedAtForRecipe(recipeEntity.id).executeAsList().map {
                it.item_id
            },
            input = database.recipesQueries.getRecipeInputForRecipe(recipeEntity.id).executeAsList().map {
                ItemAmount(
                    itemId = it.item_id,
                    amount = it.amount
                )
            },
            output = database.recipesQueries.getRecipeOutputForRecipe(recipeEntity.id).executeAsList().map {
                ItemAmount(
                    itemId = it.item_id,
                    amount = it.amount
                )
            }
        )
    }
    override suspend fun getRecipes(gameId: Int?): List<Recipe> {

        val recipes = if(gameId==null) database.recipesQueries.getAllRecipes().executeAsList()
            else database.recipesQueries.getAllRecipesForGame(gameId).executeAsList()

        return recipes.map { recipeEntity ->
            convertRecipeEntity(recipeEntity)
        }
    }

    override suspend fun getRecipe(id: Int): Recipe {
        return convertRecipeEntity(
            database.recipesQueries.getRecipe(id).executeAsOne()
        )
    }

    override suspend fun deleteRecipe(id: Int) {
        database.recipesQueries.deleteRecipe(id)
    }

    private fun updateRecipeRequest(recipe: Recipe){
        database.recipesQueries.updateRecipe(
            RecipeEntity(
                id = recipe.id,
                game_id = recipe.gameId
            )
        )
        database.recipesQueries.deleteAllRecipeCraftedAtForRecipe(recipe.id)
        database.recipesQueries.deleteAllRecipeInputForRecipe(recipe.id)
        database.recipesQueries.deleteAllRecipeOutputForRecipe(recipe.id)
        recipe.craftedAt.forEach {
            database.recipesQueries.insertCraftedAt(
                RecipeCraftedAtEntity(
                    recipe_id = recipe.id,
                    item_id = it,
                    game_id = recipe.gameId
                )
            )
        }
        recipe.input.forEach {
            database.recipesQueries.insertRecipeInput(
                RecipeInputEntity(
                    recipe_id = recipe.id,
                    item_id = it.itemId,
                    amount = it.amount,
                    game_id = recipe.gameId
                )
            )
        }
        recipe.output.forEach {
            database.recipesQueries.insertRecipeOutput(
                RecipeOutputEntity(
                    recipe_id = recipe.id,
                    item_id = it.itemId,
                    amount = it.amount,
                    game_id = recipe.gameId
                )
            )
        }
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        database.transaction {
            updateRecipeRequest(recipe)
        }
    }

    override suspend fun updateRecipes(gameId: Int?, recipes: List<Recipe>) {
        database.transaction {
            if (gameId == null) database.recipesQueries.deleteAllRecipes()
            else database.recipesQueries.deleteAllRecipesForGame(gameId)

            recipes.forEach {
                updateRecipeRequest(recipe = it)
            }
        }
    }

    override suspend fun getRecipesForOutput(itemId: Int): List<Recipe> {
        return database.recipesQueries.getRecipesOutputItem(itemId).executeAsList().map { recipeOutputEntity ->
            convertRecipeEntity(database.recipesQueries.getRecipe(recipeOutputEntity.recipe_id).executeAsOne())
        }
    }

    override suspend fun getRecipesForInput(itemId: Int): List<Recipe> {
        return database.recipesQueries.getRecipesInputItem(itemId).executeAsList().map { recipeIntputEntity ->
            convertRecipeEntity(database.recipesQueries.getRecipe(recipeIntputEntity.recipe_id).executeAsOne())
        }
    }
}