package com.martiandeveloper.easyenglish.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.databinding.FragmentAboutUsBinding
import com.martiandeveloper.easyenglish.utils.*
import com.martiandeveloper.easyenglish.viewmodel.AboutUsViewModel

class AboutUsFragment : Fragment() {

    private lateinit var aboutUsBinding: FragmentAboutUsBinding

    private lateinit var aboutUsViewModel: AboutUsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        aboutUsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_about_us, container, false)
        return aboutUsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        aboutUsViewModel = getViewModel()
        aboutUsBinding.aboutUsViewModel = aboutUsViewModel
        aboutUsBinding.lifecycleOwner = viewLifecycleOwner
        observe()
    }

    private fun getViewModel(): AboutUsViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AboutUsViewModel() as T
            }
        })[AboutUsViewModel::class.java]
    }

    private fun observe() {
        aboutUsViewModel.eventFacebookIVClick.observe(viewLifecycleOwner, {
            if (it) {
                open(FACEBOOK_URL)
            }
        })

        aboutUsViewModel.eventInstagramIVClick.observe(viewLifecycleOwner, {
            if (it) {
                open(INSTAGRAM_URL)
            }
        })

        aboutUsViewModel.eventPrivacyPolicyMTVClick.observe(viewLifecycleOwner, {
            if (it) {
                open(PRIVACY_POLICY_URL)
            }
        })

        aboutUsViewModel.eventTermsConditionsMTVClick.observe(viewLifecycleOwner, {
            if (it) {
                open(TERMS_CONDITIONS_URL)
            }
        })

        aboutUsViewModel.eventFlaticonIVClick.observe(viewLifecycleOwner, {
            if (it) {
                open(FLATICON_URL)
            }
        })
    }

    private fun open(url: String) {
        val openIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(openIntent)

        when (url) {
            FACEBOOK_URL -> aboutUsViewModel.onFacebookIVClickComplete()
            INSTAGRAM_URL -> aboutUsViewModel.onInstagramIVClickComplete()
            PRIVACY_POLICY_URL -> aboutUsViewModel.onPrivacyPolicyMTVClickComplete()
            TERMS_CONDITIONS_URL -> aboutUsViewModel.onTermsConditionsMTVClickComplete()
            FLATICON_URL -> aboutUsViewModel.onFlaticonIVClickComplete()
        }
    }
}
