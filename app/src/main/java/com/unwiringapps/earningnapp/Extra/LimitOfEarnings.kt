package com.unwiringapps.earningnapp.Extra

import com.unwiringapps.earningnapp.DB.PrefManager

enum class LimitOfEarnings(val prefName: String) {
    LimitOfSurvey(PrefManager.LIMIT_OF_SURVEY),
    LimitOfGuessNumber(PrefManager.LIMIT_OF_GUESS_NUMBER),
    LimitOfQuiz(PrefManager.LIMIT_OF_QUIZ)
}