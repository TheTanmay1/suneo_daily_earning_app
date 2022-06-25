package com.unwiringapps.earningnapp.model

import android.content.Context
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.Extra.LimitOfEarnings

data class UserLimits(
    var date: String = "no",
    var limitOfBox: Int = 102,
    var limitOfWheel: Int = 102,
    var limitOfCard: Int = 102,
    var limitOfVideo: Int = 102,
    var limitOfSurvey: Int = 102,
    var limitOfGuessNumber: Int = 102,
    var limitOfQuiz: Int = 102
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "date" to this.date,
            "limitOfBox" to this.limitOfBox,
            "limitOfCard" to this.limitOfCard,
            "limitOfSurvey" to this.limitOfSurvey,
            "limitOfGuessNumber" to this.limitOfGuessNumber,
            "limitOfQuiz" to this.limitOfQuiz,
            "limitOfVideo" to this.limitOfVideo,
            "limitOfWheel" to this.limitOfWheel,
        )
    }

    companion object {
        private const val TAG = "UserLimits"
        fun getFromPref(c: Context): UserLimits {
            val pref = PrefManager(c)
            return UserLimits(
                date = pref.getString("date")?: "no",
                limitOfBox = pref.getInt("limitOfBox"),
                limitOfCard = pref.getInt("limitOfCard"),
                limitOfSurvey = pref.getInt(LimitOfEarnings.LimitOfSurvey.prefName),
                limitOfGuessNumber = pref.getInt(LimitOfEarnings.LimitOfGuessNumber.prefName),
                limitOfQuiz = pref.getInt(LimitOfEarnings.LimitOfQuiz.prefName),
                limitOfVideo = pref.getInt("limitOfVideo"),
                limitOfWheel = pref.getInt("limitOfWheel"),
            )
        }
    }
}
