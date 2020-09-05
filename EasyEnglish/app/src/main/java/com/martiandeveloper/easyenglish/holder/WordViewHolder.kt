package com.martiandeveloper.easyenglish.holder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.adapter.WordAdapter
import com.martiandeveloper.easyenglish.model.Word

class WordViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.recyclerview_word_item, parent, false)) {
    private var recyclerviewWordItemWordMTV: MaterialTextView? = null
    private var recyclerviewWordItemMeaningMTV: MaterialTextView? = null
    private var recyclerviewWordItemMainLL: LinearLayout? = null

    init {
        recyclerviewWordItemWordMTV =
            itemView.findViewById(R.id.recyclerview_word_item_wordMTV)
        recyclerviewWordItemMeaningMTV =
            itemView.findViewById(R.id.recyclerview_word_item_meaningMTV)
        recyclerviewWordItemMainLL =
            itemView.findViewById(R.id.recyclerview_word_item_mainLL)
    }

    fun bind(
        word: Word,
        itemClickListener: WordAdapter.ItemClickListener,
        context: Context,
        wordMeaning: String?
    ) {

        recyclerviewWordItemWordMTV?.text = word.word
        recyclerviewWordItemMeaningMTV?.text = word.meaning

        if (recyclerviewWordItemMainLL != null) {
            if (word.meaning == wordMeaning) {
                recyclerviewWordItemMainLL!!.background =
                    ContextCompat.getDrawable(context, R.drawable.background_6)

            } else {
                recyclerviewWordItemMainLL!!.background =
                    ContextCompat.getDrawable(context, R.drawable.background_3)
            }
        }

        itemView.setOnClickListener {
            itemClickListener.onItemClick(word)
        }
    }
}
