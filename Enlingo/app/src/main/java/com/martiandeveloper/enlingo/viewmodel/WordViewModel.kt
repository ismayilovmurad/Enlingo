package com.martiandeveloper.enlingo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WordViewModel : ViewModel() {

    private var _eventSoundFLClick = MutableLiveData<Boolean>()
    val eventSoundFLClick: LiveData<Boolean>
        get() = _eventSoundFLClick

    private var _eventListFABClick = MutableLiveData<Boolean>()
    val eventListFABClick: LiveData<Boolean>
        get() = _eventListFABClick

    private var _isListFABGone = MutableLiveData<Boolean>()
    val isListFABGone: LiveData<Boolean>
        get() = _isListFABGone

    private var _eventRestartFABClick = MutableLiveData<Boolean>()
    val eventRestartFABClick: LiveData<Boolean>
        get() = _eventRestartFABClick

    private var _isRestartFABGone = MutableLiveData<Boolean>()
    val isRestartFABGone: LiveData<Boolean>
        get() = _isRestartFABGone

    private var _eventMainFABClick = MutableLiveData<Boolean>()
    val eventMainFABClick: LiveData<Boolean>
        get() = _eventMainFABClick

    private var _isListMTVGone = MutableLiveData<Boolean>()
    val isListMTVGone: LiveData<Boolean>
        get() = _isListMTVGone

    private var _isRestartMTVGone = MutableLiveData<Boolean>()
    val isRestartMTVGone: LiveData<Boolean>
        get() = _isRestartMTVGone

    private var _wordMeaning = MutableLiveData<String>()
    val wordMeaning: LiveData<String>
        get() = _wordMeaning

    private var _eventStartAgainMBTNClick = MutableLiveData<Boolean>()
    val eventStartAgainMBTNClick: LiveData<Boolean>
        get() = _eventStartAgainMBTNClick

    private var _eventHomeMBTNClick = MutableLiveData<Boolean>()
    val eventHomeMBTNClick: LiveData<Boolean>
        get() = _eventHomeMBTNClick

    private var _eventYesMBTNClick = MutableLiveData<Boolean>()
    val eventYesMBTNClick: LiveData<Boolean>
        get() = _eventYesMBTNClick

    private var _eventNoMBTNClick = MutableLiveData<Boolean>()
    val eventNoMBTNClick: LiveData<Boolean>
        get() = _eventNoMBTNClick

    private var _fabOpen = MutableLiveData<Boolean>()
    val fabOpen: LiveData<Boolean>
        get() = _fabOpen

    fun onSoundFLClick() {
        _eventSoundFLClick.value = true
    }

    fun onSoundFLClickComplete() {
        _eventSoundFLClick.value = false
    }

    fun onListFABClick() {
        _eventListFABClick.value = true
    }

    fun onListFABClickComplete() {
        _eventListFABClick.value = false
    }

    fun setListFAB(gone: Boolean) {
        _isListFABGone.value = gone
    }

    fun onRestartFABClick() {
        _eventRestartFABClick.value = true
    }

    fun onRestartFABClickComplete() {
        _eventRestartFABClick.value = false
    }

    fun setRestartFAB(gone: Boolean) {
        _isRestartFABGone.value = gone
    }

    fun onMainFABClick() {
        _eventMainFABClick.value = true
    }

    fun onMainFABClickComplete() {
        _eventMainFABClick.value = false
    }

    fun setListMTV(gone: Boolean) {
        _isListMTVGone.value = gone
    }

    fun setRestartMTV(gone: Boolean) {
        _isRestartMTVGone.value = gone
    }

    fun setWordMeaningMTVText(text: String) {
        _wordMeaning.value = text
    }

    fun onStartAgainMBTNClick() {
        _eventStartAgainMBTNClick.value = true
    }

    fun onStartAgainMBTNClickComplete() {
        _eventStartAgainMBTNClick.value = false
    }

    fun onHomeMBTNClick() {
        _eventHomeMBTNClick.value = true
    }

    fun onHomeMBTNClickComplete() {
        _eventHomeMBTNClick.value = false
    }

    fun onYesMBTNClick() {
        _eventYesMBTNClick.value = true
    }

    fun onYesMBTNClickComplete() {
        _eventYesMBTNClick.value = false
    }

    fun onNoMBTNClick() {
        _eventNoMBTNClick.value = true
    }

    fun onNoMBTNClickComplete() {
        _eventNoMBTNClick.value = false
    }

    fun setFabOpen(open: Boolean) {
        _fabOpen.value = open
    }
}
