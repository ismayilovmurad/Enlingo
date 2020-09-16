package com.martiandeveloper.easyenglish.holder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.adapter.PhraseAdapter
import com.martiandeveloper.easyenglish.databinding.RecyclerviewPhraseItemBinding
import com.martiandeveloper.easyenglish.model.Phrase

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
