package com.martiandeveloper.easyenglish.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.databinding.LayoutIntroBinding
import com.martiandeveloper.easyenglish.model.Splash

class IntroAdapter(
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
        val binding: LayoutIntroBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(container.context),
                R.layout.layout_intro,
                container,
                false
            )

        binding.layoutIntroMainIV.setImageResource(splashList[position].image)
        binding.title = splashList[position].title
        binding.description = splashList[position].description

        val view = binding.root

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as FrameLayout)
    }
}
