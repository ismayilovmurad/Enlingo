package com.martiandeveloper.easyenglish.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.model.Word
import java.util.*

@SuppressLint("StaticFieldLeak")
class WordViewModel(val context: Context) : ViewModel() {

    val wordList = MutableLiveData<ArrayList<Word>>()

    fun fillTheList() {
        val words = context.resources.getStringArray(R.array.words)
        val wordMeanings = context.resources.getStringArray(R.array.word_meanings)
        val wordTranscriptions = context.resources.getStringArray(R.array.word_transcriptions)

        val list = ArrayList<Word>()

        for (i in words.indices) {
            list.add(Word(words[i], wordMeanings[i], wordTranscriptions[i]))
        }

        wordList.value = list
    }
}
