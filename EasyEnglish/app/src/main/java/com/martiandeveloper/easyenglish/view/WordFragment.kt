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
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.material.button.MaterialButton
import com.lorentzos.flingswipe.SwipeFlingAdapterView.onFlingListener
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.adapter.WordAdapter
import com.martiandeveloper.easyenglish.adapter.WordCardAdapter
import com.martiandeveloper.easyenglish.databinding.FragmentWordBinding
import com.martiandeveloper.easyenglish.model.Word
import com.martiandeveloper.easyenglish.viewmodel.WordViewModel
import kotlinx.android.synthetic.main.fragment_word.*
import java.util.*
import kotlin.collections.ArrayList

const val WORD_SHARED_PREFERENCES = "WordIndex"
const val WORD_KEY = "word_index"

class WordFragment : Fragment(), View.OnClickListener, WordAdapter.ItemClickListener {

    private lateinit var vm: WordViewModel

    private lateinit var binding: FragmentWordBinding

    private lateinit var cardAdapter: WordCardAdapter

    private var index = 0

    private var currentWord = ""

    private lateinit var textToSpeech: TextToSpeech

    private var adIndex = 0

    private lateinit var interstitialAd: InterstitialAd
    private lateinit var adView: AdView

    private lateinit var finishDialog: AlertDialog

    private lateinit var wordAdapter: WordAdapter

    private lateinit var wordListDialog: AlertDialog

    private lateinit var fullWordList: ArrayList<String>

    private var isOpen = false

    private var fabClose: Animation? = null
    private var fabOpen: Animation? = null
    private var fabClock: Animation? = null
    private var fabAntiClock: Animation? = null

    private lateinit var restartDialog: AlertDialog

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
        initAnimations()
    }

    private fun initAnimations() {
        fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)
        fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        fabClock = AnimationUtils.loadAnimation(context, R.anim.fab_rotate_clock)
        fabAntiClock = AnimationUtils.loadAnimation(context, R.anim.fab_rotate_anticlock)
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
            cardAdapter = WordCardAdapter(requireContext(), vm.wordList.value!!)
            fragment_word_mainSFAV.adapter = cardAdapter
        }
    }

    private fun setListeners() {
        fragment_word_mainSFAV.setFlingListener(object : onFlingListener {
            override fun removeFirstObjectInAdapter() {
                vm.wordList.value?.removeAt(0)
                cardAdapter.notifyDataSetChanged()
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
        fragment_word_soundFL.setOnClickListener(this)
        fragment_word_mainFAB.setOnClickListener(this)
        fragment_word_restartFAB.setOnClickListener(this)
        fragment_word_listFAB.setOnClickListener(this)
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
                R.id.fragment_word_soundFL -> speak()
                R.id.dialog_finish_startAgainMBTN -> restart(
                    finishDialog,
                    WordFragmentDirections.actionWordFragmentSelf()
                )
                R.id.dialog_finish_homeMBTN -> restart(
                    finishDialog,
                    WordFragmentDirections.actionWordFragmentToHomeFragment()
                )
                R.id.fragment_word_mainFAB -> handleFabExpand()
                R.id.fragment_word_restartFAB -> {
                    closeFAB()
                    openRestartDialog(v)
                }
                R.id.fragment_word_listFAB -> {
                    closeFAB()
                    openWordListDialog(v)
                }
                R.id.dialog_restart_yesMBTN -> restart(
                    restartDialog,
                    WordFragmentDirections.actionWordFragmentSelf()
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
        textToSpeech.speak(currentWord, TextToSpeech.QUEUE_FLUSH, null, "tts1")
    }

    private fun openWordListDialog(v: View?) {
        wordListDialog = AlertDialog.Builder(v?.context).create()
        val view = layoutInflater.inflate(R.layout.layout_word_list, null)

        val layoutWordListMainRV =
            view.findViewById<RecyclerView>(R.id.layout_word_list_mainRV)
        val wordList =
            ArrayList(listOf(*resources.getStringArray(R.array.words)))

        fullWordList = wordList

        val wordMeaningList: ArrayList<String> =
            ArrayList(listOf(*resources.getStringArray(R.array.word_meanings)))
        val wordTranscriptionList: ArrayList<String> =
            ArrayList(listOf(*resources.getStringArray(R.array.word_transcriptions)))

        val wordsList = ArrayList<Word>()

        for (i in wordList.indices) {
            wordsList.add(
                Word(
                    wordList[i],
                    wordMeaningList[i],
                    wordTranscriptionList[i]
                )
            )
        }

        layoutWordListMainRV.layoutManager = LinearLayoutManager(context)
        wordAdapter = WordAdapter(wordsList, requireContext(), this,binding.wordMeaning)
        layoutWordListMainRV.adapter = wordAdapter
        layoutWordListMainRV.scrollToPosition(index)

        val layoutWordListMainSV =
            view.findViewById<SearchView>(R.id.layout_word_list_mainSV)
        layoutWordListMainSV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                wordAdapter.filter.filter(newText)
                return false
            }

        })

        wordListDialog.setView(view)
        wordListDialog.show()
    }

    private fun handleFabExpand() {
        if (isOpen) {
            closeFAB()
        } else {
            openFAB()
        }
    }

    private fun closeFAB() {
        fragment_word_restartMTV.visibility = View.INVISIBLE
        fragment_word_listMTV.visibility = View.INVISIBLE
        fragment_word_restartFAB.startAnimation(fabClose)
        fragment_word_listFAB.startAnimation(fabClose)
        fragment_word_mainFAB.startAnimation(fabAntiClock)
        fragment_word_restartFAB.isClickable = false
        fragment_word_listFAB.isClickable = false
        isOpen = false
    }

    private fun openFAB() {
        fragment_word_restartMTV.visibility = View.VISIBLE
        fragment_word_listMTV.visibility = View.VISIBLE
        fragment_word_restartFAB.startAnimation(fabOpen)
        fragment_word_listFAB.startAnimation(fabOpen)
        fragment_word_mainFAB.startAnimation(fabClock)
        fragment_word_restartFAB.isClickable = true
        fragment_word_listFAB.isClickable = true
        isOpen = true
    }

    override fun onItemClick(word: Word) {
        wordListDialog.dismiss()
        index = fullWordList.indexOf(word.word)
        saveIndex()
        navigate(WordFragmentDirections.actionWordFragmentSelf())
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
