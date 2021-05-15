package com.martiandeveloper.easyenglish.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.utils.INTRO_KEY
import com.martiandeveloper.easyenglish.utils.INTRO_PREFERENCES
import com.martiandeveloper.easyenglish.utils.SPLASH_DELAY

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        window.setBackgroundDrawableResource(R.drawable.background_2)
        setContentView(R.layout.activity_splash)
        getIntroData()
    }

    private fun getIntroData() {
        val sharedPreferences: SharedPreferences = getSharedPreferences(
            INTRO_PREFERENCES,
            Context.MODE_PRIVATE
        )
        check(
            sharedPreferences.getString(
                INTRO_KEY,
                "no"
            )!!
        )
    }

    private fun check(isIntro: String) {
        val intent = if (isIntro == "no") {
            Intent(this, IntroActivity::class.java)
        } else {
            Intent(this, FeedActivity::class.java)
        }
        waitAndGo(intent)
    }

    private fun waitAndGo(intent: Intent) {
        Handler().postDelayed({
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, SPLASH_DELAY)
    }
}
