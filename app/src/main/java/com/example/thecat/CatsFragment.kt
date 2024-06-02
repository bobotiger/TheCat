package com.example.thecat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.thecat.data.CatPictureDatabase
import com.example.thecat.data.CatsLocalRepository
import com.example.thecat.data.CatsNetworkRepository
import com.example.thecat.databinding.FragmentCatsBinding

class CatsFragment : Fragment() {
    private lateinit var _binding : FragmentCatsBinding
    val binding : FragmentCatsBinding get() = _binding

    private lateinit var viewModel: CatsViewModel
    private lateinit var viewModelFactory: CatsViewModelFactory

    private val imageLoader : ImageLoader by lazy {
        GlideImageLoader(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCatsBinding.inflate(inflater,container,false)
        val view = binding.root

        // 获取应用上下文
        val application = requireNotNull(this.activity).application

        val networkRepository = CatsNetworkRepository()

        val localRepository = CatsLocalRepository(
            CatPictureDatabase.getDatabase(application).catPictureDao)

        viewModelFactory = CatsViewModelFactory(networkRepository,localRepository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CatsViewModel::class.java)


        binding.catsViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.curUrl.observe(viewLifecycleOwner, Observer {
            url ->
                imageLoader.loadImage(url, binding.catImage)
        })

        viewModel.catPictures.observe(viewLifecycleOwner, Observer {
                pictures ->
            if (pictures!=null) {
                viewModel.updateCurrentCatPictureUrl()
            }
        })
        return view
    }
}