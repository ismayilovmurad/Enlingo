package com.martiandeveloper.easyenglish.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.DisplayMetrics
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.google.android.gms.ads.*
import com.google.android.material.button.MaterialButton
import com.lorentzos.flingswipe.SwipeFlingAdapterView.onFlingListener
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.adapter.WordCardAdapter
import com.martiandeveloper.easyenglish.databinding.FragmentWordBinding
import com.martiandeveloper.easyenglish.viewmodel.WordViewModel
import kotlinx.android.synthetic.main.fragment_word.*
import java.util.*

const val WORD_SHARED_PREFERENCES = "WordIndex"
const val WORD_KEY = "word_index"

class WordFragment : Fragment(), View.OnClickListener {

    private lateinit var vm: WordViewModel

    private lateinit var binding: FragmentWordBinding

    private lateinit var adapter: WordCardAdapter

    private var index = 0

    private var currentWord = ""

    private lateinit var textToSpeech: TextToSpeech

    private var adIndex = 0

    private lateinit var interstitialAd: InterstitialAd
    private lateinit var adView: AdView

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = getViewModel()
    }

    private fun getViewModel(): WordViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WordViewModel(requireContext()) as T
            }
        })[WordViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_word, container, false)
        binding.wordViewModel = vm
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        getIndex()
        vm.fillTheList()
        setTheCard()
        setListeners()
        binding.wordMeaning = vm.wordList.value?.get(0)?.meaning
        getCurrentWord()
        initTextToSpeech()
        setAds()
        isFinish()
    }

    private fun getIndex() {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(
            WORD_SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        index = sharedPreferences.getInt(WORD_KEY, 0)
    }

    private fun setTheCard() {
        vm.wordList.value?.subList(0, index)?.clear()

        if (vm.wordList.value != null) {
            adapter = WordCardAdapter(requireContext(), vm.wordList.value!!)
            fragment_word_mainSFAV.adapter = adapter
        }
    }

    private fun setListeners() {
        fragment_word_mainSFAV.setFlingListener(object : onFlingListener {
            override fun removeFirstObjectInAdapter() {
                vm.wordList.value?.removeAt(0)
                adapter.notifyDataSetChanged()
                binding.wordMeaning = vm.wordList.value?.get(0)?.meaning

                if (vm.wordList.value?.get(0)?.word != null) {
                    currentWord = vm.wordList.value?.get(0)?.word!!
                }

                index++

                adIndex++
                if (adIndex >= 20) {
                    if (interstitialAd.isLoaded) {
                        interstitialAd.show()
                        adIndex = 0
                    }
                }

                if (vm.wordList.value != null) {
                    if (vm.wordList.value!!.size < 3) {
                        showFinishDialog()
                    }
                }
            }

            override fun onLeftCardExit(dataObject: Any) {}
            override fun onRightCardExit(dataObject: Any) {}
            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {}
            override fun onScroll(v: Float) {}
        })
        fragment_word_soundIV.setOnClickListener(this)
    }

    private fun showFinishDialog() {
        dialog = AlertDialog.Builder(context).create()
        @SuppressLint("InflateParams") val view: View =
            layoutInflater.inflate(R.layout.dialog_finish, null)
        val dialogFinishStartAgainBTN: MaterialButton =
            view.findViewById(R.id.dialog_finish_startAgainMBTN)
        val dialogFinishHomeBTN: MaterialButton = view.findViewById(R.id.dialog_finish_homeMBTN)
        dialogFinishStartAgainBTN.setOnClickListener { restart() }
        dialogFinishHomeBTN.setOnClickListener {
            dialog.dismiss()
            index = 0
            saveIndex()
            navigate(WordFragmentDirections.actionWordFragmentToHomeFragment())
        }
        dialog.setView(view)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun navigate(navDirections: NavDirections) {
        view?.let { Navigation.findNavController(it).navigate(navDirections) }
    }

    private fun restart() {
        if (index != 0) {
            index = 0
            saveIndex()
            navigate(WordFragmentDirections.actionWordFragmentSelf())
        }
        dialog.dismiss()
    }

    private fun saveIndex() {
        val sharedPreferences = requireContext().getSharedPreferences(
            WORD_SHARED_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(WORD_KEY, index)
        editor.apply()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.fragment_word_soundIV -> speak()
            }
        }
    }

    private fun speak() {
        textToSpeech.speak(currentWord, TextToSpeech.QUEUE_FLUSH, null, "tts1")
    }

    private fun getCurrentWord() {
        if (vm.wordList.value?.get(0)?.word != null) {
            currentWord = vm.wordList.value?.get(0)?.word!!
        }
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status: Int ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.US
            }
        }
    }

    private fun setAds() {
        MobileAds.initialize(context)

        // Banner
        adView = AdView(context)
        adView.adUnitId = resources.getString(R.string.word_fragment_banner)

        fragment_word_bannerAdPlaceholderFL.addView(adView)

        val bannerAdRequest = AdRequest.Builder().build()

        val adSize = getAdSize()
        if (adSize != null) {
            adView.adSize = adSize
        }
        adView.loadAd(bannerAdRequest)

        // Interstitial
        interstitialAd = InterstitialAd(context)
        interstitialAd.adUnitId = resources.getString(R.string.word_fragment_interstitial)

        val interstitialAdRequest = AdRequest.Builder().build()
        interstitialAd.loadAd(interstitialAdRequest)

        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                interstitialAd.loadAd(interstitialAdRequest)
            }
        }
    }

    private fun getAdSize(): AdSize? {
        val display: Display? = activity?.windowManager?.defaultDisplay
        return if (display != null) {
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val widthPixels = outMetrics.widthPixels.toFloat()
            val density = outMetrics.density

            val adWidth = (widthPixels / density).toInt()

            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
        } else {
            null
        }
    }

    private fun isFinish() {
        if (vm.wordList.value?.get(0)?.word == "End" || vm.wordList.value?.get(0)?.word!!.startsWith(
                "Congratulations"
            )
        ) {
            showFinishDialog()
        }
    }

    override fun onPause() {
        textToSpeech.stop()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        saveIndex()
    }

    override fun onDestroy() {
        textToSpeech.shutdown()
        super.onDestroy()
    }
}
