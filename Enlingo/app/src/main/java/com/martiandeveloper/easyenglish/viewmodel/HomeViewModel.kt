package com.martiandeveloper.easyenglish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private var _eventWordsMCVClick = MutableLiveData<Boolean>()
    val eventWordsMCVClick: LiveData<Boolean>
        get() = _eventWordsMCVClick

    private var _eventPhrasesMCVClick = MutableLiveData<Boolean>()
    val eventPhrasesMCVClick: LiveData<Boolean>
        get() = _eventPhrasesMCVClick

    private var _eventTestMCVClick = MutableLiveData<Boolean>()
    val eventTestMCVClick: LiveData<Boolean>
        get() = _eventTestMCVClick

    private var _eventRateMCVClick = MutableLiveData<Boolean>()
    val eventRateMCVClick: LiveData<Boolean>
        get() = _eventRateMCVClick


    fun onWordsMCVClick() {
        _eventWordsMCVClick.value = true
    }

    fun onWordsMCVClickComplete() {
        _eventWordsMCVClick.value = false
    }

    fun onPhrasesMCVClick() {
        _eventPhrasesMCVClick.value = true
    }

    fun onPhrasesMCVClickComplete() {
        _eventPhrasesMCVClick.value = false
    }

    fun onTestMCVClick() {
        _eventTestMCVClick.value = true
    }

    fun onTestMCVClickComplete() {
        _eventTestMCVClick.value = false
    }

    fun onRateMCVClick() {
        _eventRateMCVClick.value = true
    }

    fun onRateMCVClickComplete() {
        _eventRateMCVClick.value = false
    }
}
