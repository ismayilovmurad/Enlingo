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
import com.martiandeveloper.easyenglish.adapter.PhraseCardAdapter
import com.martiandeveloper.easyenglish.databinding.FragmentPhraseBinding
import com.martiandeveloper.easyenglish.viewmodel.PhraseViewModel
import kotlinx.android.synthetic.main.fragment_phrase.*
import java.util.*

const val PHRASE_SHARED_PREFERENCES = "PhraseIndex"
const val PHRASE_KEY = "phrase_index"

class PhraseFragment : Fragment(), View.OnClickListener {

    private lateinit var vm: PhraseViewModel

    private lateinit var binding: FragmentPhraseBinding

    private lateinit var adapter: PhraseCardAdapter

    private var index = 0

    private var currentPhrase = ""

    private lateinit var textToSpeech: TextToSpeech

    private var adIndex = 0

    private lateinit var interstitialAd: InterstitialAd
    private lateinit var adView: AdView

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = getViewModel()
    }

    private fun getViewModel(): PhraseViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PhraseViewModel(requireContext()) as T
            }
        })[PhraseViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_phrase, container, false)
        binding.phraseViewModel = vm
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
        binding.phraseMeaning = vm.phraseList.value?.get(0)?.meaning
        getCurrentPhrase()
        initTextToSpeech()
        setAds()
        isFinish()
    }

    private fun getIndex() {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(
            PHRASE_SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        index = sharedPreferences.getInt(PHRASE_KEY, 0)
    }

    private fun setTheCard() {
        vm.phraseList.value?.subList(0, index)?.clear()

        if (vm.phraseList.value != null) {
            adapter = PhraseCardAdapter(requireContext(), vm.phraseList.value!!)
            fragment_phrase_mainSFAV.adapter = adapter
        }
    }

    private fun setListeners() {
        fragment_phrase_mainSFAV.setFlingListener(object : onFlingListener {
            override fun removeFirstObjectInAdapter() {
                vm.phraseList.value?.removeAt(0)
                adapter.notifyDataSetChanged()
                binding.phraseMeaning = vm.phraseList.value?.get(0)?.meaning

                if (vm.phraseList.value?.get(0)?.phrase != null) {
                    currentPhrase = vm.phraseList.value?.get(0)?.phrase!!
                }

                index++

                adIndex++
                if (adIndex >= 20) {
                    if (interstitialAd.isLoaded) {
                        interstitialAd.show()
                        adIndex = 0
                    }
                }

                if (vm.phraseList.value != null) {
                    if (vm.phraseList.value!!.size < 3) {
                        showFinishDialog()
                    }
                }
            }

            override fun onLeftCardExit(dataObject: Any) {}
            override fun onRightCardExit(dataObject: Any) {}
            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {}
            override fun onScroll(v: Float) {}
        })
        fragment_phrase_soundIV.setOnClickListener(this)
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
            navigate(PhraseFragmentDirections.actionPhraseFragmentToHomeFragment())
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
            navigate(PhraseFragmentDirections.actionPhraseFragmentSelf())
        }
        dialog.dismiss()
    }

    private fun saveIndex() {
        val sharedPreferences = requireContext().getSharedPreferences(
            PHRASE_SHARED_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(PHRASE_KEY, index)
        editor.apply()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.fragment_phrase_soundIV -> speak()
            }
        }
    }

    private fun speak() {
        textToSpeech.speak(currentPhrase, TextToSpeech.QUEUE_FLUSH, null, "tts1")
    }

    private fun getCurrentPhrase() {
        if (vm.phraseList.value?.get(0)?.phrase != null) {
            currentPhrase = vm.phraseList.value?.get(0)?.phrase!!
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
        adView.adUnitId = resources.getString(R.string.phrase_fragment_banner)

        fragment_phrase_bannerAdPlaceholderFL.addView(adView)

        val bannerAdRequest = AdRequest.Builder().build()

        val adSize = getAdSize()
        if (adSize != null) {
            adView.adSize = adSize
        }
        adView.loadAd(bannerAdRequest)

        // Interstitial
        interstitialAd = InterstitialAd(context)
        interstitialAd.adUnitId = resources.getString(R.string.phrase_fragment_interstitial)

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
        if (vm.phraseList.value?.get(0)?.phrase == "End" || vm.phraseList.value?.get(0)?.phrase!!.startsWith(
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
