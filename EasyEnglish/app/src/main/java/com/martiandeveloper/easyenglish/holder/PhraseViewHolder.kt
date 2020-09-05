package com.martiandeveloper.easyenglish.holder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.adapter.PhraseAdapter
import com.martiandeveloper.easyenglish.model.Phrase

class PhraseViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.recyclerview_phrase_item, parent, false)) {
    private var recyclerviewPhraseItemPhraseMTV: MaterialTextView? = null
    private var recyclerviewPhraseItemMeaningMTV: MaterialTextView? = null
    private var recyclerviewPhraseItemMainLL: LinearLayout? = null

    init {
        recyclerviewPhraseItemPhraseMTV =
            itemView.findViewById(R.id.recyclerview_phrase_item_phraseMTV)
        recyclerviewPhraseItemMeaningMTV =
            itemView.findViewById(R.id.recyclerview_phrase_item_meaningMTV)
        recyclerviewPhraseItemMainLL =
            itemView.findViewById(R.id.recyclerview_phrase_item_mainLL)
    }

    fun bind(
        phrase: Phrase,
        itemClickListener: PhraseAdapter.ItemClickListener,
        context: Context,
        phraseMeaning: String?
    ) {

        recyclerviewPhraseItemPhraseMTV?.text = phrase.phrase
        recyclerviewPhraseItemMeaningMTV?.text = phrase.meaning

        if (recyclerviewPhraseItemMainLL != null) {
            if (phrase.meaning == phraseMeaning) {
                recyclerviewPhraseItemMainLL!!.background =
                    ContextCompat.getDrawable(context, R.drawable.background_6)

            } else {
                recyclerviewPhraseItemMainLL!!.background =
                    ContextCompat.getDrawable(context, R.drawable.background_3)
            }
        }

        itemView.setOnClickListener {
            itemClickListener.onItemClick(phrase)
        }
    }
}
