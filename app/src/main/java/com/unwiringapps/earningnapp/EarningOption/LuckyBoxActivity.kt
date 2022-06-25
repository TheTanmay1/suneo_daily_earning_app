package com.unwiringapps.earningnapp.earningOption

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.model.ExtraModel.WatchVideoModel
import com.unwiringapps.earningnapp.repo.MyRepo
import com.unwiringapps.earningnapp.databinding.ActivityLuckyBoxBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import com.unwiringapps.earningnapp.Extra.*
import com.unwiringapps.earningnapp.R
import me.samlss.bloom.effector.BloomEffector

import me.samlss.bloom.Bloom
import java.util.*


class LuckyBoxActivity : AppCompatActivity() {

    lateinit var binding: ActivityLuckyBoxBinding
    lateinit var db: FirebaseFirestore
    lateinit var myRepo: MyRepo
    lateinit var manager: PrefManager
    val activity = this
    private var winningCoins = 0
    var limit = 0


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLuckyBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showProgress()


        db = FirebaseFirestore.getInstance()
        myRepo = MyRepo(this)
        manager = PrefManager(this)

        loadBanner()




        db.collection("Earning").document("box")
            .addSnapshotListener { value, error ->
                if (error == null) {
                    val data = value?.toObject(WatchVideoModel::class.java)
                    winningCoins = data?.winningCoins.toString().toInt()
                    limit = data?.limit.toString().toInt()

                    Log.e("luck Box", "onCreate: $winningCoins")
                    Log.e("luck Box", "onCreate: $limit")

                    if (manager.getInt("limitOfBox") == 102) {
                        binding.limit.text = "$limit"
                        manager.setInt("limitOfBox", limit)
                    } else {
                        binding.limit.text = manager.getInt("limitOfBox").toString()
                    }
                    dismissProgress()
                }
            }






        setListener()

// change mode before you release


        // change mode before you release
        val isTestMode = false
        UnityAds.initialize(applicationContext, LuckyBoxActivity.UNITY_GAME_ID, isTestMode)

        UnityAds.addListener(object : IUnityAdsListener {
            override fun onUnityAdsReady(s: String) {
                // p0 is ad's ID
            }

            override fun onUnityAdsStart(s: String) {}
            override fun onUnityAdsFinish(s: String, finishState: UnityAds.FinishState) {}
            override fun onUnityAdsError(unityAdsError: UnityAds.UnityAdsError, s: String) {}
        })


    }

//oncreate end here

    private fun setListener() {
        findViewById<View>(R.id.giftImg).setOnClickListener { v: View? ->
            showAd(
                LuckyBoxActivity.INTERSTITIAL_ID
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
                Toast.makeText(this@LuckyBoxActivity, "Ads didn't load, please either wait for ads to load, or try this task after few seconds, or try using vpn if this issue occurs everytime", Toast.LENGTH_LONG).show()
            }

            override fun onUnityAdsShowStart(s: String) {}
            override fun onUnityAdsShowClick(s: String) {}
            override fun onUnityAdsShowComplete(
                s: String,
                unityAdsShowCompletionState: UnityAds.UnityAdsShowCompletionState
            ) {


                    gandu2()





                Toast.makeText(this@LuckyBoxActivity, "Ads succesfully appeared", Toast.LENGTH_LONG).show()
//                Toast.makeText(
//                    ClientProperties.getActivity(), "ads chal gaya succesfully!!",
//                    Toast.LENGTH_LONG
//                ).show()
            }
        })
    }



    fun loadBanner() {
        val view = BannerView(this@LuckyBoxActivity, constants.banner, UnityBannerSize(320, 50))
        view.load()
        binding.bannerAd.addView(view)
    }
    companion object {

        val UNITY_GAME_ID = "4429147"
        private val INTERSTITIAL_ID = "Interstitial_Android"
        private val REWARDED_ID = "Rewarded_Android"
        private val BANNER_ID = "Banner_Android"
        private const val TAG = "LuckyBoxActivity"
    }


    @SuppressLint("SetTextI18n")
    fun gandu2 () {

        val winningAmount = (0..winningCoins).random()


        if (manager.getInt("limitOfBox") > 0) {
            Bloom.with(this)
                .setParticleRadius(5f)
                .setEffector(
                    BloomEffector.Builder()
                        .setDuration(1500)
                        .setAnchor(
                            (binding.giftImg.width / 2).toFloat(),
                            (binding.giftImg.height / 2).toFloat()
                        )
                        .build()
                )
                .boom(binding.giftImg)

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                binding.giftImg.visibility = View.INVISIBLE
            }, 1000)
            Handler(Looper.getMainLooper()).postDelayed(Runnable {

                manager.setInt("limitOfBox", manager.getInt("limitOfBox") - 1)
                binding.limit.text = manager.getInt("limitOfBox").toString()

                binding.resultText.visibility = View.VISIBLE

                if (winningAmount == 0) {
                    binding.resultText.text =
                        "Oops !! better luck next time..."
                } else {
                    binding.resultText.text = "$winningAmount Coins"
                    myRepo.addCoins(winningAmount.toString().toDouble())
                }
            }, 1500)

        } else {
            showToast("Daily Limit Over")
        }

    }

}
