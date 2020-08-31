@file:Suppress("PrivatePropertyName")

package com.martiandeveloper.easyenglish.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.martiandeveloper.easyenglish.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), View.OnClickListener {

    private val ACTIVITY_CALLBACK: Int = 1
    private var reviewInfo: ReviewInfo? = null
    private lateinit var reviewManager: ReviewManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        setListeners()
        initReview()
    }

    private fun setListeners() {
        fragment_home_wordsMCV.setOnClickListener(this)
        fragment_home_phrasesMCV.setOnClickListener(this)
        fragment_home_testMCV.setOnClickListener(this)
        fragment_home_rateMCV.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.fragment_home_wordsMCV -> navigate(HomeFragmentDirections.actionHomeFragmentToWordFragment())
                R.id.fragment_home_phrasesMCV -> navigate(HomeFragmentDirections.actionHomeFragmentToPhraseFragment())
                R.id.fragment_home_testMCV -> Toast.makeText(
                    context,
                    R.string.the_test_section_is_under_development,
                    Toast.LENGTH_SHORT
                ).show()
                R.id.fragment_home_rateMCV -> support()
            }
        }
    }

    private fun navigate(navDirections: NavDirections) {
        view?.let { Navigation.findNavController(it).navigate(navDirections) }
    }

    private fun support() {
        startActivityForResult(
            Intent(context, SupportUsActivity::class.java),
            ACTIVITY_CALLBACK
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ACTIVITY_CALLBACK && resultCode == Activity.RESULT_OK) {
            Handler().postDelayed({
                reviewInfo?.let {
                    val flow = reviewManager.launchReviewFlow(requireActivity(), it)
                    flow.addOnSuccessListener {}
                    flow.addOnFailureListener {}
                    flow.addOnCompleteListener {}
                }
            }, 3000)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initReview() {
        //Create the ReviewManager instance
        reviewManager = ReviewManagerFactory.create(requireContext())

        //Request a ReviewInfo object ahead of time (Pre-cache)
        val requestFlow = reviewManager.requestReviewFlow()
        requestFlow.addOnCompleteListener { request ->
            reviewInfo = if (request.isSuccessful) {
                //Received ReviewInfo object
                request.result
            } else {
                //Problem in receiving object
                null
            }
        }
    }
}
