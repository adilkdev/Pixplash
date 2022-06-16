package com.adil.pixplash.data.local.prefs

import android.content.SharedPreferences
import javax.inject.Inject

class UserPreferences @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        const val ACTIVE_THEME = "PREF_THEME"
    }

}