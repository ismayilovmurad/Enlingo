@file:Suppress("unused")

package com.martiandeveloper.enlingo.utils

import android.app.Application

import timber.log.Timber

const val MAIN_ACTIVITY_BANNER =
    "ca-app-pub-3036948542324685/6817981258" // ca-app-pub-3940256099942544/6300978111
const val MAIN_ACTIVITY_INTERSTITIAL =
    "ca-app-pub-3036948542324685/9252572905" // ca-app-pub-3940256099942544/1033173712

const val IN_APP_UPDATE_REQUEST_CODE = 100

const val INTRO_PREFERENCES = "intro_pref"
const val INTRO_KEY = "is_intro"

const val IN_APP_REVIEW_REQUEST_CODE = 200

const val PHRASE_SHARED_PREFERENCES = "PhraseIndex"
const val PHRASE_KEY = "phrase_index"

const val WORD_SHARED_PREFERENCES = "WordIndex"
const val WORD_KEY = "word_index"

const val FACEBOOK_URL = "https://www.facebook.com/imartiandeveloper"
const val INSTAGRAM_URL = "https://www.instagram.com/martiandeveloper/"
const val PRIVACY_POLICY_URL =
    "https://github.com/mdismayilov/Easy-English-Privacy-Policy/blob/master/easy_english_privacy_policy.docx"
const val TERMS_CONDITIONS_URL =
    "https://github.com/mdismayilov/Easy-English-Privacy-Policy/blob/master/easy_english_terms_conditions.docx"
const val FLATICON_URL = "https://www.flaticon.com/"

const val COMPANY_EMAIL = "app.martiandeveloper@gmail.com"

const val APP_URL = "https://play.google.com/store/apps/details?id=com.martiandeveloper.easyenglish"

const val SPLASH_DELAY = 2000L

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
