package com.example.themealapp.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.function.DoubleToLongFunction

//region Entities from API
@Parcelize
data class Hit(
    val recipe: Recipe,
    val bookmarked: Boolean,
    val bought: Boolean
): Parcelable

@Parcelize
data class MainData(
    val q: String,
    val from: Int,
    val to: Int,
    val more: Boolean,
    val count: Int,
    val hits: List<Hit>
): Parcelable

@Parcelize
data class Recipe(
    @SerializedName("label")
    var name: String = "",
    @SerializedName("image")
    val image: String = "",
    @SerializedName("ingredients")
    val ingredients: List<Ingredientes>,
    @SerializedName("source")
    val source: String = "",
    @SerializedName("url")
    val url: String = "",
    @SerializedName("calories")
    val totalCalories: String = ""
): Parcelable

@Parcelize
data class Ingredientes(
    val text: String,
    val quantity: Double,
    val measure: String,
    val food: String,
    val weight: Double
): Parcelable

@Parcelize
data class xIngredientes(
    @SerializedName("list")
    val lista: List<Ingredientes>
): Parcelable

//endregion

//region Entities Room
@Entity(tableName = "recipesEntity")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val recipeId:Int,
    @ColumnInfo(name = "recipe_name")
    val name: String = "",
    @ColumnInfo(name = "recipe_img")
    val image: String = "",
    @ColumnInfo(name = "recipe_ingredients")
    val ingredients: String = "",
    @ColumnInfo(name = "recipe_origen")
    val source: String = "",
    @ColumnInfo(name = "recipe_url_link")
    val url: String = "",
    @ColumnInfo(name = "recipe_calories")
    val totalCalories: String = "",
    @ColumnInfo(name = "recipe_view")
    val recipeView: String = ""
)
//endregion

fun RecipeEntity.asRecipe() : Recipe {
    val ingredientList = Gson().fromJson("{\"list\":" + this.ingredients + "}", xIngredientes::class.java)
    return Recipe(this.name, this.image, ingredientList.lista, this.source, this.url, this.totalCalories)
}