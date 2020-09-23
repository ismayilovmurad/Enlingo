@file:Suppress("unused")

package com.martiandeveloper.easyenglish.utils

import android.app.Application

import timber.log.Timber

const val MAIN_ACTIVITY_BANNER =
    "ca-app-pub-3036948542324685/6817981258" // ca-app-pub-3940256099942544/6300978111
const val MAIN_ACTIVITY_INTERSTITIAL =
    "ca-app-pub-3036948542324685/9252572905" // ca-app-pub-3940256099942544/1033173712

const val IN_APP_UPDATE_REQUEST_CODE = 100

const val INTRO_SHARED_PREFERENCES = "Intro"
const val INTRO_KEY = "intro"

const val IN_APP_REVIEW_REQUEST_CODE = 200

const val PHRASE_SHARED_PREFERENCES = "PhraseIndex"
const val PHRASE_KEY = "phrase_index"

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
