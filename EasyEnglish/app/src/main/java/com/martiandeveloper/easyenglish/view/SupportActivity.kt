package com.martiandeveloper.easyenglish.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.martiandeveloper.easyenglish.R

class SupportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        window.setBackgroundDrawableResource(R.drawable.background_3)
        setContentView(R.layout.activity_support)
    }
}
