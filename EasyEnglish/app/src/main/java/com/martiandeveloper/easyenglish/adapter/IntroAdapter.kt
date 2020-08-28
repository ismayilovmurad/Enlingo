package com.martiandeveloper.easyenglish.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.textview.MaterialTextView
import com.martiandeveloper.easyenglish.R

class IntroAdapter(
    private val context: Context,
    private val splash_images: List<Int>,
    private val splash_titles: List<String>,
    private val splash_descriptions: List<String>
) :
    PagerAdapter() {
    override fun getCount(): Int {
        return splash_titles.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val view: View = layoutInflater.inflate(R.layout.layout_intro, container, false)

        val layoutIntroMainIV = view.findViewById<ImageView>(R.id.layout_intro_mainIV)
        val layoutIntroTitleTV: MaterialTextView = view.findViewById(R.id.layout_intro_titleTV)
        val layoutIntroDescriptionTV: MaterialTextView =
            view.findViewById(R.id.layout_intro_descriptionTV)

        layoutIntroMainIV.setImageResource(splash_images[position])
        layoutIntroTitleTV.text = splash_titles[position]
        layoutIntroDescriptionTV.text = splash_descriptions[position]

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as FrameLayout)
    }
}
