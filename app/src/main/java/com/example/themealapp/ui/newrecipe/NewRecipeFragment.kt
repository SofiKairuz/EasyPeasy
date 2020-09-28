package com.example.themealapp.ui.newrecipe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.themealapp.AppDatabase
import com.example.themealapp.R
import com.example.themealapp.data.DataSourceImpl
import com.example.themealapp.data.model.Ingredientes
import com.example.themealapp.data.model.Recipe
import com.example.themealapp.data.model.RecipeEntity
import com.example.themealapp.domain.RepoImpl
import com.example.themealapp.ui.viewmodel.MainViewModel
import com.example.themealapp.ui.viewmodel.VMFactory
import com.example.themealapp.utils.ImageController
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_new_recipe.*

class NewRecipeFragment : Fragment() {

    var imageUri: Uri? = null
    private var recipe: Recipe? = null
    private var recipeID: Int = 0

    private val viewModel by viewModels<MainViewModel> { VMFactory(
        RepoImpl(
            DataSourceImpl(
                AppDatabase.getDatabase(requireActivity().applicationContext))
        )
    ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            recipe = it.getParcelable<Recipe>("recipe")
            recipeID = it.getInt("recipe_id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupIngredientButton()
        saveNewRecipes()
        cancelNewRecipe()
        selectLocalImage()
        if (recipe != null) editRecipeForm()
    }

    private fun editRecipeForm() {
        txt_new_title.setText(recipe!!.name)
        txt_new_calories.setText(recipe!!.totalCalories)
        txt_new_source.setText(recipe!!.source)
        txt_new_description.setText(recipe!!.url)
        txt_new_img.setText(recipe!!.image)
        txt_new_img_gallery.setImageURI(ImageController.getImageUri(requireContext(), recipeID.toString()))
        txt_new_measure_1.setText(recipe!!.ingredients[0].measure)
        txt_new_measure_2.setText(recipe!!.ingredients[1].measure)
        txt_new_measure_3.setText(recipe!!.ingredients[2].measure)
        txt_new_measure_4.setText(recipe!!.ingredients[3].measure)
        txt_new_measure_5.setText(recipe!!.ingredients[4].measure)
        txt_new_measure_6.setText(recipe!!.ingredients[5].measure)
        txt_new_ingredient_1.setText(recipe!!.ingredients[0].food)
        txt_new_ingredient_2.setText(recipe!!.ingredients[1].food)
        txt_new_ingredient_3.setText(recipe!!.ingredients[2].food)
        txt_new_ingredient_4.setText(recipe!!.ingredients[3].food)
        txt_new_ingredient_5.setText(recipe!!.ingredients[4].food)
        txt_new_ingredient_6.setText(recipe!!.ingredients[5].food)
        if (!txt_new_ingredient_1.text.isNullOrEmpty()) layout_ingredient_1.visibility = View.VISIBLE
        if (!txt_new_ingredient_2.text.isNullOrEmpty()) layout_ingredient_2.visibility = View.VISIBLE
        if (!txt_new_ingredient_3.text.isNullOrEmpty()) layout_ingredient_3.visibility = View.VISIBLE
        if (!txt_new_ingredient_4.text.isNullOrEmpty()) layout_ingredient_4.visibility = View.VISIBLE
        if (!txt_new_ingredient_5.text.isNullOrEmpty()) layout_ingredient_5.visibility = View.VISIBLE
        if (!txt_new_ingredient_6.text.isNullOrEmpty()) layout_ingredient_6.visibility = View.VISIBLE
    }


    private fun selectLocalImage() {
        txt_new_img_gallery.setOnClickListener {
            ImageController.selectPhotoFromGallery(this, ImageController.IMAGE_PICK_CODE)
        }
    }

    private fun cancelNewRecipe() {
        btn_new_cancel.setOnClickListener {
            cleanRecipe()
            navigateToMyRecipes()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == ImageController.IMAGE_PICK_CODE) {
            imageUri = data!!.data
            txt_new_img_gallery.setImageURI(imageUri)
            txt_new_img.setText("")
        }
    }

    private fun saveNewRecipes() {
        btn_new_save.setOnClickListener {
            if(validateBlanks()) {
                var imageRecipe = ""
                val newRecipe = createRecipe()
                if (imageUri == null) imageRecipe = newRecipe.image
                var idReceta = viewModel.validateDuplicateRecipe(newRecipe.name, newRecipe.source, imageRecipe, newRecipe.url) ?: 0
                if (recipeID > 0) idReceta = recipeID
                idReceta = viewModel.saveRecipe(RecipeEntity(recipeId = idReceta, name = newRecipe.name, image = newRecipe.image, source = newRecipe.source, ingredients =  Gson().toJson(newRecipe.ingredients), totalCalories = newRecipe.totalCalories, url = newRecipe.url, recipeView = "MY_RECIPES"))!!
                Toast.makeText(requireContext(), "Se guardó la receta correctamente", Toast.LENGTH_SHORT).show()
                saveImageToGallery(idReceta)
                cleanRecipe()
                navigateToMyRecipes()
            }
        }
    }

    private fun saveImageToGallery(idRecipe: Int) {
        imageUri?.let {
            ImageController.saveImage(requireContext(), idRecipe.toString(), it)
        }
    }

    private fun validateBlanks(): Boolean {
        var hasError: Boolean = false
        if (txt_new_title.text.isNullOrEmpty() || txt_new_title.text.isNullOrBlank()){
            txt_new_title.error = "Este campo no puede estar vacío"
            hasError = true
        }
        if (txt_new_description.text.isNullOrEmpty() || txt_new_description.text.isNullOrBlank()){
            txt_new_description.error = "Este campo no puede estar vacío"
            hasError = true
        }
        if (txt_new_source.text.isNullOrEmpty() || txt_new_source.text.isNullOrBlank()){
           txt_new_source.error = "Este campo no puede estar vacío"
            hasError = true
        }
        if (txt_new_ingredient_1.text.isNullOrBlank() || txt_new_ingredient_1.text.isNullOrEmpty()) {
            txt_new_ingredient_1.error = "Este campo no puede estar vacío"
            hasError = true
        }
        if (txt_new_measure_1.text.isNullOrEmpty() || txt_new_measure_1.text.isNullOrBlank()) {
            txt_new_measure_1.error = "Este campo no puede estar vacío"
            hasError = true
        }
        if ((!txt_new_ingredient_2.text.isNullOrEmpty() || !txt_new_ingredient_2.text.isNullOrBlank()) &&
            (txt_new_measure_2.text.isNullOrBlank() || txt_new_measure_2.text.isNullOrEmpty())) {
            txt_new_measure_2.error = "Este campo no puede estar vacío"
            hasError = true
        }
        if ((!txt_new_ingredient_3.text.isNullOrEmpty() || !txt_new_ingredient_3.text.isNullOrBlank()) &&
            (txt_new_measure_3.text.isNullOrBlank() || txt_new_measure_3.text.isNullOrEmpty())) {
            txt_new_measure_3.error = "Este campo no puede estar vacío"
            hasError = true
        }
        if ((!txt_new_ingredient_4.text.isNullOrEmpty() || !txt_new_ingredient_4.text.isNullOrBlank()) &&
            (txt_new_measure_4.text.isNullOrBlank() || txt_new_measure_4.text.isNullOrEmpty())) {
            txt_new_measure_4.error = "Este campo no puede estar vacío"
            hasError = true
        }
        if ((!txt_new_ingredient_5.text.isNullOrEmpty() || !txt_new_ingredient_5.text.isNullOrBlank()) &&
            (txt_new_measure_5.text.isNullOrBlank() || txt_new_measure_5.text.isNullOrEmpty())) {
            txt_new_measure_5.error = "Este campo no puede estar vacío"
            hasError = true
        }
        if ((!txt_new_ingredient_6.text.isNullOrEmpty() || !txt_new_ingredient_6.text.isNullOrBlank()) &&
            (txt_new_measure_6.text.isNullOrBlank() || txt_new_measure_6.text.isNullOrEmpty())) {
            txt_new_measure_6.error = "Este campo no puede estar vacío"
            hasError = true
        }
        if (txt_new_calories.text.isNullOrEmpty() || txt_new_calories.text.isNullOrBlank()) {
            txt_new_calories.setText("0")
            // Si no se cuantas calorías tiene lo setteo en 0 por default
        }
        return hasError.not()
    }

    private fun createRecipe() : Recipe {
        val listIngredients = listOf(
            Ingredientes(txt_new_measure_1.text.toString() + " " + txt_new_ingredient_1.text.toString(), 0.0, txt_new_measure_1.text.toString(), txt_new_ingredient_1.text.toString(), 0.0),
            Ingredientes(txt_new_measure_2.text.toString() + " " + txt_new_ingredient_2.text.toString(), 0.0, txt_new_measure_2.text.toString(), txt_new_ingredient_2.text.toString(), 0.0),
            Ingredientes(txt_new_measure_3.text.toString() + " " + txt_new_ingredient_3.text.toString(), 0.0, txt_new_measure_3.text.toString(), txt_new_ingredient_3.text.toString(), 0.0),
            Ingredientes(txt_new_measure_4.text.toString() + " " + txt_new_ingredient_4.text.toString(), 0.0, txt_new_measure_4.text.toString(), txt_new_ingredient_4.text.toString(), 0.0),
            Ingredientes(txt_new_measure_5.text.toString() + " " + txt_new_ingredient_5.text.toString(), 0.0, txt_new_measure_5.text.toString(), txt_new_ingredient_5.text.toString(), 0.0),
            Ingredientes(txt_new_measure_6.text.toString() + " " + txt_new_ingredient_6.text.toString(), 0.0, txt_new_measure_6.text.toString(), txt_new_ingredient_6.text.toString(), 0.0)
        )
        return Recipe(txt_new_title.text.toString(), txt_new_img.text.toString(), listIngredients, txt_new_source.text.toString(), txt_new_description.text.toString(), txt_new_calories.text.toString())
    }

    private fun setupIngredientButton() {
        btn_new_plus_1.setOnClickListener {
            layout_ingredient_2.visibility = View.VISIBLE
        }
        btn_new_plus_2.setOnClickListener {
            layout_ingredient_3.visibility = View.VISIBLE
        }
        btn_new_plus_3.setOnClickListener {
            layout_ingredient_4.visibility = View.VISIBLE
        }
        btn_new_plus_4.setOnClickListener {
            layout_ingredient_5.visibility = View.VISIBLE
        }
        btn_new_plus_5.setOnClickListener {
            layout_ingredient_6.visibility = View.VISIBLE
        }
    }

    private fun cleanRecipe() {
        txt_new_title.text.clear()
        txt_new_calories.text.clear()
        txt_new_description.text.clear()
        txt_new_img.text.clear()
        txt_new_img_gallery.setImageResource(R.drawable.no_image)
        txt_new_ingredient_1.text.clear()
        txt_new_ingredient_2.text.clear()
        txt_new_ingredient_3.text.clear()
        txt_new_ingredient_4.text.clear()
        txt_new_ingredient_5.text.clear()
        txt_new_ingredient_6.text.clear()
        txt_new_measure_1.text.clear()
        txt_new_measure_2.text.clear()
        txt_new_measure_3.text.clear()
        txt_new_measure_4.text.clear()
        txt_new_measure_5.text.clear()
        txt_new_measure_6.text.clear()
        txt_new_source.text.clear()
    }

    private fun navigateToMyRecipes() {
        findNavController().navigateUp()
    }

}