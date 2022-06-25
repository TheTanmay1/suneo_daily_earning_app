package com.unwiringapps.earningnapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.unity3d.ads.UnityAds
import com.unwiringapps.earningnapp.Extra.constants
import com.unwiringapps.earningnapp.Extra.setConnectionLiveDataObserver
import com.unwiringapps.earningnapp.R
import java.util.*

class privacy_policyyy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privacy_policy_terms_conditions)

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        val activity = this

//        Timer().scheduleAtFixedRate(object : TimerTask() {
//            override fun run() {
//                Log.d(TAG, "run: running ad in MainActivity")
//                if (UnityAds.isReady(constants.interstitial)) {
//                    Log.d(TAG, "showAd: ad ready and showing...")
//                    UnityAds.show(activity, constants.interstitial)
//                }
//            }
//        }, 1000, 40000)


    }

    companion object {
        private const val TAG = "privacy_policy"
    }

}