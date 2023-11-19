package com.dicoding.musicstory.ui.detailstory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.dicoding.musicstory.R
import com.dicoding.musicstory.constants.Constants
import com.dicoding.musicstory.databinding.ActivityDetailStoryBinding
import com.dicoding.musicstory.response.Story
import com.dicoding.musicstory.utils.withDateFormat

class DetailStoryAct : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailStory = intent.getParcelableExtra<Story>(Constants.DETAIL_STORY) as Story

        setupToolBar()
        setupUi(detailStory)
    }

    private fun setupToolBar() {
        title = resources.getString(R.string.detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun setupUi(detailStory: Story) {
        Glide.with(this@DetailStoryAct)
            .load(detailStory.photoUrl)
            .fitCenter()
            .into(binding.storyImageView)

        detailStory.apply {
            binding.nameTextView.text = name
            binding.descriptionTextView.text = description
            binding.dateTextView.text = createdAt.withDateFormat()
        }
    }
}