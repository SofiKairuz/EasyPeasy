package com.example.themealapp.ui.recipefavorites

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil.DiffResult.NO_POSITION
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.themealapp.R
import com.example.themealapp.base.BaseViewHolder
import com.example.themealapp.data.model.RecipeEntity
import com.example.themealapp.data.model.asRecipe
import com.example.themealapp.databinding.RecipesRowBinding

class FavoritesAdapter(private val context: Context, private var recipesList: List<RecipeEntity>,
                       private val itemClickListener: OnRecipeClickListener)
    : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnRecipeClickListener {
        fun onRecipeClick(recipe: RecipeEntity, position: Int)
        fun onRecipeDeleteLongClick(recipe: RecipeEntity, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = RecipesRowBinding.inflate(LayoutInflater.from(context), parent, false)
        val vh = FavoritesViewHolder(itemBinding)

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
            is FavoritesViewHolder -> holder.bind(recipesList[position], position)
        }
    }

    private inner class FavoritesViewHolder(val binding: RecipesRowBinding): BaseViewHolder<RecipeEntity>(binding.root) {
        override fun bind(item: RecipeEntity, position: Int) = with(binding) {
            val recipe = item.asRecipe()
            Glide.with(context).load(recipe.image).listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean
                {
                    imgRecipe.setImageResource(R.drawable.ic_baseline_not_interested_24)
                    return true
                }
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean
                {
                    return false
                }
            }).into(imgRecipe);
            txtTitle.text = recipe.name
            txtDescription.text = "Receta de ${recipe.source}"
            recipe.ingredients.forEach {
                txtIngredients.text = txtIngredients.text.toString() + it.text + ". "
            }
            itemView.setOnClickListener { itemClickListener.onRecipeClick(item, position) }
            itemView.setOnLongClickListener {
                itemClickListener.onRecipeDeleteLongClick(item, position)
                return@setOnLongClickListener true
            }
        }
    }
}