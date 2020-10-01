package com.example.themealapp.ui.myrecipes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult.NO_POSITION
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.themealapp.R
import com.example.themealapp.base.BaseViewHolder
import com.example.themealapp.data.model.RecipeEntity
import com.example.themealapp.data.model.asRecipe
import com.example.themealapp.databinding.RecipesRowBinding
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
        val itemBinding = RecipesRowBinding.inflate(LayoutInflater.from(context), parent, false)
        val vh = MyRecipeViewHolder(itemBinding)

        vh.itemView.setOnClickListener {
            val pos = vh.adapterPosition
            if (pos != NO_POSITION) {
                itemClickListener.onRecipeClick(recipesList[pos],pos)
            }
        }

        vh.itemView.setOnLongClickListener {
            val pos = vh.adapterPosition
            if (pos != NO_POSITION) {
                itemClickListener.onRecipeDeleteLongClick(recipesList[pos], pos)
            }
            return@setOnLongClickListener true
        }

        return vh
    }

    override fun getItemCount(): Int {
        return recipesList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder) {
            is MyRecipeViewHolder -> holder.bind(recipesList[position], position)
        }
    }

    private inner class MyRecipeViewHolder(val binding: RecipesRowBinding): BaseViewHolder<RecipeEntity>(binding.root) {
        override fun bind(item: RecipeEntity, position: Int) = with(binding) {
            val recipe = item.asRecipe()

            if (recipe.image.startsWith("http://") || recipe.image.startsWith("https://")) {
                Glide.with(context).load(recipe.image).into(imgRecipe)
            } else {
                imgRecipe.setImageURI(ImageController.getImageUri(context, item.recipeId.toString()))
            }

            txtTitle.text = recipe.name
            txtDescription.text = "Receta de ${recipe.source}"
            recipe.ingredients.forEach {
                if (!it.text.trim().isNullOrEmpty())
                    txtIngredients.text = txtIngredients.text.toString() + it.text + ". "
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