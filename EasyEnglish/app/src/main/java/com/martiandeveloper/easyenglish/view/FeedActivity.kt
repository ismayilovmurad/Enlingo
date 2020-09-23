package com.martiandeveloper.easyenglish.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.utils.IN_APP_UPDATE_REQUEST_CODE
import com.martiandeveloper.easyenglish.utils.MAIN_ACTIVITY_BANNER
import kotlinx.android.synthetic.main.activity_feed.*
import timber.log.Timber

class FeedActivity : AppCompatActivity() {

    private var appUpdateManager: AppUpdateManager? = null

    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        setContentView(R.layout.activity_feed)
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        checkUpdate()
        setAds()
    }

    private fun checkUpdate() {
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)
            ) {
                appUpdateManager?.startUpdateFlowForResult(
                    appUpdateInfo,
                    IMMEDIATE,
                    this,
                    IN_APP_UPDATE_REQUEST_CODE
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability()
                == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                appUpdateManager!!.startUpdateFlowForResult(
                    appUpdateInfo,
                    IMMEDIATE,
                    this,
                    IN_APP_UPDATE_REQUEST_CODE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IN_APP_UPDATE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Timber.i("The result is okay")
                }
                Activity.RESULT_CANCELED -> {
                    Timber.i("The result is cancelled")
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    Timber.i("The result In-app update failed")
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setAds() {
        adView = AdView(applicationContext)
        adView.adUnitId = MAIN_ACTIVITY_BANNER

        activity_feed_bannerAdPlaceholderFL.addView(adView)

        val bannerAdRequest = AdRequest.Builder().build()

        val adSize = getAdSize()
        if (adSize != null) {
            adView.adSize = adSize
        }
        adView.loadAd(bannerAdRequest)
    }

    private fun getAdSize(): AdSize? {
        val display: Display? = windowManager.defaultDisplay
        return if (display != null) {
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val widthPixels = outMetrics.widthPixels.toFloat()
            val density = outMetrics.density

            val adWidth = (widthPixels / density).toInt()

            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(applicationContext, adWidth)
        } else {
            null
        }
    }
}
