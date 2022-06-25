package com.unwiringapps.earningnapp

import android.app.Application
import android.util.Log
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.Extra.constants.GAME_ID
import com.unwiringapps.earningnapp.Extra.constants.testing
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.onesignal.OneSignal
import com.unity3d.ads.UnityAds
import com.unwiringapps.earningnapp.Extra.LimitOfEarnings
import com.unwiringapps.earningnapp.model.UserModel
import com.unwiringapps.earningnapp.Repo.NotificationRepository
import java.text.SimpleDateFormat
import java.util.*

class AppClass : Application() {
    lateinit var manager: PrefManager
    var TAG = "@@@@"

    override fun onCreate() {
        super.onCreate()

        manager = PrefManager(this)
        UnityAds.initialize(this, GAME_ID, testing)

        val date: String =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        Log.e(TAG, "onCreate: ${manager.getString("date")}")
        Log.e(TAG, "onCreate: ${manager.getString("date") == "no"}")
        Log.e(TAG, "onCreate: $date")
        Log.e(TAG, "onCreate: ${manager.getString("date") != date}")

        OneSignal.initWithContext(this);
//        OneSignal.setAppId("b2f7f966-d8cc-11e4-bed1-df8f05be55ba")


        MobileAds.initialize(
            this
        ) { }


        Log.e(TAG, "onCreate: " + (manager.getString("date") == "no"))
        Log.e(TAG, "onCreate: " + (manager.getString("date") != date))
        Log.e(TAG, "onCreate: $date")
        Log.e(TAG, "onCreate: ${manager.getString("date")}")

        if (manager.getString("date") == "no") {
            manager.setString("date", date)
            //reset
            resetValues()
            loadLimitsFromRemote()
        } else {
            if (manager.getString("date") != date) {
                //reset Variables
                resetValues()
                manager.setString("date", date)
            }
        }
        loadLimitsFromRemote()


        //create notification channel
        val notificationRepo = NotificationRepository(this)
        notificationRepo.createNotificationChannel()

    }

    fun resetValues() {
        Log.e(TAG, "resetValues: ")
        manager.setInt("limitOfWheel", 102)
        manager.setInt("limitOfCard", 102)
        manager.setInt("limitOfBox", 102)
        manager.setInt("limitOfVideo", 102)
        manager.resetAllLimits()
    }

    fun loadLimitsFromRemote() {
        Log.d(TAG, "loadLimitsFromRemote: called")
        val date: String =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val user = Firebase.auth.currentUser
        if(user != null) {
            Firebase.firestore.collection("user").document(user.uid).get().addOnSuccessListener {
                Log.d(TAG, "loadLimitsFromRemote: success : $it")
                val userModel = it.toObject<UserModel>()
                Log.d(TAG, "loadLimitsFromRemote: userModel is: $userModel")
                if(userModel!= null) {
                    Log.d(TAG, "loadLimitsFromRemote: user model not null")
                    if(userModel.limits.date != date) {
                        manager.setString("date", date)
                        //reset
                        resetValues()
                        return@addOnSuccessListener
                    }
                    manager.setString("date", userModel.limits.date)
                    manager.setInt("limitOfWheel", userModel.limits.limitOfWheel)
                    manager.setInt("limitOfCard", userModel.limits.limitOfCard)
                    manager.setInt("limitOfBox", userModel.limits.limitOfBox)
                    manager.setInt("limitOfVideo", userModel.limits.limitOfVideo)
                    manager.setInt(LimitOfEarnings.LimitOfSurvey.prefName, userModel.limits.limitOfSurvey)
                    manager.setInt(LimitOfEarnings.LimitOfGuessNumber.prefName, userModel.limits.limitOfGuessNumber)
                    manager.setInt(LimitOfEarnings.LimitOfQuiz.prefName, userModel.limits.limitOfQuiz)
                }
            }
                .addOnFailureListener {
                    Log.e(TAG, "loadLimitsFromRemote: failed", it)
                }
        }
    }
}