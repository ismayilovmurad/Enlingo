package com.martiandeveloper.easyenglish.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.textview.MaterialTextView
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.model.Word

class WordCardAdapter(private val mContext: Context, private val list: List<Word>) :
    ArrayAdapter<Word?>(mContext, 0, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView
        if (listItem == null) listItem =
            LayoutInflater.from(mContext).inflate(R.layout.layout_word_card, parent, false)

        val layoutWordCardWordMTV: MaterialTextView =
            listItem!!.findViewById(R.id.layout_word_card_wordMTV)
        val layoutWordCardTranscriptionMTV: MaterialTextView =
            listItem.findViewById(R.id.layout_word_card_transcriptionMTV)

        layoutWordCardWordMTV.text = list[position].word
        layoutWordCardTranscriptionMTV.text = list[position].transcription

        return listItem
    }
}
