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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.material.button.MaterialButton
import com.lorentzos.flingswipe.SwipeFlingAdapterView.onFlingListener
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.adapter.PhraseAdapter
import com.martiandeveloper.easyenglish.adapter.PhraseCardAdapter
import com.martiandeveloper.easyenglish.databinding.FragmentPhraseBinding
import com.martiandeveloper.easyenglish.model.Phrase
import kotlinx.android.synthetic.main.fragment_phrase.*
import java.util.*
import kotlin.collections.ArrayList

class PhraseFragment : Fragment(), View.OnClickListener, PhraseAdapter.ItemClickListener {

    private val phraseSharedPreferences = "PhraseIndex"
    private val phraseKey = "phrase_index"

    var phraseList = ArrayList<Phrase>()

    private lateinit var binding: FragmentPhraseBinding

    private lateinit var cardAdapter: PhraseCardAdapter

    private var index = 0

    private var currentPhrase = ""

    private lateinit var textToSpeech: TextToSpeech

    private var adIndex = 0

    private lateinit var interstitialAd: InterstitialAd
    private lateinit var adView: AdView

    private lateinit var finishDialog: AlertDialog

    private lateinit var phraseAdapter: PhraseAdapter

    private lateinit var phraseListDialog: AlertDialog

    private lateinit var fullPhraseList: ArrayList<String>

    private var isOpen = false

    private var fabClose: Animation? = null
    private var fabOpen: Animation? = null
    private var fabClock: Animation? = null
    private var fabAntiClock: Animation? = null

    private lateinit var restartDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_phrase, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        getIndex()
        fillTheList()
        setTheCard()
        setListeners()
        binding.phraseMeaning = phraseList[0].meaning
        getCurrentPhrase()
        initTextToSpeech()
        setAds()
        isFinish()
        initAnimations()
    }

    private fun fillTheList() {
        val phrases = context?.resources?.getStringArray(R.array.phrases)
        val phraseMeanings = context?.resources?.getStringArray(R.array.phrase_meanings)
        val phraseTranscriptions = context?.resources?.getStringArray(R.array.phrase_transcriptions)

        val list = ArrayList<Phrase>()

        for (i in phrases!!.indices) {
            list.add(Phrase(phrases[i], phraseMeanings!![i], phraseTranscriptions!![i]))
        }

        phraseList = list
    }

    private fun initAnimations() {
        fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)
        fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        fabClock = AnimationUtils.loadAnimation(context, R.anim.fab_rotate_clock)
        fabAntiClock = AnimationUtils.loadAnimation(context, R.anim.fab_rotate_anticlock)
    }

    private fun getIndex() {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(
            phraseSharedPreferences,
            Context.MODE_PRIVATE
        )
        index = sharedPreferences.getInt(phraseKey, 0)
    }

    private fun setTheCard() {
        phraseList.subList(0, index).clear()

        cardAdapter = PhraseCardAdapter(requireContext(), phraseList)
        fragment_phrase_mainSFAV.adapter = cardAdapter
    }

    private fun setListeners() {
        fragment_phrase_mainSFAV.setFlingListener(object : onFlingListener {
            override fun removeFirstObjectInAdapter() {
                phraseList.removeAt(0)
                cardAdapter.notifyDataSetChanged()
                binding.phraseMeaning = phraseList[0].meaning

                currentPhrase = phraseList[0].phrase

                index++

                adIndex++
                if (adIndex >= 20) {
                    if (interstitialAd.isLoaded) {
                        interstitialAd.show()
                        adIndex = 0
                    }
                }

                if (phraseList.size < 3) {
                    showFinishDialog()
                }
            }

            override fun onLeftCardExit(dataObject: Any) {}
            override fun onRightCardExit(dataObject: Any) {}
            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {}
            override fun onScroll(v: Float) {}
        })

        fragment_phrase_soundFL.setOnClickListener(this)
        fragment_phrase_mainFAB.setOnClickListener(this)
        fragment_phrase_restartFAB.setOnClickListener(this)
        fragment_phrase_listFAB.setOnClickListener(this)
    }

    private fun showFinishDialog() {
        finishDialog = AlertDialog.Builder(context).create()
        @SuppressLint("InflateParams") val view: View =
            layoutInflater.inflate(R.layout.dialog_finish, null)
        val dialogFinishStartAgainBTN: MaterialButton =
            view.findViewById(R.id.dialog_finish_startAgainMBTN)
        val dialogFinishHomeBTN: MaterialButton = view.findViewById(R.id.dialog_finish_homeMBTN)
        dialogFinishStartAgainBTN.setOnClickListener(this)
        dialogFinishHomeBTN.setOnClickListener(this)
        finishDialog.setView(view)
        finishDialog.setCanceledOnTouchOutside(false)
        finishDialog.setCancelable(false)
        finishDialog.show()
    }

    private fun navigate(navDirections: NavDirections) {
        view?.let { Navigation.findNavController(it).navigate(navDirections) }
    }

    private fun restart(alertDialog: AlertDialog?, navDirections: NavDirections) {
        alertDialog?.dismiss()
        index = 0
        saveIndex()
        navigate(navDirections)
    }

    private fun saveIndex() {
        val sharedPreferences = requireContext().getSharedPreferences(
            phraseSharedPreferences,
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(phraseKey, index)
        editor.apply()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.fragment_phrase_soundFL -> speak()
                R.id.dialog_finish_startAgainMBTN -> restart(
                    finishDialog,
                    PhraseFragmentDirections.actionPhraseFragmentSelf()
                )
                R.id.dialog_finish_homeMBTN -> restart(
                    finishDialog,
                    PhraseFragmentDirections.actionPhraseFragmentToHomeFragment()
                )
                R.id.fragment_phrase_mainFAB -> handleFabExpand()
                R.id.fragment_phrase_restartFAB -> {
                    closeFAB()
                    openRestartDialog(v)
                }
                R.id.fragment_phrase_listFAB -> {
                    closeFAB()
                    openPhraseListDialog(v)
                }
                R.id.dialog_restart_yesMBTN -> restart(
                    restartDialog,
                    PhraseFragmentDirections.actionPhraseFragmentSelf()
                )
                R.id.dialog_restart_noMBTN -> restartDialog.dismiss()
            }
        }
    }

    private fun openRestartDialog(v: View?) {
        restartDialog = AlertDialog.Builder(v?.context).create()
        val view = layoutInflater.inflate(R.layout.dialog_restart, null)

        val dialogRestartYesMBTN =
            view.findViewById<MaterialButton>(R.id.dialog_restart_yesMBTN)
        val dialogRestartNoMBTN =
            view.findViewById<MaterialButton>(R.id.dialog_restart_noMBTN)

        dialogRestartYesMBTN.setOnClickListener(this)
        dialogRestartNoMBTN.setOnClickListener(this)

        restartDialog.setView(view)
        restartDialog.show()
    }

    private fun speak() {
        textToSpeech.speak(currentPhrase, TextToSpeech.QUEUE_FLUSH, null, "tts1")
    }

    private fun openPhraseListDialog(v: View?) {
        phraseListDialog = AlertDialog.Builder(v?.context).create()
        val view = layoutInflater.inflate(R.layout.layout_phrase_list, null)

        val layoutPhraseListMainRV =
            view.findViewById<RecyclerView>(R.id.layout_phrase_list_mainRV)
        val phraseList =
            ArrayList(listOf(*resources.getStringArray(R.array.phrases)))

        fullPhraseList = phraseList

        val phraseMeaningList: ArrayList<String> =
            ArrayList(listOf(*resources.getStringArray(R.array.phrase_meanings)))
        val phraseTranscriptionList: ArrayList<String> =
            ArrayList(listOf(*resources.getStringArray(R.array.phrase_transcriptions)))

        val phrasesList = ArrayList<Phrase>()

        for (i in phraseList.indices) {
            phrasesList.add(
                Phrase(
                    phraseList[i],
                    phraseMeaningList[i],
                    phraseTranscriptionList[i]
                )
            )
        }

        layoutPhraseListMainRV.layoutManager = LinearLayoutManager(context)
        phraseAdapter = PhraseAdapter(phrasesList, requireContext(), this, binding.phraseMeaning)
        layoutPhraseListMainRV.adapter = phraseAdapter
        layoutPhraseListMainRV.scrollToPosition(index)

        val layoutPhraseListMainSV =
            view.findViewById<SearchView>(R.id.layout_phrase_list_mainSV)
        layoutPhraseListMainSV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                phraseAdapter.filter.filter(newText)
                return false
            }

        })

        phraseListDialog.setView(view)
        phraseListDialog.show()
    }

    private fun handleFabExpand() {
        if (isOpen) {
            closeFAB()
        } else {
            openFAB()
        }
    }

    private fun closeFAB() {
        fragment_phrase_restartMTV.visibility = View.INVISIBLE
        fragment_phrase_listMTV.visibility = View.INVISIBLE
        fragment_phrase_restartFAB.startAnimation(fabClose)
        fragment_phrase_listFAB.startAnimation(fabClose)
        fragment_phrase_mainFAB.startAnimation(fabAntiClock)
        fragment_phrase_restartFAB.isClickable = false
        fragment_phrase_listFAB.isClickable = false
        isOpen = false
    }

    private fun openFAB() {
        fragment_phrase_restartMTV.visibility = View.VISIBLE
        fragment_phrase_listMTV.visibility = View.VISIBLE
        fragment_phrase_restartFAB.startAnimation(fabOpen)
        fragment_phrase_listFAB.startAnimation(fabOpen)
        fragment_phrase_mainFAB.startAnimation(fabClock)
        fragment_phrase_restartFAB.isClickable = true
        fragment_phrase_listFAB.isClickable = true
        isOpen = true
    }

    override fun onItemClick(phrase: Phrase) {
        phraseListDialog.dismiss()
        index = fullPhraseList.indexOf(phrase.phrase)
        saveIndex()
        navigate(PhraseFragmentDirections.actionPhraseFragmentSelf())
    }

    private fun getCurrentPhrase() {
        currentPhrase = phraseList[0].phrase
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
        if (phraseList[0].phrase == "End" || phraseList[0].phrase.startsWith(
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
