package com.example.themealapp.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.themealapp.AppDatabase
import com.example.themealapp.R
import com.example.themealapp.data.DataSourceImpl
import com.example.themealapp.data.model.Hit
import com.example.themealapp.data.model.RecipeEntity
import com.example.themealapp.databinding.FragmentMainBinding
import com.example.themealapp.domain.RepoImpl
import com.example.themealapp.ui.viewmodel.MainViewModel
import com.example.themealapp.ui.viewmodel.VMFactory
import com.example.themealapp.vo.Resource
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), MainAdapter.OnRecipeClickListener {

    private val viewModel by viewModels<MainViewModel> { VMFactory(RepoImpl(DataSourceImpl(AppDatabase.getDatabase(requireActivity().applicationContext)))) }
    private var _binding: FragmentMainBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        setupObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.favorites -> {
                findNavController().navigate(R.id.action_mainFragment_to_favoritesFragment)
                false
            }
            R.id.myrecipes -> {
                findNavController().navigate(R.id.action_mainFragment_to_myRecipesFragment)
                false
            }
            else -> false
        }
    }

    private fun setupObservers() {
        viewModel.fetchRecipesList.observe(viewLifecycleOwner, Observer { result ->
            when(result) {
                is Resource.Loading -> {
                    binding.emptyContainer.root.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (result.data.isEmpty()) {
                        binding.emptyContainer.root.visibility = View.VISIBLE
                        return@Observer
                    }
                    rv_recipes.adapter = MainAdapter(requireContext(), result.data, this)
                    binding.emptyContainer.root.visibility = View.GONE
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Ocurrió un error al traer los datos de la lista ${result.exception}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setRecipe(query!!)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    private fun setupRecyclerView() {
        binding.rvRecipes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecipes.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }

    override fun onRecipeClick(recipe: Hit, position: Int) {
        val bundle = Bundle()
        bundle.putParcelable("recipe", recipe.recipe)
        findNavController().navigate(R.id.action_mainFragment_to_detailsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}