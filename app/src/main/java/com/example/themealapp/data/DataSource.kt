package com.example.themealapp.data

import com.example.themealapp.data.model.Hit
import com.example.themealapp.data.model.RecipeEntity
import com.example.themealapp.vo.Resource

interface DataSource {
    suspend fun getRecipeByName(recipeName: String): Resource<List<Hit>>
    suspend fun saveRecipeInDatabase(recipe: RecipeEntity)
    suspend fun getFavoriteRecipes(): Resource<List<RecipeEntity>>
    suspend fun getMyListRecipes(): Resource<List<RecipeEntity>>
    suspend fun checkDuplicates(name: String, source: String, img: String, url: String): Int?
    suspend fun deleteRecipe(recipe: RecipeEntity)
}