package com.example.themealapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.themealapp.R
import com.example.themealapp.base.BaseViewHolder
import com.example.themealapp.data.model.Hit
import kotlinx.android.synthetic.main.recipes_row.view.*

class MainAdapter(private val context: Context, private var recipesList: List<Hit>,
                  private val itemClickListener: OnRecipeClickListener)
    : Adapter<BaseViewHolder<*>>() {

    interface OnRecipeClickListener {
        fun onRecipeClick(recipe: Hit, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return MainViewHolder(LayoutInflater.from(context).inflate(R.layout.recipes_row, parent, false))
    }

    override fun getItemCount(): Int {
        return recipesList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder) {
            is MainViewHolder -> holder.bind(recipesList[position], position)
        }
    }

    inner class MainViewHolder(itemView: View): BaseViewHolder<Hit>(itemView) {
        @SuppressLint("SetTextI18n")
        override fun bind(item: Hit, position: Int) {
            Glide.with(context).load(item.recipe.image).listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?,model: Any?,target: Target<Drawable>?,isFirstResource: Boolean): Boolean
                {
                    itemView.img_recipe.setImageResource(R.drawable.ic_baseline_not_interested_24)
                    return true
                }
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean
                {
                    return false
                }
            }).into(itemView.img_recipe);
            itemView.txt_title.text = item.recipe.name
            itemView.txt_description.text = "Receta de ${item.recipe.source}"
            item.recipe.ingredients.forEach {
                itemView.txt_ingredients.text = itemView.txt_ingredients.text.toString() + it.text + ". "
            }
            itemView.setOnClickListener { itemClickListener.onRecipeClick(item, position) }
        }
    }

}