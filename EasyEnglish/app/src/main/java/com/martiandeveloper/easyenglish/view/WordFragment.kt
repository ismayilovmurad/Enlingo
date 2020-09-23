package com.martiandeveloper.easyenglish.view

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.lorentzos.flingswipe.SwipeFlingAdapterView.onFlingListener
import com.martiandeveloper.easyenglish.R
import com.martiandeveloper.easyenglish.adapter.WordAdapter
import com.martiandeveloper.easyenglish.adapter.WordCardAdapter
import com.martiandeveloper.easyenglish.databinding.DialogFinishWordBinding
import com.martiandeveloper.easyenglish.databinding.DialogRestartWordBinding
import com.martiandeveloper.easyenglish.databinding.FragmentWordBinding
import com.martiandeveloper.easyenglish.databinding.LayoutListWordBinding
import com.martiandeveloper.easyenglish.model.Word
import com.martiandeveloper.easyenglish.utils.MAIN_ACTIVITY_INTERSTITIAL
import com.martiandeveloper.easyenglish.utils.PHRASE_KEY
import com.martiandeveloper.easyenglish.utils.PHRASE_SHARED_PREFERENCES
import java.util.*
import kotlin.collections.ArrayList

class WordFragment : Fragment(), View.OnClickListener, WordAdapter.ItemClickListener {

    var wordList = ArrayList<Word>()

    private lateinit var mainBinding: FragmentWordBinding

    private lateinit var cardAdapter: WordCardAdapter

    private var index = 0

    private var currentWord = ""

    private lateinit var textToSpeech: TextToSpeech

    private var adIndex = 0

    private lateinit var interstitialAd: InterstitialAd

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_word, container, false)
        return mainBinding.root
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
        mainBinding.wordMeaning = wordList[0].meaning
        getCurrentWord()
        initTextToSpeech()
        setAds()
        isFinish()
        initAnimations()
        mainBinding.isRestartMTVGone = true
        mainBinding.isListMTVGone = true
        mainBinding.isRestartFABGone = true
        mainBinding.isListFABGone = true
        mainBinding.onClickListener = this
    }

    private fun fillTheList() {
        val words = context?.resources?.getStringArray(R.array.words)
        val wordMeanings = context?.resources?.getStringArray(R.array.word_meanings)
        val wordTranscriptions = context?.resources?.getStringArray(R.array.word_transcriptions)

        val list = ArrayList<Word>()

        for (i in words!!.indices) {
            list.add(Word(words[i], wordMeanings!![i], wordTranscriptions!![i]))
        }

        wordList = list
    }

    private fun initAnimations() {
        fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)
        fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        fabClock = AnimationUtils.loadAnimation(context, R.anim.fab_rotate_clock)
        fabAntiClock = AnimationUtils.loadAnimation(context, R.anim.fab_rotate_anticlock)
    }

    private fun getIndex() {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(
            PHRASE_SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        index = sharedPreferences.getInt(PHRASE_KEY, 0)
    }

    private fun setTheCard() {
        wordList.subList(0, index).clear()

        cardAdapter = WordCardAdapter(requireContext(), wordList)
        mainBinding.fragmentWordMainSFAV.adapter = cardAdapter
    }

    private fun setListeners() {
        mainBinding.fragmentWordMainSFAV.setFlingListener(object : onFlingListener {
            override fun removeFirstObjectInAdapter() {
                wordList.removeAt(0)
                cardAdapter.notifyDataSetChanged()
                mainBinding.wordMeaning = wordList[0].meaning

                currentWord = wordList[0].word

                index++

                adIndex++
                if (adIndex >= 20) {
                    if (interstitialAd.isLoaded) {
                        interstitialAd.show()
                        adIndex = 0
                    }
                }

                if (wordList.size < 3) {
                    showFinishDialog()
                }
            }

            override fun onLeftCardExit(dataObject: Any) {}
            override fun onRightCardExit(dataObject: Any) {}
            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {}
            override fun onScroll(v: Float) {}
        })
    }

    private fun showFinishDialog() {
        finishDialog = AlertDialog.Builder(context).create()
        val binding = DialogFinishWordBinding.inflate(LayoutInflater.from(context))

        binding.onClickListener = this

        finishDialog.setView(binding.root)
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
                R.id.fragment_word_soundFL -> speak()
                R.id.dialog_finish_word_startAgainMBTN -> restart(
                    finishDialog,
                    WordFragmentDirections.actionWordFragmentSelf()
                )
                R.id.dialog_finish_word_homeMBTN -> restart(
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
                R.id.dialog_restart_word_yesMBTN -> restart(
                    restartDialog,
                    WordFragmentDirections.actionWordFragmentSelf()
                )
                R.id.dialog_restart_word_noMBTN -> restartDialog.dismiss()
            }
        }
    }

    private fun openRestartDialog(v: View?) {
        restartDialog = AlertDialog.Builder(v?.context).create()
        val binding = DialogRestartWordBinding.inflate(LayoutInflater.from(context))

        binding.onClickListener = this

        restartDialog.setView(binding.root)
        restartDialog.show()
    }

    private fun speak() {
        textToSpeech.speak(currentWord, TextToSpeech.QUEUE_FLUSH, null, "tts1")
    }

    private fun openWordListDialog(v: View?) {
        wordListDialog = AlertDialog.Builder(v?.context).create()
        val binding = LayoutListWordBinding.inflate(LayoutInflater.from(context))

        val layoutWordListMainRV = binding.layoutListWordMainRV
        val wordList = ArrayList(listOf(*resources.getStringArray(R.array.words)))

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
        wordAdapter =
            WordAdapter(wordsList, requireContext(), this, mainBinding.wordMeaning)
        layoutWordListMainRV.adapter = wordAdapter
        layoutWordListMainRV.scrollToPosition(index)

        val layoutWordListMainSV = binding.layoutListWordMainSV
        layoutWordListMainSV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                wordAdapter.filter.filter(newText)
                return false
            }

        })

        wordListDialog.setView(binding.root)
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
        mainBinding.isRestartMTVGone = true
        mainBinding.isListMTVGone = true
        mainBinding.fragmentWordRestartFAB.startAnimation(fabClose)
        mainBinding.fragmentWordListFAB.startAnimation(fabClose)
        mainBinding.fragmentWordMainFAB.startAnimation(fabAntiClock)
        isOpen = false
    }

    private fun openFAB() {
        mainBinding.isRestartMTVGone = false
        mainBinding.isListMTVGone = false
        mainBinding.fragmentWordRestartFAB.startAnimation(fabOpen)
        mainBinding.fragmentWordListFAB.startAnimation(fabOpen)
        mainBinding.fragmentWordMainFAB.startAnimation(fabClock)
        isOpen = true
    }

    override fun onItemClick(word: Word) {
        wordListDialog.dismiss()
        index = fullWordList.indexOf(word.word)
        saveIndex()
        navigate(WordFragmentDirections.actionWordFragmentSelf())
    }

    private fun getCurrentWord() {
        currentWord = wordList[0].word
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status: Int ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.US
            }
        }
    }

    private fun setAds() {
        // Interstitial
        interstitialAd = InterstitialAd(context)
        interstitialAd.adUnitId = MAIN_ACTIVITY_INTERSTITIAL

        val interstitialAdRequest = AdRequest.Builder().build()
        interstitialAd.loadAd(interstitialAdRequest)

        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                interstitialAd.loadAd(interstitialAdRequest)
            }
        }
    }

    private fun isFinish() {
        if (wordList[0].word == "End" || wordList[0].word.startsWith(
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
