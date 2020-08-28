package com.martiandeveloper.easyenglish.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.database.DatabaseAsync
import com.martiandeveloper.easyenglish.database.DatabaseHelper
import com.martiandeveloper.easyenglish.view.FeedActivity
import com.martiandeveloper.easyenglish.view.IntroActivity

const val INTRO_SHARED_PREFERENCES = "Intro"
const val INTRO_KEY = "intro"

@SuppressLint("StaticFieldLeak")
class SplashViewModel(
    private val context: Context,
    private val databaseHelper: DatabaseHelper,
    private val activity: Activity
) :
    ViewModel() {

    fun checkDatabase() {

        if (databaseHelper.checkDataBase()) {
            openDatabase()
        } else {
            val databaseAsync = DatabaseAsync(context, activity)
            databaseAsync.execute()
        }
    }

    private fun openDatabase() {
        try {
            databaseHelper.openDataBase()
            getIntroData()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, R.string.went_wrong, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getIntroData() {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            INTRO_SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        if (sharedPreferences.getString(INTRO_KEY, "no") != null) {
            check(
                sharedPreferences.getString(
                    INTRO_KEY,
                    "no"
                )!!
            )
        }
    }

    private fun check(isIntro: String) {
        if (isIntro == "no") {
            val intent = Intent(context, IntroActivity::class.java)
            waitAndGo(intent)
        } else {
            val intent = Intent(context, FeedActivity::class.java)
            waitAndGo(intent)
        }
    }

    private fun waitAndGo(intent: Intent) {
        Handler().postDelayed({
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2000)
    }
}
