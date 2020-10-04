package com.martiandeveloper.enlingo.holder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.martiandeveloper.enlingo.R
import com.martiandeveloper.enlingo.adapter.PhraseAdapter
import com.martiandeveloper.enlingo.databinding.RecyclerviewPhraseItemBinding
import com.martiandeveloper.enlingo.model.Phrase

class PhraseViewHolder(private val binding: RecyclerviewPhraseItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        phrase: Phrase,
        itemClickListener: PhraseAdapter.ItemClickListener,
        context: Context,
        phraseMeaning: String?
    ) {

        binding.phrase = phrase.phrase
        binding.meaning = phrase.meaning

        if (phrase.meaning == phraseMeaning) {
            binding.recyclerviewPhraseItemMainLL.background =
                ContextCompat.getDrawable(context, R.drawable.background_6)
        } else {
            binding.recyclerviewPhraseItemMainLL.background =
                ContextCompat.getDrawable(context, R.drawable.background_3)
        }

        itemView.setOnClickListener {
            itemClickListener.onItemClick(phrase)
        }
    }
}
