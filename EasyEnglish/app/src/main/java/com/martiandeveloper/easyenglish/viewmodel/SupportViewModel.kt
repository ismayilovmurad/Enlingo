package com.martiandeveloper.easyenglish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martiandeveloper.easyenglish.BuildConfig

class SupportViewModel : ViewModel() {

    private var _version = MutableLiveData<String>()
    val version: LiveData<String>
        get() = _version

    private var _eventAboutUsMCVClick = MutableLiveData<Boolean>()
    val eventAboutUsMCVClick: LiveData<Boolean>
        get() = _eventAboutUsMCVClick

    private var _eventSendUsMessageMCVClick = MutableLiveData<Boolean>()
    val eventSendUsMessageMCVClick: LiveData<Boolean>
        get() = _eventSendUsMessageMCVClick

    private var _eventInviteFriendMCVClick = MutableLiveData<Boolean>()
    val eventInviteFriendMCVClick: LiveData<Boolean>
        get() = _eventInviteFriendMCVClick


    init {
        _version.value = BuildConfig.VERSION_NAME
    }

    fun onAboutUsMCVClick() {
        _eventAboutUsMCVClick.value = true
    }

    fun onAboutUsMCVClickComplete() {
        _eventAboutUsMCVClick.value = false
    }

    fun onSendUsMessageMCVClick() {
        _eventSendUsMessageMCVClick.value = true
    }

    fun onSendUsMessageMCVClickComplete() {
        _eventSendUsMessageMCVClick.value = false
    }

    fun onInviteFriendMCVClick() {
        _eventInviteFriendMCVClick.value = true
    }

    fun onInviteFriendMCVClickComplete() {
        _eventInviteFriendMCVClick.value = false
    }
}
