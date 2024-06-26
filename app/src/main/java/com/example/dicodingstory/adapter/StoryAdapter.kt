package com.example.dicodingstory.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingstory.data.story.response.ListStoryItem
import com.example.dicodingstory.databinding.StoryItemBinding
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import com.example.dicodingstory.view.DetailActivity

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.ListViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val storyItem = getItem(position)
        val photo = storyItem?.photoUrl
        val name = storyItem?.name
        val description = storyItem?.description

        Glide.with(holder.itemView.context)
            .load(photo)
            .into(holder.binding.imgItemPhoto)
        holder.binding.tvItemName.text = name
        holder.binding.tvItemDescription.text = description

        holder.binding.imgItemPhoto.transitionName = "sharedImage_${storyItem?.id}"
        holder.binding.tvItemName.transitionName = "sharedName_${storyItem?.id}"
        holder.binding.tvItemDescription.transitionName = "sharedDescription_${storyItem?.id}"

        holder.itemView.setOnClickListener {
            val detailIntent = Intent(
                holder.itemView.context,
                DetailActivity::class.java
            ).apply {
                putExtra(DetailActivity.PHOTO, photo)
                putExtra(DetailActivity.NAME, name)
                putExtra(DetailActivity.DESCRIPTION, description)

                putExtra(DetailActivity.TRANSITION_NAME_IMAGE, holder.binding.imgItemPhoto.transitionName)
                putExtra(DetailActivity.TRANSITION_NAME_NAME, holder.binding.tvItemName.transitionName)
                putExtra(DetailActivity.TRANSITION_NAME_DESCRIPTION, holder.binding.tvItemDescription.transitionName)
            }

            val context = holder.itemView.context

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                context as Activity,
                Pair(holder.binding.imgItemPhoto, holder.binding.imgItemPhoto.transitionName),
                Pair(holder.binding.tvItemName, holder.binding.tvItemName.transitionName),
                Pair(holder.binding.tvItemDescription, holder.binding.tvItemDescription.transitionName)
            )
            context.startActivity(detailIntent, options.toBundle())
        }
    }

    class ListViewHolder(var binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}