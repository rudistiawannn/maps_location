package com.example.storiesapplication.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.storiesapplication.R
import com.example.storiesapplication.databinding.ActivityDetailBinding
import com.example.storiesapplication.response.Story
import com.example.storiesapplication.util.Constants

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val story = intent.getParcelableExtra<Story>(Constants.MAIN_TO_DETAIL)

        story?.let {
            Glide.with(this)
                .load(story.photoUrl)
                .placeholder(R.drawable.baseline_image_24)
                .error(R.drawable.baseline_image_24)
                .into(binding.detailIvPhoto)
            binding.detailIvPhoto.contentDescription = story.description

            binding.detailTvName.text = story.name
            binding.detailTvDescription.text = story.description
        } ?: run {
            Toast.makeText(this, getString(R.string.data_invalid), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}