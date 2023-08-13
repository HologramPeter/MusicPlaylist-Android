package com.example.myapplication.modules.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adaptor.MusicListAdaptor
import com.example.myapplication.adaptor.MusicListEventListener
import com.example.myapplication.databinding.FragmentFavouriteBinding
import com.example.myapplication.databinding.FragmentSearchBinding
import com.example.myapplication.extensions.setFavourite
import com.example.myapplication.modules.favourite.FavouriteViewModel
import com.example.myapplication.request.MusicInfo

class SearchFragment : Fragment(), MusicListEventListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: SearchViewModel
    lateinit var adaptor: MusicListAdaptor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = SearchViewModel(view.context)
        setupUI(view.context)
        setListener(view.context)
    }


    fun setupUI(context: Context){
        adaptor = MusicListAdaptor(viewModel.displayData, viewModel.images, context)
        adaptor.setMusicListEventListener(this)

        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adaptor
        binding.musicList.addView(recyclerView)

        viewModel.loading.observe(viewLifecycleOwner) {
            reloadData()
        }
    }

    private fun setListener(context: Context){
        binding.searchInput.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus) {
                val inputMethodManager = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)

                val text = binding.searchInput.text.toString()
                if (viewModel.filterMode){
                    viewModel.filter(text)
                    reloadData()
                }else{
                    viewModel.search(text)
                }
            }
        }

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                binding.searchInput.clearFocus()
                true
            } else {
                false
            }
        }

        val onColor = context.resources.getColor(R.color.blue)
        val offColor = context.resources.getColor(R.color.white)

        binding.filterBtn.setOnClickListener{
            viewModel.filterMode = !viewModel.filterMode
            val color = if(viewModel.filterMode) onColor else offColor
            binding.filterBtn.background.setTint(color)
            binding.searchInput.clearFocus()
            if (viewModel.filterMode) {
                binding.searchInput.hint = "filter"
                binding.searchInput.setText(viewModel.filterStr)
            } else {
                binding.searchInput.hint = "search"
                binding.searchInput.setText(viewModel.request.term)
            }
        }
    }

    private fun reloadData(){
        adaptor.dataSet = viewModel.displayData
        adaptor.imageMap = viewModel.images
        adaptor.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(data: MusicInfo, setFavourite: Boolean, view: View) {
        context?.setFavourite(data, setFavourite)
    }
}