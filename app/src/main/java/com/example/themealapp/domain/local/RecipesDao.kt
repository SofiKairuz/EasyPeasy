package com.example.themealapp.domain.local

import androidx.room.*
import com.example.themealapp.data.model.Hit
import com.example.themealapp.data.model.RecipeEntity

@Dao
interface RecipesDao {

    @Query("SELECT * FROM recipesEntity WHERE recipe_view = 'FAVORITES' ")
    suspend fun getAllFavorites(): List<RecipeEntity>

    @Query("SELECT * FROM recipesEntity WHERE recipe_view = 'MY_RECIPES' ")
    suspend fun getAllMyRecipes(): List<RecipeEntity>

    @Query(
        "SELECT recipeId FROM recipesEntity " +
               "WHERE recipe_name = :name AND " +
               "recipe_img = :imagen AND " +
               "recipe_url_link = :url AND " +
               "recipe_origen = :origen")
    suspend fun getFavoriteByParams(name: String, origen: String, imagen: String, url: String) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(recipe:RecipeEntity)

    @Delete
    suspend fun deleteRecipeEntity(recipe: RecipeEntity)

}