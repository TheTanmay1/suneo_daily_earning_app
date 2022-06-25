package com.unwiringapps.earningnapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.unity3d.ads.UnityAds
import com.unwiringapps.earningnapp.Extra.constants
import com.unwiringapps.earningnapp.Extra.constants.interstitial
import com.unwiringapps.earningnapp.R
import java.util.*

class AboutUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us_page)

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        val activity = this

//        Timer().scheduleAtFixedRate(object : TimerTask() {
//            override fun run() {
//                Log.d(TAG, "run: running ad in MainActivity")
//                if (UnityAds.isReady (constants.interstitial)) {
//                    Log.d(TAG, "showAd: ad ready and showing...")
//                    UnityAds.show (activity, constants.interstitial)
//                }
//            }
//        }, 1000,400000)
    }

    companion object {
        private const val TAG = "AboutUs"
    }
}