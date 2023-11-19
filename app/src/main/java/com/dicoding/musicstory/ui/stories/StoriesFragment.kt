package com.dicoding.musicstory.ui.stories

import StoryListAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.res.Configuration
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.musicstory.adapter.LoadAdapter
import com.dicoding.musicstory.databinding.FragmentStoriesBinding
import com.dicoding.musicstory.ui.createstory.CreateStoryAct
import com.dicoding.musicstory.utils.FactoryVM

class StoriesFragment : Fragment() {

    private var _binding: FragmentStoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var listStoriesAdapter: StoryListAdapter
    private lateinit var factory: FactoryVM
    private val homeViewModel: StoriesVM by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoriesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewModel()
        setupView(root.context)
        getStories()
        createStoryButtonHandler()

        return root
    }

    private fun setupViewModel() {
        factory = FactoryVM.getInstance(binding.root.context)
    }

    private fun setupView(context: Context) {
        val storiesRv = binding.storiesRv

        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            storiesRv.layoutManager = GridLayoutManager(context, 2)
        } else {
            storiesRv.layoutManager = LinearLayoutManager(context)
        }

        listStoriesAdapter = StoryListAdapter()
        storiesRv.adapter = listStoriesAdapter
    }

    private fun getStories() {
        binding.storiesRv.adapter = listStoriesAdapter.withLoadStateFooter(
            footer = LoadAdapter {
                listStoriesAdapter.retry()
            }
        )

        homeViewModel.getListStory.observe(viewLifecycleOwner) {
            listStoriesAdapter.submitData(lifecycle, it)
        }
    }

    private fun createStoryButtonHandler() {
        binding.createStoryButton.setOnClickListener {
            val intent = Intent(binding.root.context, CreateStoryAct::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}