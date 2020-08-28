package com.martiandeveloper.easyenglish.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.model.Phrase
import java.util.*

@SuppressLint("StaticFieldLeak")
class PhraseViewModel(val context: Context) : ViewModel() {

    val phraseList = MutableLiveData<ArrayList<Phrase>>()

    fun fillTheList() {
        val phrases = context.resources.getStringArray(R.array.phrases)
        val phraseMeanings = context.resources.getStringArray(R.array.phrase_meanings)
        val phraseTranscriptions = context.resources.getStringArray(R.array.phrase_transcriptions)

        val list = ArrayList<Phrase>()

        for (i in phrases.indices) {
            list.add(Phrase(phrases[i], phraseMeanings[i], phraseTranscriptions[i]))
        }

        phraseList.value = list
    }
}
