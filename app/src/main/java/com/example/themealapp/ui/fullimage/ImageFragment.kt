package com.example.themealapp.ui.fullimage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.themealapp.R
import com.example.themealapp.data.model.Recipe
import com.example.themealapp.utils.ImageController
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_image.*

class ImageFragment : Fragment() {

    private lateinit var imageRecipe: String
    private var idRecipe: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            imageRecipe = it.getString("recipe")!!
            idRecipe = it.getInt("recipe_id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupImage()
    }

    private fun setupImage() {
        if (imageRecipe.startsWith("http://") || imageRecipe.startsWith("https://"))
            Glide.with(requireContext()).load(imageRecipe).into(full_image)
        else
            full_image.setImageURI(ImageController.getImageUri(requireContext(), idRecipe.toString()))
    }

}