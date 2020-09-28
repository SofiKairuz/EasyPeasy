package com.example.themealapp.domain

import com.example.themealapp.data.model.Hit
import com.example.themealapp.data.model.RecipeEntity
import com.example.themealapp.vo.Resource

interface Repo {

    suspend fun getRecipesList(recipeName: String): Resource<List<Hit>>
    suspend fun getFavoritesList(): Resource<List<RecipeEntity>>
    suspend fun getMyRecipesList(): Resource<List<RecipeEntity>>
    suspend fun saveRecipe(recipe: RecipeEntity): Int?
    suspend fun validateRecipe(name: String, source: String, img: String, url: String): Int?
    suspend fun deleteFavorite(recipe: RecipeEntity): Resource<List<RecipeEntity>>
    suspend fun deleteMyRecipe(recipe: RecipeEntity): Resource<List<RecipeEntity>>

}