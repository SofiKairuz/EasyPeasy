package com.example.themealapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.themealapp.data.model.Hit
import com.example.themealapp.data.model.RecipeEntity
import com.example.themealapp.domain.Repo
import com.example.themealapp.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class MainViewModel(private val repo:Repo): ViewModel() {

    private val recipesData = MutableLiveData<String>()

    fun setRecipe(recipeName: String) {
        recipesData.value = recipeName
    }

    init {
        //Por defecto quiero que me traiga algun valor
        setRecipe("pollo")
    }

    val fetchRecipesList = recipesData.distinctUntilChanged().switchMap { resultName ->
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(repo.getRecipesList(resultName))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    fun saveRecipe(recipe: RecipeEntity): Int? =
        runBlocking {
            repo.saveRecipe(recipe)
        }

    fun validateDuplicateRecipe(name: String, source: String, img: String, url: String): Int? =
        runBlocking {
            repo.validateRecipe(name, source, img, url)
        }

    fun getFavorites() = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(repo.getFavoritesList())
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

    fun getMyRecipes() = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(repo.getMyRecipesList())
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

    fun deleteFavorite(recipe: RecipeEntity) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(repo.deleteFavorite(recipe))
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

    fun deleteMyRecipe(recipe: RecipeEntity) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(repo.deleteMyRecipe(recipe))
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

}