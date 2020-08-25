package com.martiandeveloper.easyenglish.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.database.DatabaseHelper
import com.martiandeveloper.easyenglish.view.FeedActivity

@SuppressLint("StaticFieldLeak")
class SplashViewModel(private val context: Context, private val databaseHelper: DatabaseHelper) :
    ViewModel() {

    fun checkDatabase() {

        if (databaseHelper.checkDataBase()) {
            openDatabase()
        } else {
            val databaseAsync = DatabaseAsync(context)
            databaseAsync.execute()
        }
    }

    private fun openDatabase() {
        try {
            databaseHelper.openDataBase()
            waitAndGo()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, R.string.went_wrong, Toast.LENGTH_SHORT).show()
        }
    }

    private fun waitAndGo() {
        Handler().postDelayed({
            val intent = Intent(context, FeedActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }, 2000)
    }

    class DatabaseAsync(private val context: Context) : AsyncTask<Void?, Void?, Boolean?>() {

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
            Handler().postDelayed({
                val intent = Intent(context, FeedActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }, 2000)
        }
    }
}
