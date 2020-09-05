package com.martiandeveloper.easyenglish.view

import android.app.Activity
import android.os.Bundle
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
}
