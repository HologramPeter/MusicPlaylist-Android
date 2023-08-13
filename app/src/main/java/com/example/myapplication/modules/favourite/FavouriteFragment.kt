package com.example.myapplication.modules.favourite

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adaptor.MusicListAdaptor
import com.example.myapplication.adaptor.MusicListEventListener
import com.example.myapplication.databinding.FragmentFavouriteBinding
import com.example.myapplication.extensions.setFavourite
import com.example.myapplication.request.MusicInfo

class FavouriteFragment : Fragment(), MusicListEventListener {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: FavouriteViewModel
    lateinit var adaptor: MusicListAdaptor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = FavouriteViewModel(view.context)
        setupUI(view.context)
        setListener()
    }

    private fun setupUI(context: Context){
        adaptor = MusicListAdaptor(viewModel.data, viewModel.images, context)
        adaptor.setMusicListEventListener(this)

        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adaptor
        binding.musicList.addView(recyclerView)

        viewModel.loading.observe(viewLifecycleOwner) {
            adaptor.dataSet = viewModel.data
            adaptor.imageMap = viewModel.images
            adaptor.notifyDataSetChanged()
        }
    }

    private fun setListener(){
        binding.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(data: MusicInfo, setFavourite: Boolean, view: View) {
        context?.setFavourite(data, setFavourite)
    }
}