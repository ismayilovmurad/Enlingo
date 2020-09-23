package com.martiandeveloper.easyenglish.holder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.adapter.WordAdapter
import com.martiandeveloper.easyenglish.databinding.RecyclerviewWordItemBinding
import com.martiandeveloper.easyenglish.model.Word

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
