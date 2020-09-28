package com.example.themealapp.ui.recipefavorites

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.themealapp.R
import com.example.themealapp.base.BaseViewHolder
import com.example.themealapp.data.model.RecipeEntity
import com.example.themealapp.data.model.asRecipe
import kotlinx.android.synthetic.main.recipes_row.view.*

class FavoritesAdapter(private val context: Context, private var recipesList: List<RecipeEntity>,
                       private val itemClickListener: OnRecipeClickListener)
    : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnRecipeClickListener {
        fun onRecipeClick(recipe: RecipeEntity, position: Int)
        fun onRecipeDeleteLongClick(recipe: RecipeEntity, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return FavoritesViewHolder(LayoutInflater.from(context).inflate(R.layout.recipes_row, parent, false))
    }

    override fun getItemCount(): Int {
        return recipesList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder) {
            is FavoritesViewHolder -> holder.bind(recipesList[position], position)
        }
    }

    inner class FavoritesViewHolder(itemView: View): BaseViewHolder<RecipeEntity>(itemView) {
        override fun bind(item: RecipeEntity, position: Int) {
            val recipe = item.asRecipe()
            Glide.with(context).load(recipe.image).listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean
                {
                    itemView.img_recipe.setImageResource(R.drawable.ic_baseline_not_interested_24)
                    return true
                }
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean
                {
                    return false
                }
            }).into(itemView.img_recipe);
            itemView.txt_title.text = recipe.name
            itemView.txt_description.text = "Receta de ${recipe.source}"
            recipe.ingredients.forEach {
                itemView.txt_ingredients.text = itemView.txt_ingredients.text.toString() + it.text + ". "
            }
            itemView.setOnClickListener { itemClickListener.onRecipeClick(item, position) }
            itemView.setOnLongClickListener {
                itemClickListener.onRecipeDeleteLongClick(item, position)
                return@setOnLongClickListener true
            }
        }
    }
}