package com.unwiringapps.earningnapp.earningOption

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.unwiringapps.earningnapp.Extra.constants
import com.unwiringapps.earningnapp.model.ExtraModel.WatchVideoModel
import com.unwiringapps.earningnapp.R
import com.unwiringapps.earningnapp.repo.MyRepo
import com.unwiringapps.earningnapp.databinding.ActivitySurveyBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.pollfish.Pollfish
import com.pollfish.builder.Params
import com.pollfish.callback.*
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import com.unwiringapps.earningnapp.Extra.LimitOfEarnings
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.Extra.dismissProgress
import com.unwiringapps.earningnapp.Extra.showProgress
import java.util.*

class SurveyActivity : AppCompatActivity(),
    PollfishSurveyCompletedListener,
    PollfishSurveyReceivedListener,
    PollfishOpenedListener,
    PollfishClosedListener,
    PollfishSurveyNotAvailableListener,
    PollfishUserNotEligibleListener,
    PollfishUserRejectedSurveyListener {

    lateinit var binding: ActivitySurveyBinding
    val TAG = "@@@@"
    lateinit var logText: TextView
    lateinit var params: Params
    var pollFishSurveyDone = false
    lateinit var db: FirebaseFirestore
    private var winningCoins: String? = "0"
    private var prefManager: PrefManager? = null
    val activity = this

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        logText = findViewById(R.id.logText)
        prefManager = PrefManager(this)

        showProgress()
        loadBanner()



        db.collection("Earning").document("survey").addSnapshotListener { value, error ->
            if (error == null) {
                val data = value?.toObject(WatchVideoModel::class.java)

                if(prefManager?.isLimitDefault(LimitOfEarnings.LimitOfSurvey) == true) {
                    binding.limit.text = "Limit: ${data?.limit} left"
                    data?.limit?.let {
                        prefManager?.setInt(LimitOfEarnings.LimitOfSurvey.prefName, it.toInt())
                    }
                }
                else {
                    binding.limit.text = "Limit: ${prefManager?.getInt(LimitOfEarnings.LimitOfSurvey.prefName)} left"
                }

                winningCoins = data?.winningCoins
            }
            dismissProgress()
        }

       // idhar se kera hai copy gandu survey ke liye


        params = Params.Builder("16c5a820-cbcc-43fc-8a23-046d772ab39e")
//done
            .rewardMode(true)
            .build()

        Pollfish.initWith(this, params)


        setListener()

// change mode before you release


        // change mode before you release
        val isTestMode = false
        UnityAds.initialize(applicationContext, SurveyActivity.UNITY_GAME_ID, isTestMode)

        UnityAds.addListener(object : IUnityAdsListener {
            override fun onUnityAdsReady(s: String) {
                // p0 is ad's ID
            }

            override fun onUnityAdsStart(s: String) {}
            override fun onUnityAdsFinish(s: String, finishState: UnityAds.FinishState) {}
            override fun onUnityAdsError(unityAdsError: UnityAds.UnityAdsError, s: String) {}
        })

    }


    @SuppressLint("SetTextI18n")
    override fun onPollfishSurveyCompleted(surveyInfo: SurveyInfo) {

        logText.setText(R.string.survey_completed)
        pollFishSurveyDone = true
        val repository = MyRepo(this@SurveyActivity)
        repository.addCoins(winningCoins.toString().toDouble())
        prefManager?.decrementLimit(LimitOfEarnings.LimitOfSurvey)
        binding.limit.text = "Limit: ${prefManager?.getInt(LimitOfEarnings.LimitOfSurvey.prefName)} left"
        finish()
    }


    override fun onPollfishOpened() {
        Log.d(TAG, getString(R.string.on_pollfish_opened))
    }

    override fun onPollfishClosed() {
        Log.d(TAG, getString(R.string.on_pollfish_closed))
        finish()
    }

    override fun onPollfishSurveyNotAvailable() {
        Log.d(TAG, getString(R.string.survey_not_available))
        logText.setText(R.string.survey_not_available)
        finish()
    }

    override fun onUserNotEligible() {
        Log.d(TAG, getString(R.string.user_not_eligible))
        logText.setText(R.string.user_not_eligible)
        finish()
    }

    override fun onUserRejectedSurvey() {
        logText.setText(R.string.user_rejected_survey)
        finish()
    }

    override fun onPollfishSurveyReceived(surveyInfo: SurveyInfo?) {
        logText.setText(R.string.survey_received)
    }

    override fun onResume() {
        super.onResume()
        Pollfish.initWith(this, params)
        if (!pollFishSurveyDone) {
            if (!Pollfish.isPollfishPresent()) {
                logText.setText(R.string.logging)
            } else {
                logText.setText(R.string.survey_received)
            }
        }
    }

    fun loadBanner() {
        val view = BannerView(this@SurveyActivity, constants.banner, UnityBannerSize(320, 50))
        view.load()
        binding.bannerAd.addView(view)
    }

    companion object {
        private const val TAG = "SurveyActivity"
        val UNITY_GAME_ID = "4429147"
        private val INTERSTITIAL_ID = "Interstitial_Android"
        private val REWARDED_ID = "Rewarded_Android"
        private val BANNER_ID = "Banner_Android"
    }

    private fun setListener() {
        findViewById<View>(R.id.button2).setOnClickListener { v: View? ->
            showAd(
                SurveyActivity.INTERSTITIAL_ID
            )


        }

//        findViewById<View>(R.id.buttonBanner).setOnClickListener { v: View? ->
//            showAd(
//                MainActivity.BANNER_ID
//            )
//        }
    }


    private fun showAd(adID: String) {
        UnityAds.show(this, adID, object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(
                s: String,
                unityAdsShowError: UnityAds.UnityAdsShowError,
                s1: String
            ) {
                Toast.makeText(this@SurveyActivity, "Ads didn't load, please either wait for ads to load, or try this task after few seconds, or try using vpn if this issue occurs everytime", Toast.LENGTH_LONG).show()
            }

            override fun onUnityAdsShowStart(s: String) {}
            override fun onUnityAdsShowClick(s: String) {}
            override fun onUnityAdsShowComplete(
                s: String,
                unityAdsShowCompletionState: UnityAds.UnityAdsShowCompletionState
            ) {


                gandusurvey()





                Toast.makeText(this@SurveyActivity, "Ads succesfully appeared, now you can proceed clicking on start the survey button", Toast.LENGTH_SHORT).show()
//                Toast.makeText(
//                    ClientProperties.getActivity(), "ads chal gaya succesfully!!",
//                    Toast.LENGTH_LONG
//                ).show()
            }
        })
    }


    fun gandusurvey() {

        binding.button2.setOnClickListener {
            prefManager?.withLimitCheck(LimitOfEarnings.LimitOfSurvey) {
                Pollfish.show()
            }
        }

    }



}