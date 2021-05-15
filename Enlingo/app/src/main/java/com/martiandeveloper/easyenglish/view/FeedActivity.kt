package com.martiandeveloper.easyenglish.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import androidx.appcompat.app.AppCompatActivity
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.utils.IN_APP_UPDATE_REQUEST_CODE
import com.martiandeveloper.easyenglish.utils.MAIN_ACTIVITY_BANNER
import kotlinx.android.synthetic.main.activity_feed.*
import timber.log.Timber

class FeedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        setContentView(R.layout.activity_feed)
    }

}
