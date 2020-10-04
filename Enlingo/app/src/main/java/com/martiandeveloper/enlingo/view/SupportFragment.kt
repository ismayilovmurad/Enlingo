package com.martiandeveloper.enlingo.view

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
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.martiandeveloper.enlingo.R
import com.martiandeveloper.enlingo.databinding.FragmentSupportBinding
import com.martiandeveloper.enlingo.utils.APP_URL
import com.martiandeveloper.enlingo.utils.COMPANY_EMAIL
import com.martiandeveloper.enlingo.viewmodel.SupportViewModel

class SupportFragment : Fragment() {

    private lateinit var supportBinding: FragmentSupportBinding

    private lateinit var supportViewModel: SupportViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        supportBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_support, container, false)
        return supportBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        supportViewModel = getViewModel()
        supportBinding.supportViewModel = supportViewModel
        supportBinding.lifecycleOwner = viewLifecycleOwner
        observe()
    }

    private fun getViewModel(): SupportViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SupportViewModel() as T
            }
        })[SupportViewModel::class.java]
    }

    private fun observe() {
        supportViewModel.eventAboutUsMCVClick.observe(viewLifecycleOwner, {
            if (it) {
                navigate(SupportFragmentDirections.actionSupportFragmentToAboutUsFragment())
            }
        })

        supportViewModel.eventSendUsMessageMCVClick.observe(viewLifecycleOwner, {
            if (it) {
                sendEmail()
            }
        })

        supportViewModel.eventInviteFriendMCVClick.observe(viewLifecycleOwner, {
            if (it) {
                invite()
            }
        })
    }

    private fun navigate(navDirections: NavDirections) {
        view?.let { Navigation.findNavController(it).navigate(navDirections) }
        supportViewModel.onAboutUsMCVClickComplete()
    }

    private fun sendEmail() {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", COMPANY_EMAIL, null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Easy English")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_via)))
        supportViewModel.onSendUsMessageMCVClickComplete()
    }

    private fun invite() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val shareBody =
            "${getString(R.string.hi_i_found_a_beautiful_app_for_improving_english)} $APP_URL"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)))
        supportViewModel.onInviteFriendMCVClickComplete()
    }
}
