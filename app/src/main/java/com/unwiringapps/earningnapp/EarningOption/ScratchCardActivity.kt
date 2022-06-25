package com.unwiringapps.earningnapp.earningOption

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.unwiringapps.earningnapp.databinding.ActivityScratchCardBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.anupkumarpanwar.scratchview.ScratchView

import com.anupkumarpanwar.scratchview.ScratchView.IRevealListener
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.Extra.constants
import com.unwiringapps.earningnapp.Extra.dismissProgress
import com.unwiringapps.earningnapp.Extra.showProgress
import com.unwiringapps.earningnapp.model.ExtraModel.ScratchModel
import com.unwiringapps.earningnapp.repo.MyRepo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

import java.util.*


class ScratchCardActivity : AppCompatActivity() {


    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityScratchCardBinding
    var winningCoins: String? = "0"
    var limit: String? = "0"
    var TAG = "@@@@"
    val activity = this

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScratchCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        val manager = PrefManager(this)
        showProgress()

        loadBanner()


        Log.e(TAG, "onCreate: " + manager.getInt("limitOfCard"))

        if (manager.getInt("limitOfCard") == 0) {
            binding.scratchCard.visibility = View.INVISIBLE
            binding.resultText.text = "Daily Scratch Card Over"
        }

        db.collection("Earning").document("card")
            .addSnapshotListener { value, error ->
                if (error == null) {
                    val data = value?.toObject(ScratchModel::class.java)
                    winningCoins = data?.winningCoins
                    limit = data?.limit
                    dismissProgress()
                    if (manager.getInt("limitOfCard") == 102) {
                        binding.limit.text = "$limit"
                        manager.setInt("limitOfCard", limit!!.toInt())
                    } else {
                        binding.limit.text = manager.getInt("limitOfCard").toString()
                    }
                }

            }



        setListener()

// change mode before you release


        // change mode before you release
        val isTestMode = false
        UnityAds.initialize(applicationContext, ScratchCardActivity.UNITY_GAME_ID, isTestMode)

        UnityAds.addListener(object : IUnityAdsListener {
            override fun onUnityAdsReady(s: String) {
                // p0 is ad's ID
            }

            override fun onUnityAdsStart(s: String) {}
            override fun onUnityAdsFinish(s: String, finishState: UnityAds.FinishState) {}
            override fun onUnityAdsError(unityAdsError: UnityAds.UnityAdsError, s: String) {}
        })



    }


    private fun setListener() {
        binding.scratchCard.setRevealListener(object : IRevealListener {
            @SuppressLint("SetTextI18n")
            override fun onRevealed(scratchView: ScratchView) {//ye
            showAd(
                ScratchCardActivity.INTERSTITIAL_ID
            )


        } //ye

            override fun onRevealPercentChangedListener(scratchView: ScratchView?, percent: Float) {

            }
        })

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
                Toast.makeText(
                    this@ScratchCardActivity,
                    "Ads didn't load, please either wait for ads to load, or try this task after few seconds, or try using vpn if this issue occurs everytime",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onUnityAdsShowStart(s: String) {}
            override fun onUnityAdsShowClick(s: String) {}
            override fun onUnityAdsShowComplete(
                s: String,
                unityAdsShowCompletionState: UnityAds.UnityAdsShowCompletionState
            ) {


                        gandu3 ()


                        Toast.makeText(
                            this@ScratchCardActivity,
                            "Ads succesfully load, and your scratch card also works perfectly",
                            Toast.LENGTH_LONG
                        ).show()
                    }



        })}



            @SuppressLint("SetTextI18n")
            fun gandu3 () {

                val manager = PrefManager(this@ScratchCardActivity)
                val winningAmount = (0..winningCoins!!.toInt()).random().toString()
                binding.resultText.text = "You won $winningAmount Coins"
                manager.setInt("limitOfCard", manager.getInt("limitOfCard") - 1)
                binding.limit.text = manager.getInt("limitOfCard").toString()
                //add coins
                val repo = MyRepo(this@ScratchCardActivity)
                repo.addCoins(winningAmount.toDouble())
                binding.scratchCard.visibility = View.INVISIBLE

                if (manager.getInt("limitOfCard") == 0) {
                    binding.scratchCard.visibility = View.INVISIBLE
                    binding.resultText.text = "Daily Scratch Card Over"
                }

            }


    fun loadBanner() {
        val view = BannerView(this@ScratchCardActivity, constants.banner, UnityBannerSize(320, 50))
        view.load()
        binding.bannerAd.addView(view)
    }

    companion object {
        val UNITY_GAME_ID = "4429147"
        private val INTERSTITIAL_ID = "Interstitial_Android"
        private val REWARDED_ID = "Rewarded_Android"
        private val BANNER_ID = "Banner_Android"
        private const val TAG = "ScratchCardActivity"
    }


}