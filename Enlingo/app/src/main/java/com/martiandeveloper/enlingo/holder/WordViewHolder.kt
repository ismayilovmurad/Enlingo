package com.martiandeveloper.enlingo.holder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.martiandeveloper.enlingo.R
import com.martiandeveloper.enlingo.adapter.WordAdapter
import com.martiandeveloper.enlingo.databinding.RecyclerviewWordItemBinding
import com.martiandeveloper.enlingo.model.Word

class WordViewHolder(private val binding: RecyclerviewWordItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        word: Word,
        itemClickListener: WordAdapter.ItemClickListener,
        context: Context,
        wordMeaning: String?
    ) {

        binding.word = word.word
        binding.meaning = word.meaning

        if (word.meaning == wordMeaning) {
            binding.recyclerviewWordItemMainLL.background =
                ContextCompat.getDrawable(context, R.drawable.background_6)
        } else {
            binding.recyclerviewWordItemMainLL.background =
                ContextCompat.getDrawable(context, R.drawable.background_3)
        }

        itemView.setOnClickListener {
            itemClickListener.onItemClick(word)
        }
    }
}
