package com.martiandeveloper.easyenglish.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.textview.MaterialTextView
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.adapter.IntroAdapter
import com.martiandeveloper.easyenglish.viewmodel.KEY
import com.martiandeveloper.easyenglish.viewmodel.SHARED_PREFERENCES
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity(), OnPageChangeListener, View.OnClickListener {

    private var introImages = ArrayList<Int>()
    private lateinit var introTitles: List<String>
    private lateinit var introDescriptions: List<String>
    private var introDots = ArrayList<MaterialTextView>()

    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        window.setBackgroundDrawableResource(R.drawable.background_3)
        setContentView(R.layout.activity_intro)
        fillTheLists()
        setAdapter()
        addDotIndicator(0)
        setListeners()
        hidePreviousButton()
    }

    private fun fillTheLists() {
        introImages.add(R.drawable.rate_us)
        introImages.add(R.drawable.words)
        introImages.add(R.drawable.phrases)
        introImages.add(R.drawable.test)

        introTitles = listOf(*resources.getStringArray(R.array.titles))
        introDescriptions = listOf(*resources.getStringArray(R.array.descriptions))
    }

    private fun setAdapter() {
        val pagerAdapter: PagerAdapter =
            IntroAdapter(this, introImages, introTitles, introDescriptions)
        activity_intro_mainVP.adapter = pagerAdapter
        activity_intro_mainVP.setPageTransformer(
            true
        ) { view: View, position: Float ->
            val pageWidth = view.width
            when {
                position < -1 -> {
                    view.alpha = 1f
                }
                position <= 1 -> {
                    val pageWidthDividedBy10 = pageWidth / 10
                    val pageWidthDividedBy3 = pageWidth / 3
                    val layoutSplashDescriptionTV: MaterialTextView =
                        view.findViewById(R.id.layout_intro_descriptionTV)
                    layoutSplashDescriptionTV.translationX = -position * pageWidthDividedBy10
                    val layoutSplashMainLL =
                        view.findViewById<LinearLayout>(R.id.layout_intro_mainLL)
                    layoutSplashMainLL.translationX = -position * pageWidthDividedBy3
                }
                else -> {
                    view.alpha = 1f
                }
            }
        }
    }

    private fun addDotIndicator(position: Int) {
        activity_intro_dotLL.removeAllViews()
        for (i in introImages.indices) {
            introDots.add(MaterialTextView(this))
            introDots[i].text = HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY)
            introDots[i].setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            introDots[i].textSize = 32f
            activity_intro_dotLL.addView(introDots[i])
        }
        if (introDots.size > 0) {
            introDots[position].setTextColor(ContextCompat.getColor(this, R.color.colorTwo))
        }
    }

    private fun setListeners() {
        activity_intro_mainVP.addOnPageChangeListener(this)
        activity_intro_nextBTN.setOnClickListener(this)
        activity_intro_previousBTN.setOnClickListener(this)
    }

    private fun hidePreviousButton() {
        activity_intro_previousBTN.isClickable = false
        activity_intro_previousBTN.visibility = View.INVISIBLE
    }

    private fun showPreviousButton() {
        activity_intro_previousBTN.isClickable = true
        activity_intro_previousBTN.visibility = View.VISIBLE
    }

    private fun saveToSharedPreferences() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY, "yes")
        editor.apply()
    }

    private fun go() {
        val intent = Intent(this, FeedActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        addDotIndicator(position)
        page = position
        if (page == 0) {
            hidePreviousButton()
        } else {
            showPreviousButton()
        }
        if (page == 3) {
            activity_intro_nextBTN.setText(R.string.okay)
        } else {
            activity_intro_nextBTN.setText(R.string.next)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.activity_intro_nextBTN -> next()
                R.id.activity_intro_previousBTN -> previous()
            }
        }
    }

    private fun next() {
        if (page != 3) {
            activity_intro_mainVP.currentItem = page + 1
        } else {
            saveToSharedPreferences()
            go()
        }
    }

    private fun previous() {
        activity_intro_mainVP.currentItem = page - 1
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
