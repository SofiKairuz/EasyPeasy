package com.example.themealapp.data

import com.example.themealapp.AppDatabase
import com.example.themealapp.data.model.Hit
import com.example.themealapp.data.model.RecipeEntity
import com.example.themealapp.vo.Resource
import com.example.themealapp.vo.RetrofitClient

class DataSourceImpl(private val appDatabase: AppDatabase): DataSource {

    override suspend fun getRecipeByName(recipeName: String): Resource<List<Hit>> {
        return Resource.Success(RetrofitClient.webService.getRecipeByName(recipeName, 0, 25).hits)
    }

    override suspend fun saveRecipeInDatabase(recipe: RecipeEntity) {
        appDatabase.recipeDao().insertFavorite(recipe)
    }

    override suspend fun getFavoriteRecipes(): Resource<List<RecipeEntity>> {
        return Resource.Success(appDatabase.recipeDao().getAllFavorites())
    }

    override suspend fun getMyListRecipes(): Resource<List<RecipeEntity>> {
        return Resource.Success(appDatabase.recipeDao().getAllMyRecipes())
    }

    override suspend fun checkDuplicates(name: String, source: String, img: String, url: String): Int? {
        return appDatabase.recipeDao().getFavoriteByParams(name, source, img, url)
    }

    override suspend fun deleteRecipe(recipe: RecipeEntity) {
        appDatabase.recipeDao().deleteRecipeEntity(recipe)
    }

}