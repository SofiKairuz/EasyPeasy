package com.example.themealapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.themealapp.data.model.RecipeEntity
import com.example.themealapp.domain.local.RecipesDao
import com.example.themealapp.utils.AppConstants.TABLE_NAME

@Database(entities = arrayOf(RecipeEntity::class), version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun recipeDao(): RecipesDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            INSTANCE = INSTANCE ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, TABLE_NAME).build()
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }

    }


}