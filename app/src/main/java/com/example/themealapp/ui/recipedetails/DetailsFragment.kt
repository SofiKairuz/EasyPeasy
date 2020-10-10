package com.example.themealapp.ui.recipedetails

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.themealapp.AppDatabase
import com.example.themealapp.R
import com.example.themealapp.data.DataSourceImpl
import com.example.themealapp.data.model.Recipe
import com.example.themealapp.data.model.RecipeEntity
import com.example.themealapp.domain.RepoImpl
import com.example.themealapp.ui.viewmodel.MainViewModel
import com.example.themealapp.ui.viewmodel.VMFactory
import com.example.themealapp.utils.ImageController
import com.example.themealapp.utils.TTSManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_details.*
import java.math.RoundingMode

class DetailsFragment : Fragment() {

    private lateinit var mTTS: TTSManager
    private lateinit var recipe: Recipe
    private var idRecipe: Int = 0

    private val viewModel by viewModels<MainViewModel> { VMFactory(RepoImpl(DataSourceImpl(AppDatabase.getDatabase(requireActivity().applicationContext)))) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTTS = TTSManager(requireContext())
        requireArguments().let {
            recipe = it.getParcelable<Recipe>("recipe")!!
            idRecipe = it.getInt("recipeId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().currentBackStackEntry?.savedStateHandle?.get<Recipe>("updateRecipe")?.let {
            recipe = it
        }

        setupRecipeDetail()
        validateDescriptionDetail()
        navigateToRecipeUri()
        addRecipeToFavoritesEditRecipe()
        speakIngredients()
        speakPreparation()
        onImageClick()

    }

    @SuppressLint("SetTextI18n")
    private fun setupRecipeDetail() {
        if (recipe.image.startsWith("http://") || recipe.image.startsWith("https://")) {
            Glide.with(requireContext()).load(recipe.image).centerCrop().into(img_recipe_detail)
        } else {
            img_recipe_detail.setImageURI(ImageController.getImageUri(requireContext(), idRecipe.toString()))
        }
        txt_title_detail.text = recipe.name
        txt_description_detail.text = recipe.source
        recipe.ingredients.forEach {
            txt_ingredients_detail.text = txt_ingredients_detail.text.toString() + it.text + "\n"
        }
        val calories = recipe.totalCalories.replace(",",".").toDouble()
        val roundedCalories = calories.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
        txt_category_detail.text = "Total de calorias: $roundedCalories"
    }

    private fun onImageClick() {
        img_recipe_detail.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("recipe_id", idRecipe)
            bundle.putString("recipe", recipe.image)
            findNavController().navigate(R.id.action_detailsFragment_to_imageFragment, bundle)
        }
    }

    private fun addRecipeToFavoritesEditRecipe() {
        btn_favorites_edit.setOnClickListener {
            if (recipe.url.startsWith("http://") || recipe.url.startsWith("https://")) {
                // GUARDO LA RECETA
                val idReceta = viewModel.validateDuplicateRecipe(recipe.name, recipe.source, recipe.image, recipe.url) ?: 0
                viewModel.saveRecipe(RecipeEntity(recipeId = idReceta, name = recipe.name, image = recipe.image, source = recipe.source, ingredients =  Gson().toJson(recipe.ingredients), totalCalories = recipe.totalCalories, url = recipe.url, recipeView = "FAVORITES"))
                Toast.makeText(requireContext(), "Se añadió la receta a la sección de Favoritos", Toast.LENGTH_SHORT).show()
            } else {
                // EDITO LA RECETA
                val bundle = Bundle()
                bundle.putParcelable("recipe", recipe)
                bundle.putInt("recipe_id", idRecipe)
                findNavController().navigate(R.id.action_detailsFragment_to_newRecipeFragment, bundle)
            }
        }
    }

    private fun navigateToRecipeUri() {
        btn_url_recipe.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.url))
            startActivity(i)
        }
    }

    private fun speakIngredients() {
        btn_tts.setOnClickListener {
            mTTS.addQueue(txt_ingredients_detail.text.toString())
        }
    }

    private fun speakPreparation() {
        btn_tts_prep.setOnClickListener {
            mTTS.addQueue(txt_prep_detail.text.toString())
        }
    }

    private fun validateDescriptionDetail() {
        if (recipe.url.startsWith("http://") || recipe.url.startsWith("https://")) {
            btn_url_recipe.visibility = View.VISIBLE
            layout_preparation.visibility = View.GONE
            txt_prep_detail.visibility = View.GONE
        }
        else {
            txt_prep_detail.text = recipe.url
            btn_url_recipe.visibility = View.GONE
            layout_preparation.visibility = View.VISIBLE
            txt_prep_detail.visibility = View.VISIBLE
            btn_favorites_edit.setImageResource(R.drawable.ic_baseline_edit_24)
        }
    }

}
