package com.example.themealapp.domain.remote

import com.example.themealapp.data.model.MainData
import retrofit2.http.GET
import retrofit2.http.Query

interface WebService {

    @GET("search")
    suspend fun getRecipeByName(
        @Query(value = "q") recipeName: String,
        @Query(value = "from") countFrom: Int,
        @Query(value = "to") countTo: Int): MainData

}