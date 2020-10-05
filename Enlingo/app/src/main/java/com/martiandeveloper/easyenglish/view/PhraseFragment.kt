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
import com.martiandeveloper.easyenglish.adapter.PhraseAdapter
import com.martiandeveloper.easyenglish.adapter.PhraseCardAdapter
import com.martiandeveloper.easyenglish.databinding.DialogFinishPhraseBinding
import com.martiandeveloper.easyenglish.databinding.DialogRestartPhraseBinding
import com.martiandeveloper.easyenglish.databinding.FragmentPhraseBinding
import com.martiandeveloper.easyenglish.databinding.LayoutListPhraseBinding
import com.martiandeveloper.easyenglish.model.Phrase
import com.martiandeveloper.easyenglish.utils.MAIN_ACTIVITY_INTERSTITIAL
import com.martiandeveloper.easyenglish.utils.PHRASE_KEY
import com.martiandeveloper.easyenglish.utils.PHRASE_SHARED_PREFERENCES
import com.martiandeveloper.easyenglish.viewmodel.PhraseViewModel
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class PhraseFragment : Fragment(), PhraseAdapter.ItemClickListener {

    var phraseList = ArrayList<Phrase>()

    private lateinit var mainBinding: FragmentPhraseBinding

    private lateinit var cardAdapter: PhraseCardAdapter

    private var index = 0

    private var currentPhrase = ""

    private lateinit var textToSpeech: TextToSpeech

    private var adIndex = 0

    private lateinit var interstitialAd: InterstitialAd

    private lateinit var finishDialog: AlertDialog

    private lateinit var phraseAdapter: PhraseAdapter

    private lateinit var phraseListDialog: AlertDialog

    private lateinit var fullPhraseList: ArrayList<String>

    private var fabClose: Animation? = null
    private var fabOpen: Animation? = null
    private var fabClock: Animation? = null
    private var fabAntiClock: Animation? = null

    private lateinit var restartDialog: AlertDialog

    private lateinit var phraseViewModel: PhraseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_phrase, container, false)
        return mainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        phraseViewModel = getViewModel()
        mainBinding.phraseViewModel = phraseViewModel
        mainBinding.lifecycleOwner = this
        getIndex()
        fillTheList()
        setTheCard()
        setListeners()
        phraseViewModel.setPhraseMeaningMTVText(phraseList[0].meaning)
        getCurrentPhrase()
        initTextToSpeech()
        setAds()
        isFinish()
        initAnimations()
        if (phraseViewModel.fabOpen.value == null) {
            phraseViewModel.setRestartMTV(true)
            phraseViewModel.setListMTV(true)
            phraseViewModel.setRestartFAB(true)
            phraseViewModel.setListFAB(true)
        }
        observe()
    }

    private fun getViewModel(): PhraseViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PhraseViewModel() as T
            }
        })[PhraseViewModel::class.java]
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
            PHRASE_SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        index = sharedPreferences.getInt(PHRASE_KEY, 0)
    }

    private fun setTheCard() {
        phraseList.subList(0, index).clear()

        cardAdapter = PhraseCardAdapter(requireContext(), phraseList)
        mainBinding.fragmentPhraseMainSFAV.adapter = cardAdapter
    }

    private fun setListeners() {
        mainBinding.fragmentPhraseMainSFAV.setFlingListener(object : onFlingListener {
            override fun removeFirstObjectInAdapter() {
                phraseList.removeAt(0)
                cardAdapter.notifyDataSetChanged()
                phraseViewModel.setPhraseMeaningMTVText(phraseList[0].meaning)

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
    }

    private fun showFinishDialog() {
        finishDialog = AlertDialog.Builder(context).create()
        val binding = DialogFinishPhraseBinding.inflate(LayoutInflater.from(context))

        binding.phraseViewModel = phraseViewModel
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
            PHRASE_SHARED_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(PHRASE_KEY, index)
        editor.apply()
    }

    private fun observe() {
        phraseViewModel.eventSoundFLClick.observe(viewLifecycleOwner, {
            if (it) {
                speak()
                phraseViewModel.onSoundFLClickComplete()
            }
        })

        phraseViewModel.eventStartAgainMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                restart(
                    finishDialog,
                    PhraseFragmentDirections.actionPhraseFragmentSelf()
                )
                phraseViewModel.onStartAgainMBTNClickComplete()
            }
        })

        phraseViewModel.eventHomeMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                restart(
                    finishDialog,
                    PhraseFragmentDirections.actionPhraseFragmentToHomeFragment()
                )
                phraseViewModel.onHomeMBTNClickComplete()
            }
        })

        phraseViewModel.eventMainFABClick.observe(viewLifecycleOwner, {
            if (it) {
                handleFabExpand()
                phraseViewModel.onMainFABClickComplete()
            }
        })

        phraseViewModel.eventRestartFABClick.observe(viewLifecycleOwner, {
            if (it) {
                closeFAB()
                openRestartDialog(view)
                phraseViewModel.onRestartFABClickComplete()
            }
        })

        phraseViewModel.eventListFABClick.observe(viewLifecycleOwner, {
            if (it) {
                closeFAB()
                openPhraseListDialog(view)
                phraseViewModel.onListFABClickComplete()
            }
        })

        phraseViewModel.eventYesMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                restart(
                    restartDialog,
                    PhraseFragmentDirections.actionPhraseFragmentSelf()
                )
                phraseViewModel.onYesMBTNClickComplete()
            }
        })

        phraseViewModel.eventNoMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                restartDialog.dismiss()
                phraseViewModel.onNoMBTNClickComplete()
            }
        })
    }

    private fun openRestartDialog(v: View?) {
        restartDialog = AlertDialog.Builder(v?.context).create()
        val binding = DialogRestartPhraseBinding.inflate(LayoutInflater.from(context))

        binding.phraseViewModel = phraseViewModel
        binding.lifecycleOwner

        restartDialog.setView(binding.root)
        restartDialog.show()
    }

    private fun speak() {
        Timber.i(currentPhrase)
        textToSpeech.speak(currentPhrase, TextToSpeech.QUEUE_FLUSH, null, "tts1")
    }

    private fun openPhraseListDialog(v: View?) {
        phraseListDialog = AlertDialog.Builder(v?.context).create()
        val binding = LayoutListPhraseBinding.inflate(LayoutInflater.from(context))

        val layoutPhraseListMainRV = binding.layoutListPhraseMainRV
        val phraseList = ArrayList(listOf(*resources.getStringArray(R.array.phrases)))

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
        phraseAdapter =
            PhraseAdapter(phrasesList, requireContext(), this, phraseViewModel.phraseMeaning.value)
        layoutPhraseListMainRV.adapter = phraseAdapter
        layoutPhraseListMainRV.scrollToPosition(index)

        val layoutPhraseListMainSV = binding.layoutListPhraseMainSV
        layoutPhraseListMainSV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                phraseAdapter.filter.filter(newText)
                return false
            }

        })

        phraseListDialog.setView(binding.root)
        phraseListDialog.show()
    }

    private fun handleFabExpand() {
        if (phraseViewModel.fabOpen.value == null) {
            Timber.i("Open")
            openFAB()
        } else {
            if (phraseViewModel.fabOpen.value!!) {
                Timber.i("Close")
                closeFAB()
            } else {
                Timber.i("Open")
                openFAB()
            }
        }
    }

    private fun closeFAB() {
        phraseViewModel.setRestartMTV(true)
        phraseViewModel.setListMTV(true)
        mainBinding.fragmentPhraseRestartFAB.startAnimation(fabClose)
        mainBinding.fragmentPhraseListFAB.startAnimation(fabClose)
        mainBinding.fragmentPhraseMainFAB.startAnimation(fabAntiClock)
        phraseViewModel.setRestartFAB(true)
        phraseViewModel.setListFAB(true)
        phraseViewModel.setFabOpen(false)
    }

    private fun openFAB() {
        phraseViewModel.setRestartMTV(false)
        phraseViewModel.setListMTV(false)
        mainBinding.fragmentPhraseRestartFAB.startAnimation(fabOpen)
        mainBinding.fragmentPhraseListFAB.startAnimation(fabOpen)
        mainBinding.fragmentPhraseMainFAB.startAnimation(fabClock)
        phraseViewModel.setRestartFAB(false)
        phraseViewModel.setListFAB(false)
        phraseViewModel.setFabOpen(true)
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
