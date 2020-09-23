package com.martiandeveloper.easyenglish.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.textview.MaterialTextView
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.model.Phrase

class PhraseCardAdapter(private val mContext: Context, private val list: List<Phrase>) :
    ArrayAdapter<Phrase?>(mContext, 0, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView
        if (listItem == null) listItem =
            LayoutInflater.from(mContext).inflate(R.layout.layout_card_phrase, parent, false)

        val layoutPhraseCardPhraseMTV: MaterialTextView =
            listItem!!.findViewById(R.id.layout_phrase_card_phraseMTV)
        val layoutPhraseCardTranscriptionMTV: MaterialTextView =
            listItem.findViewById(R.id.layout_phrase_card_transcriptionMTV)

        layoutPhraseCardPhraseMTV.text = list[position].phrase
        layoutPhraseCardTranscriptionMTV.text = list[position].transcription

        return listItem
    }
}
