package com.martiandeveloper.enlingo.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.martiandeveloper.enlingo.R
import com.martiandeveloper.enlingo.databinding.FragmentHomeBinding
import com.martiandeveloper.enlingo.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var homeViewModel: HomeViewModel

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
        homeViewModel = getViewModel()
        binding.homeViewModel = homeViewModel
        binding.lifecycleOwner = this
        observe()
    }

    private fun getViewModel(): HomeViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel() as T
            }
        })[HomeViewModel::class.java]
    }

    private fun observe() {
        homeViewModel.eventWordsMCVClick.observe(viewLifecycleOwner, {
            if (it) {
                navigate(HomeFragmentDirections.actionHomeFragmentToWordFragment())
            }
        })

        homeViewModel.eventPhrasesMCVClick.observe(viewLifecycleOwner, {
            if (it) {
                navigate(HomeFragmentDirections.actionHomeFragmentToPhraseFragment())
            }
        })

        homeViewModel.eventTestMCVClick.observe(viewLifecycleOwner, {
            if (it) {
                Toast.makeText(
                    context,
                    R.string.the_test_section_is_under_development,
                    Toast.LENGTH_SHORT
                ).show()
                homeViewModel.onTestMCVClickComplete()
            }
        })

        homeViewModel.eventRateMCVClick.observe(viewLifecycleOwner, {
            if (it) {
                navigate(HomeFragmentDirections.actionHomeFragmentToSupportFragment())
            }
        })
    }

    private fun navigate(navDirections: NavDirections) {
        view?.let { Navigation.findNavController(it).navigate(navDirections) }

        when (navDirections) {
            HomeFragmentDirections.actionHomeFragmentToWordFragment() -> homeViewModel.onWordsMCVClickComplete()
            HomeFragmentDirections.actionHomeFragmentToPhraseFragment() -> homeViewModel.onPhrasesMCVClickComplete()
            HomeFragmentDirections.actionHomeFragmentToSupportFragment() -> homeViewModel.onRateMCVClickComplete()
        }
    }
}
