package com.martiandeveloper.easyenglish.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.databinding.FragmentHomeBinding
import com.martiandeveloper.easyenglish.utils.IN_APP_REVIEW_REQUEST_CODE

class HomeFragment : Fragment(), View.OnClickListener {

    private var reviewInfo: ReviewInfo? = null
    private lateinit var reviewManager: ReviewManager

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initReview()
        binding.onClickListener = this
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
            Intent(context, SupportActivity::class.java),
            IN_APP_REVIEW_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IN_APP_REVIEW_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            reviewInfo?.let {
                val flow = reviewManager.launchReviewFlow(requireActivity(), it)
                flow.addOnSuccessListener {}
                flow.addOnFailureListener {}
                flow.addOnCompleteListener {}
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initReview() {
        reviewManager = ReviewManagerFactory.create(requireContext())

        val requestFlow = reviewManager.requestReviewFlow()
        requestFlow.addOnCompleteListener { request ->
            reviewInfo = if (request.isSuccessful) {
                request.result
            } else {
                null
            }
        }
    }
}
