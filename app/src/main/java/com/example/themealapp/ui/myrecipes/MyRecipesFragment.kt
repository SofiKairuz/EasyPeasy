package com.example.themealapp.ui.myrecipes

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.themealapp.AppDatabase
import com.example.themealapp.R
import com.example.themealapp.data.DataSourceImpl
import com.example.themealapp.data.model.*
import com.example.themealapp.domain.RepoImpl
import com.example.themealapp.ui.viewmodel.MainViewModel
import com.example.themealapp.ui.viewmodel.VMFactory
import com.example.themealapp.vo.Resource
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.android.synthetic.main.fragment_my_recipes.*

class MyRecipesFragment : Fragment(), MyRecipesAdapter.OnRecipeClickListener {

    private val viewModel by viewModels<MainViewModel> { VMFactory(
        RepoImpl(
            DataSourceImpl(
                AppDatabase.getDatabase(requireActivity().applicationContext))
        )
    ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        rv_my_recipes.layoutManager = LinearLayoutManager(requireContext())
        rv_my_recipes.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }

    private fun setupObservers() {
        viewModel.getMyRecipes().observe(viewLifecycleOwner, Observer { result ->
            when(result) {
                is Resource.Loading -> {
                    my_progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    my_progressBar.visibility = View.GONE
                    Log.d("MYRECIPES","${result.data}")
                    onSuccessResource(result.data)
                }
                is Resource.Failure -> {
                    my_progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Ocurrió un error al traer los datos de la lista ${result.exception}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun onSuccessResource(entity: List<RecipeEntity>) {
        rv_my_recipes.adapter =
            MyRecipesAdapter(
                requireContext(),
                entity,
                this
            )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.myrecipes_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.addRecipe -> {
                val bundle = Bundle()
                bundle.putString("recipe", null)
                bundle.putInt("recipe_id", 0)
                findNavController().navigate(R.id.action_myRecipesFragment_to_newRecipeFragment, bundle)
                false
            }
            else -> false
        }
    }

    override fun onRecipeClick(recipe: RecipeEntity, position: Int) {
        val bundle = Bundle()
        bundle.putParcelable("recipe", recipe.asRecipe())
        bundle.putInt("recipeId", recipe.recipeId)
        findNavController().navigate(R.id.action_myRecipesFragment_to_detailsFragment, bundle)
    }

    override fun onRecipeDeleteLongClick(recipe: RecipeEntity, position: Int) {
        viewModel.deleteMyRecipe(recipe).observe(viewLifecycleOwner, Observer { result ->
            when(result) {
                is Resource.Loading -> {
                    my_progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    my_progressBar.visibility = View.GONE
                    onSuccessResource(result.data)
                    rv_my_recipes.adapter?.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Se eliminó el registro correctamente", Toast.LENGTH_LONG).show()
                }
                is Resource.Failure -> {
                    my_progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Ocurrió un error al eliminar el registro. ${result.exception}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

}