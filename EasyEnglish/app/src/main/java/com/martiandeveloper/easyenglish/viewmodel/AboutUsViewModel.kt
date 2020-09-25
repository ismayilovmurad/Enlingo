package com.martiandeveloper.easyenglish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutUsViewModel : ViewModel() {

    private var _eventFacebookIVClick = MutableLiveData<Boolean>()
    val eventFacebookIVClick: LiveData<Boolean>
        get() = _eventFacebookIVClick

    private var _eventInstagramIVClick = MutableLiveData<Boolean>()
    val eventInstagramIVClick: LiveData<Boolean>
        get() = _eventInstagramIVClick

    private var _eventPrivacyPolicyMTVClick = MutableLiveData<Boolean>()
    val eventPrivacyPolicyMTVClick: LiveData<Boolean>
        get() = _eventPrivacyPolicyMTVClick

    private var _eventTermsConditionsMTVClick = MutableLiveData<Boolean>()
    val eventTermsConditionsMTVClick: LiveData<Boolean>
        get() = _eventTermsConditionsMTVClick

    private var _eventFlaticonIVClick = MutableLiveData<Boolean>()
    val eventFlaticonIVClick: LiveData<Boolean>
        get() = _eventFlaticonIVClick


    fun onFacebookIVClick() {
        _eventFacebookIVClick.value = true
    }

    fun onFacebookIVClickComplete() {
        _eventFacebookIVClick.value = false
    }

    fun onInstagramIVClick() {
        _eventInstagramIVClick.value = true
    }

    fun onInstagramIVClickComplete() {
        _eventInstagramIVClick.value = false
    }

    fun onPrivacyPolicyMTVClick() {
        _eventPrivacyPolicyMTVClick.value = true
    }

    fun onPrivacyPolicyMTVClickComplete() {
        _eventPrivacyPolicyMTVClick.value = false
    }

    fun onTermsConditionsMTVClick() {
        _eventTermsConditionsMTVClick.value = true
    }

    fun onTermsConditionsMTVClickComplete() {
        _eventTermsConditionsMTVClick.value = false
    }

    fun onFlaticonIVClick() {
        _eventFlaticonIVClick.value = true
    }

    fun onFlaticonIVClickComplete() {
        _eventFlaticonIVClick.value = false
    }
}
