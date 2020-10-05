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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import com.martiandeveloper.easyenglish.utils.WORD_KEY
import com.martiandeveloper.easyenglish.utils.WORD_SHARED_PREFERENCES
import com.martiandeveloper.easyenglish.viewmodel.WordViewModel
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class WordFragment : Fragment(), WordAdapter.ItemClickListener {

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

    private var fabClose: Animation? = null
    private var fabOpen: Animation? = null
    private var fabClock: Animation? = null
    private var fabAntiClock: Animation? = null

    private lateinit var restartDialog: AlertDialog

    private lateinit var wordViewModel: WordViewModel

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
        wordViewModel = getViewModel()
        mainBinding.wordViewModel = wordViewModel
        mainBinding.lifecycleOwner = this
        getIndex()
        fillTheList()
        setTheCard()
        setListeners()
        wordViewModel.setWordMeaningMTVText(wordList[0].meaning)
        getCurrentWord()
        initTextToSpeech()
        setAds()
        isFinish()
        initAnimations()
        if (wordViewModel.fabOpen.value == null) {
            wordViewModel.setRestartMTV(true)
            wordViewModel.setListMTV(true)
            wordViewModel.setRestartFAB(true)
            wordViewModel.setListFAB(true)
        }
        observe()
    }

    private fun getViewModel(): WordViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WordViewModel() as T
            }
        })[WordViewModel::class.java]
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
            WORD_SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        index = sharedPreferences.getInt(WORD_KEY, 0)
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
                wordViewModel.setWordMeaningMTVText(wordList[0].meaning)

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

        binding.wordViewModel = wordViewModel
        binding.lifecycleOwner = this

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
            WORD_SHARED_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(WORD_KEY, index)
        editor.apply()
    }

    private fun observe() {
        wordViewModel.eventSoundFLClick.observe(viewLifecycleOwner, {
            if (it) {
                speak()
                wordViewModel.onSoundFLClickComplete()
            }
        })

        wordViewModel.eventStartAgainMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                restart(
                    finishDialog,
                    WordFragmentDirections.actionWordFragmentSelf()
                )
                wordViewModel.onStartAgainMBTNClickComplete()
            }
        })

        wordViewModel.eventHomeMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                restart(
                    finishDialog,
                    WordFragmentDirections.actionWordFragmentToHomeFragment()
                )
                wordViewModel.onHomeMBTNClickComplete()
            }
        })

        wordViewModel.eventMainFABClick.observe(viewLifecycleOwner, {
            if (it) {
                handleFabExpand()
                wordViewModel.onMainFABClickComplete()
            }
        })

        wordViewModel.eventRestartFABClick.observe(viewLifecycleOwner, {
            if (it) {
                closeFAB()
                openRestartDialog(view)
                wordViewModel.onRestartFABClickComplete()
            }
        })

        wordViewModel.eventListFABClick.observe(viewLifecycleOwner, {
            if (it) {
                closeFAB()
                openWordListDialog(view)
                wordViewModel.onListFABClickComplete()
            }
        })

        wordViewModel.eventYesMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                restart(
                    restartDialog,
                    WordFragmentDirections.actionWordFragmentSelf()
                )
                wordViewModel.onYesMBTNClickComplete()
            }
        })

        wordViewModel.eventNoMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                restartDialog.dismiss()
                wordViewModel.onNoMBTNClickComplete()
            }
        })
    }

    private fun openRestartDialog(v: View?) {
        restartDialog = AlertDialog.Builder(v?.context).create()
        val binding = DialogRestartWordBinding.inflate(LayoutInflater.from(context))

        binding.wordViewModel = wordViewModel
        binding.lifecycleOwner

        restartDialog.setView(binding.root)
        restartDialog.show()
    }

    private fun speak() {
        Timber.i(currentWord)
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
            WordAdapter(wordsList, requireContext(), this, wordViewModel.wordMeaning.value)
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
        if (wordViewModel.fabOpen.value == null) {
            Timber.i("Open")
            openFAB()
        } else {
            if (wordViewModel.fabOpen.value!!) {
                Timber.i("Close")
                closeFAB()
            } else {
                Timber.i("Open")
                openFAB()
            }
        }
    }

    private fun closeFAB() {
        wordViewModel.setRestartMTV(true)
        wordViewModel.setListMTV(true)
        mainBinding.fragmentWordRestartFAB.startAnimation(fabClose)
        mainBinding.fragmentWordListFAB.startAnimation(fabClose)
        mainBinding.fragmentWordMainFAB.startAnimation(fabAntiClock)
        wordViewModel.setRestartFAB(true)
        wordViewModel.setListFAB(true)
        wordViewModel.setFabOpen(false)
    }

    private fun openFAB() {
        wordViewModel.setRestartMTV(false)
        wordViewModel.setListMTV(false)
        mainBinding.fragmentWordRestartFAB.startAnimation(fabOpen)
        mainBinding.fragmentWordListFAB.startAnimation(fabOpen)
        mainBinding.fragmentWordMainFAB.startAnimation(fabClock)
        wordViewModel.setRestartFAB(false)
        wordViewModel.setListFAB(false)
        wordViewModel.setFabOpen(true)
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
