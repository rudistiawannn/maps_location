package com.example.storiesapplication.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storiesapplication.R
import com.example.storiesapplication.databinding.ItemStoryBinding
import com.example.storiesapplication.response.Story
import com.example.storiesapplication.ui.activity.DetailActivity
import com.example.storiesapplication.util.Constants

class StoryListAdapter : PagingDataAdapter<Story, StoryListAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(holder.adapterPosition)
        val context = holder.itemView.context

        Glide.with(holder.itemView.context)
            .load(data?.photoUrl)
            .placeholder(R.drawable.baseline_image_24)
            .error(R.drawable.baseline_image_24)
            .into(holder.binding.itemStoryIvImg)
        holder.binding.itemStoryIvImg.contentDescription = data?.description

        holder.binding.itemStoryTvName.text = data?.name

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(Constants.MAIN_TO_DETAIL, data)
            context.startActivity(
                intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context as Activity,
                    Pair(
                        holder.binding.itemStoryIvImg,
                        context.getString(R.string.transition_photo)
                    ),
                    Pair(
                        holder.binding.itemStoryTvName,
                        context.getString(R.string.transition_name)
                    ),
                ).toBundle()
            )
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id?.equals(newItem.id) as Boolean
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}
