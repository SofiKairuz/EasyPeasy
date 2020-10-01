package com.example.themealapp.ui.recipefavorites

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.themealapp.AppDatabase
import com.example.themealapp.R
import com.example.themealapp.data.DataSourceImpl
import com.example.themealapp.data.model.*
import com.example.themealapp.databinding.FragmentFavoritesBinding
import com.example.themealapp.domain.RepoImpl
import com.example.themealapp.ui.MainAdapter
import com.example.themealapp.ui.viewmodel.MainViewModel
import com.example.themealapp.ui.viewmodel.VMFactory
import com.example.themealapp.vo.Resource
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_favorites.*

class FavoritesFragment : Fragment(), FavoritesAdapter.OnRecipeClickListener {

    private val viewModel by viewModels<MainViewModel> { VMFactory(RepoImpl(DataSourceImpl(AppDatabase.getDatabase(requireActivity().applicationContext)))) }
    private var _binding: FragmentFavoritesBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.getFavorites().observe(viewLifecycleOwner, Observer { result ->
            when(result) {
                is Resource.Loading -> {
                    binding.favProgressBar.visibility = View.VISIBLE
                    binding.favEmptyContainer.root.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.favProgressBar.visibility = View.GONE
                    if (result.data.isEmpty()) {
                        binding.favEmptyContainer.root.visibility = View.VISIBLE
                        return@Observer
                    }
                    onSuccessResource(result.data)
                }
                is Resource.Failure -> {
                    binding.favProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Ocurrió un error al traer los datos de la lista ${result.exception}", Toast.LENGTH_LONG).show()
                    binding.favEmptyContainer.root.visibility = View.GONE
                }
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvFavRecipes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavRecipes.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }

    private fun onSuccessResource(entity: List<RecipeEntity>) {
        binding.rvFavRecipes.adapter = FavoritesAdapter(requireContext(), entity, this)
    }

    override fun onRecipeClick(recipe: RecipeEntity, position: Int) {
        val bundle = Bundle()
        bundle.putParcelable("recipe", recipe.asRecipe())
        findNavController().navigate(R.id.action_favoritesFragment_to_detailsFragment, bundle)
    }

    override fun onRecipeDeleteLongClick(recipe: RecipeEntity, position: Int) {
        viewModel.deleteFavorite(recipe).observe(viewLifecycleOwner, Observer { result ->
            when(result) {
                is Resource.Loading -> {
                    binding.favProgressBar.visibility = View.VISIBLE
                    binding.favEmptyContainer.root.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.favProgressBar.visibility = View.GONE
                    if (result.data.isEmpty()) {
                        binding.favEmptyContainer.root.visibility = View.VISIBLE
                        return@Observer
                    }
                    onSuccessResource(result.data)
                    binding.rvFavRecipes.adapter?.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Se eliminó el registro correctamente", Toast.LENGTH_SHORT).show()
                }
                is Resource.Failure -> {
                    binding.favProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Ocurrió un error al eliminar el registro. ${result.exception}", Toast.LENGTH_LONG).show()
                    binding.favEmptyContainer.root.visibility = View.GONE
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}