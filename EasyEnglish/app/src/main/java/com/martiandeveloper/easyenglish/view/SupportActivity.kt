package com.martiandeveloper.easyenglish.view

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.martiandeveloper.easyenglish.BuildConfig
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.databinding.ActivitySupportBinding

class SupportActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySupportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        window.setBackgroundDrawableResource(R.drawable.background_3)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_support)

        binding.onClickListener = this

        val version = "${getString(R.string.version)} ${BuildConfig.VERSION_NAME}"
        binding.version = version
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.activity_support_aboutUsMCV -> Toast.makeText(
                    applicationContext,
                    getString(R.string.this_section_is_under_development),
                    Toast.LENGTH_SHORT
                ).show()
                R.id.activity_support_sendUsMessageMCV -> Toast.makeText(
                    applicationContext,
                    getString(R.string.this_section_is_under_development),
                    Toast.LENGTH_SHORT
                ).show()
                R.id.activity_support_inviteFriendMCV -> Toast.makeText(
                    applicationContext,
                    getString(R.string.this_section_is_under_development),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
