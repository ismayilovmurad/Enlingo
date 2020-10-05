package com.martiandeveloper.easyenglish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IntroViewModel : ViewModel() {

    private var _eventNextMBTNClick = MutableLiveData<Boolean>()
    val eventNextMBTNClick: LiveData<Boolean>
        get() = _eventNextMBTNClick

    private var _eventPreviousMBTNClick = MutableLiveData<Boolean>()
    val eventPreviousMBTNClick: LiveData<Boolean>
        get() = _eventPreviousMBTNClick

    private var _isPreviousMBTNGone = MutableLiveData<Boolean>()
    val isPreviousMBTNGone: LiveData<Boolean>
        get() = _isPreviousMBTNGone

    private var _next = MutableLiveData<String>()
    val next: LiveData<String>
        get() = _next


    fun onNextMBTNClick() {
        _eventNextMBTNClick.value = true
    }

    fun onNextMBTNClickComplete() {
        _eventNextMBTNClick.value = false
    }

    fun onPreviousMBTNClick() {
        _eventPreviousMBTNClick.value = true
    }

    fun onPreviousMBTNClickComplete() {
        _eventPreviousMBTNClick.value = false
    }

    fun setPreviousMBTN(gone: Boolean) {
        _isPreviousMBTNGone.value = gone
    }

    fun setNextMBTNText(text: String) {
        _next.value = text
    }
}
