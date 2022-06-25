package com.unwiringapps.earningnapp.DB

import android.content.Context
import android.content.SharedPreferences
import com.unwiringapps.earningnapp.Extra.LimitOfEarnings
import com.unwiringapps.earningnapp.Extra.showToast

class PrefManager(context: Context) {

    var PRIVATE_MODE = 0
    val PREF_NAME = "profile"
    var _context: Context = context
    var pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    var editor: SharedPreferences.Editor = pref.edit()


    fun setString(PREF_NAME: String?, VAL: String?) {
        editor.putString(PREF_NAME, VAL)
        editor.commit()
    }


    fun setBool(PREF_NAME: String?, VAL: Boolean) {
        editor.putBoolean(PREF_NAME, VAL)
        editor.commit()
    }

    fun setInt(PREF_NAME: String?, VAL: Int) {
        editor.putInt(PREF_NAME, VAL)
        editor.commit()
    }

    fun addCount(PREF_NAME: String?, val1: Int, val2: Int) {
        editor.putInt(PREF_NAME, val1 + val2)
        editor.commit()
    }

    fun setCoins(PREF_NAME: String?, VAL: Int, VAL2: Int, minus: Boolean) {
        if (minus) {
            editor.putInt(PREF_NAME, VAL - VAL2)
        } else {
            editor.putInt(PREF_NAME, VAL + VAL2)
        }
        editor.commit()
    }

    fun getString(PREF_NAME: String?): String? {
        return if (pref.contains(PREF_NAME)) {
            pref.getString(PREF_NAME, null)
        } else "no"
    }

    fun getBool(PREF_NAME: String?): Boolean {
        return pref.getBoolean(PREF_NAME, false)
    }

    fun getInt(key: String?): Int {
        return pref.getInt(key, 102)
    }

    fun decrementLimit(l: LimitOfEarnings) {
        setInt(l.prefName, getInt(l.prefName) - 1)
    }

    fun isLimitOver(l: LimitOfEarnings): Boolean {
        return getInt(l.prefName) <= 0
    }

    fun resetAllLimits() {
        for (limit in LimitOfEarnings.values()) {
            setInt(limit.prefName, 102)
        }
    }

    fun withLimitCheck(limit: LimitOfEarnings, doIfLimitNotOver: () -> Unit) {
        if(isLimitOver(limit)) {
            _context.showToast("Daily Limit Over")
        }
        else {
            doIfLimitNotOver()
        }
    }

    fun isLimitDefault(l: LimitOfEarnings): Boolean {
        return (getInt(l.prefName) == 102)
    }

    companion object {
        const val LIMIT_OF_GUESS_NUMBER = "limitOfGuessNumber"
        const val LIMIT_OF_QUIZ = "limitOfQuiz"
        const val LIMIT_OF_SURVEY = "limitOfSurvey"

    }

}