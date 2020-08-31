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
import com.martiandeveloper.easyenglish.model.Splash

class IntroAdapter(
    private val context: Context,
    private val splashList: List<Splash>
) :
    PagerAdapter() {
    override fun getCount(): Int {
        return splashList.size
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

        layoutIntroMainIV.setImageResource(splashList[position].image)
        layoutIntroTitleTV.text = splashList[position].title
        layoutIntroDescriptionTV.text = splashList[position].description

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as FrameLayout)
    }
}
