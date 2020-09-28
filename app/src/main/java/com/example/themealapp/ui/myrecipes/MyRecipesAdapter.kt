package com.example.themealapp.ui.myrecipes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.themealapp.R
import com.example.themealapp.base.BaseViewHolder
import com.example.themealapp.data.model.RecipeEntity
import com.example.themealapp.data.model.asRecipe
import com.example.themealapp.utils.ImageController
import kotlinx.android.synthetic.main.recipes_row.view.*

class MyRecipesAdapter(private val context: Context, private var recipesList: List<RecipeEntity>,
                       private val itemClickListener: OnRecipeClickListener
)
    : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnRecipeClickListener {
        fun onRecipeClick(recipe: RecipeEntity, position: Int)
        fun onRecipeDeleteLongClick(recipe: RecipeEntity, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return MyRecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.recipes_row, parent, false))
    }

    override fun getItemCount(): Int {
        return recipesList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder) {
            is MyRecipeViewHolder -> holder.bind(recipesList[position], position)
        }
    }

    inner class MyRecipeViewHolder(itemView: View): BaseViewHolder<RecipeEntity>(itemView) {
        override fun bind(item: RecipeEntity, position: Int) {
            val recipe = item.asRecipe()

            if (recipe.image.startsWith("http://") || recipe.image.startsWith("https://")) {
                Glide.with(context).load(recipe.image).into(itemView.img_recipe)
            } else {
                itemView.img_recipe.setImageURI(ImageController.getImageUri(context, item.recipeId.toString()))
            }

            itemView.txt_title.text = recipe.name
            itemView.txt_description.text = "Receta de ${recipe.source}"
            recipe.ingredients.forEach {
                if (!it.text.trim().isNullOrEmpty())
                    itemView.txt_ingredients.text = itemView.txt_ingredients.text.toString() + it.text + ". "
            }
            itemView.setOnClickListener { itemClickListener.onRecipeClick(item, position) }
            itemView.setOnLongClickListener {
                itemClickListener.onRecipeDeleteLongClick(item, position)
                ImageController.deleteImage(context, item.recipeId.toString())
                return@setOnLongClickListener true
            }
        }
    }

}