package com.martiandeveloper.enlingo.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.textview.MaterialTextView
import com.martiandeveloper.enlingo.R
import com.martiandeveloper.enlingo.adapter.IntroAdapter
import com.martiandeveloper.enlingo.databinding.ActivityIntroBinding
import com.martiandeveloper.enlingo.model.Splash
import com.martiandeveloper.enlingo.utils.INTRO_KEY
import com.martiandeveloper.enlingo.utils.INTRO_PREFERENCES
import com.martiandeveloper.enlingo.viewmodel.IntroViewModel
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity(), OnPageChangeListener {

    private var introImages = ArrayList<Int>()
    private lateinit var introTitles: List<String>
    private lateinit var introDescriptions: List<String>

    private var introDots = ArrayList<MaterialTextView>()

    private var page = 0

    private var splashList = ArrayList<Splash>()

    private lateinit var binding: ActivityIntroBinding

    private lateinit var introViewModel: IntroViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        window.setBackgroundDrawableResource(R.drawable.background_3)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_intro)
        introViewModel = getViewModel()
        binding.introViewModel = introViewModel
        binding.lifecycleOwner = this
        fillTheLists()
        setAdapter()
        addDotIndicator(0)
        setListeners()
        introViewModel.setPreviousMBTN(true)
        introViewModel.setNextMBTNText(getString(R.string.next))
        observe()
    }

    private fun getViewModel(): IntroViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return IntroViewModel() as T
            }
        })[IntroViewModel::class.java]
    }

    private fun fillTheLists() {
        introImages.add(R.drawable.welcome)
        introImages.add(R.drawable.improve)
        introImages.add(R.drawable.diplom)
        introImages.add(R.drawable.test)

        introTitles = listOf(*resources.getStringArray(R.array.titles))
        introDescriptions = listOf(*resources.getStringArray(R.array.descriptions))

        for (i in 0 until introImages.size) {
            splashList.add(Splash(introImages[i], introTitles[i], introDescriptions[i]))
        }
    }

    private fun setAdapter() {
        val pagerAdapter: PagerAdapter =
            IntroAdapter(splashList)
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
                    val layoutSplashDescriptionMTV: MaterialTextView =
                        view.findViewById(R.id.layout_intro_descriptionMTV)
                    layoutSplashDescriptionMTV.translationX = -position * pageWidthDividedBy10
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
    }

    private fun observe() {
        introViewModel.eventNextMBTNClick.observe(this, {
            if (it) {
                next()
            }
        })

        introViewModel.eventPreviousMBTNClick.observe(this, {
            if (it) {
                previous()
            }
        })
    }

    private fun next() {
        if (page != 3) {
            activity_intro_mainVP.currentItem = page + 1
        } else {
            saveToSharedPreferences()
            go()
        }
        introViewModel.onNextMBTNClickComplete()
    }

    private fun saveToSharedPreferences() {
        val sharedPreferences = getSharedPreferences(INTRO_PREFERENCES, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(INTRO_KEY, "yes")
        editor.apply()
    }

    private fun go() {
        val intent = Intent(this, FeedActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun previous() {
        activity_intro_mainVP.currentItem = page - 1
        introViewModel.onPreviousMBTNClickComplete()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        addDotIndicator(position)
        page = position
        if (page == 0) {
            introViewModel.setPreviousMBTN(true)
        } else {
            introViewModel.setPreviousMBTN(false)
        }
        if (page == 3) {
            introViewModel.setNextMBTNText(getString(R.string.okay))
        } else {
            introViewModel.setNextMBTNText(getString(R.string.next))
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}
}
