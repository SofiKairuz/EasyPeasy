package com.example.themealapp.domain

import com.example.themealapp.data.DataSourceImpl
import com.example.themealapp.data.model.Hit
import com.example.themealapp.data.model.RecipeEntity
import com.example.themealapp.vo.Resource

class RepoImpl(private val dataSource: DataSourceImpl) : Repo
{
    override suspend fun getRecipesList(recipeName: String): Resource<List<Hit>> {
        return dataSource.getRecipeByName(recipeName)
    }

    override suspend fun getFavoritesList(): Resource<List<RecipeEntity>> {
        return dataSource.getFavoriteRecipes()
    }

    override suspend fun getMyRecipesList(): Resource<List<RecipeEntity>> {
        return dataSource.getMyListRecipes()
    }

    override suspend fun saveRecipe(recipe: RecipeEntity): Int? {
        dataSource.saveRecipeInDatabase(recipe)
        return validateRecipe(recipe.name, recipe.source, recipe.image, recipe.url)
    }

    override suspend fun validateRecipe(name: String, source: String, img: String, url: String): Int? {
        return dataSource.checkDuplicates(name, source, img, url)
    }

    override suspend fun deleteFavorite(recipe: RecipeEntity): Resource<List<RecipeEntity>> {
        dataSource.deleteRecipe(recipe)
        return getFavoritesList()
    }

    override suspend fun deleteMyRecipe(recipe: RecipeEntity): Resource<List<RecipeEntity>> {
        dataSource.deleteRecipe(recipe)
        return getMyRecipesList()
    }

}