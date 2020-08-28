package com.martiandeveloper.easyenglish.database

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Handler
import android.widget.Toast
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.view.FeedActivity
import com.martiandeveloper.easyenglish.view.IntroActivity
import com.martiandeveloper.easyenglish.viewmodel.INTRO_KEY
import com.martiandeveloper.easyenglish.viewmodel.INTRO_SHARED_PREFERENCES

@SuppressLint("StaticFieldLeak")
class DatabaseAsync(private val context: Context, private val activity: Activity) :
    AsyncTask<Void?, Void?, Boolean?>() {

    override fun doInBackground(vararg params: Void?): Boolean? {
        val databaseHelper = DatabaseHelper(context)

        try {
            databaseHelper.createDataBase()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, R.string.went_wrong, Toast.LENGTH_SHORT).show()
        }

        databaseHelper.close()
        return null
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        getIntroData()
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
