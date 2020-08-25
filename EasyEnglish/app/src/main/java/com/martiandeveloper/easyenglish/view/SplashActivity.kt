package com.martiandeveloper.easyenglish.view

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.database.DatabaseHelper
import com.martiandeveloper.easyenglish.viewmodel.SplashViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var vm: SplashViewModel

    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        window.setBackgroundDrawableResource(R.drawable.background_2)
        setContentView(R.layout.activity_splash)
        activity = this
        databaseHelper = DatabaseHelper(this)
        vm = getViewModel()
        vm.checkDatabase()
    }

    private fun getViewModel(): SplashViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SplashViewModel(applicationContext, databaseHelper, activity) as T
            }
        })[SplashViewModel::class.java]
    }

    override fun onDestroy() {
        databaseHelper.close()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}
