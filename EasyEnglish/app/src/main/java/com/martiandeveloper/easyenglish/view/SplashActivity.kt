package com.martiandeveloper.easyenglish.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.martiandeveloper.easyenglish.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        window.setBackgroundDrawableResource(R.drawable.background_2)
        setContentView(R.layout.activity_splash)
    }
}
