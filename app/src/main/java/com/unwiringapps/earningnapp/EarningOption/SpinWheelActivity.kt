package com.unwiringapps.earningnapp.earningOption

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.model.ExtraModel.SpinWheelModel
import com.unwiringapps.earningnapp.model.ExtraModel.WatchVideoModel
import com.unwiringapps.earningnapp.repo.MyRepo
import com.unwiringapps.earningnapp.SpinWheel.model.LuckyItem
import com.unwiringapps.earningnapp.databinding.ActivitySpinWheelBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.IUnityAdsShowListener

import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAds.*
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import com.unwiringapps.earningnapp.Extra.*
import com.unwiringapps.earningnapp.R

import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class SpinWheelActivity : AppCompatActivity() {



    lateinit var binding: ActivitySpinWheelBinding
    lateinit var db: FirebaseFirestore
    lateinit var repo: MyRepo
    var randomNumber = 0
    var listSize = 0
    lateinit var item: ArrayList<LuckyItem>
    lateinit var list: ArrayList<SpinWheelModel>


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpinWheelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // change mode before you release


        // change mode before you release
        val isTestMode = false
        UnityAds.initialize(applicationContext, SpinWheelActivity.UNITY_GAME_ID, isTestMode)

        UnityAds.addListener(object : IUnityAdsListener {
            override fun onUnityAdsReady(s: String) {
                // p0 is ad's ID
            }

            override fun onUnityAdsStart(s: String) {}
            override fun onUnityAdsFinish(s: String, finishState: FinishState) {}
            override fun onUnityAdsError(unityAdsError: UnityAdsError, s: String) {}
        })

        setListener()

        repo = MyRepo(activity = this)


        val id = intent.getStringExtra("id")
        val luckeyItemList = arrayListOf<LuckyItem>()
        db = FirebaseFirestore.getInstance()
        var limit = 0

        val activity = this



        loadBanner()



        showProgress()

        db.collection("Earning").document(id!!).collection("spinwheel")
            .addSnapshotListener { value, error ->
                list = arrayListOf<SpinWheelModel>()

                val data = value?.toObjects(SpinWheelModel::class.java)
                list.addAll(data!!)

                for (k in data) {
                    val luckyItem = LuckyItem()
                    luckyItem.topText = k.topText
                    luckyItem.color = Color.parseColor(k.color!!)
                    luckyItem.textColor = Color.parseColor(k.textColor!!)
                    luckyItem.secondaryText = k.secondaryText
                    luckeyItemList.add(luckyItem)
                }

                listSize = luckeyItemList.size
                item = luckeyItemList

                binding.wheelview.setData(luckeyItemList)
                binding.wheelview.setRound(5)

                dismissProgress()
            }
        val manager = PrefManager(this)
        db.collection("Earning").document("spinWheel").addSnapshotListener { value, error ->

            if (error == null) {
                val data = value?.toObject(WatchVideoModel::class.java)

                Log.e("@@@", "onCreate: $data")

                limit = data?.limit.toString().toInt()
                Log.e("luck Box", "onCreate: $limit")
                if (manager.getInt("limitOfWheel") == 102) {
                    binding.limit.text = "$limit"
                    manager.setInt("limitOfWheel", limit)
                } else {
                    binding.limit.text = manager.getInt("limitOfWheel").toString()
                }
                dismissProgress()
            }

        }




    }
// idhar se yaaar

    private fun setListener() {
        findViewById<View>(R.id.spinButton).setOnClickListener { v: View? ->
            showAd(
                SpinWheelActivity.INTERSTITIAL_ID
            )
        }
//        findViewById<View>(R.id.buttonRewarded).setOnClickListener { v: View? ->
//            showAd(
//                MainActivity.REWARDED_ID
//            )
//        }
//        findViewById<View>(R.id.buttonBanner).setOnClickListener { v: View? ->
//            showAd(
//                MainActivity.BANNER_ID
//            )
//        }
    }


    private fun showAd(adID: String) {
        show(this, adID, object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(
                s: String,
                unityAdsShowError: UnityAdsShowError,
                s1: String
            ) {
                Toast.makeText(this@SpinWheelActivity, "Ads didn't load, please either wait for ads to load, or try this task after few seconds, or try using vpn if this issue occurs everytime", Toast.LENGTH_LONG).show()
            }

            override fun onUnityAdsShowStart(s: String) {}
            override fun onUnityAdsShowClick(s: String) {}
            override fun onUnityAdsShowComplete(
                s: String,
                unityAdsShowCompletionState: UnityAdsShowCompletionState
            ) {


                    gandu ()




                    Toast.makeText(this@SpinWheelActivity, "Ads succesfully load, and your spinner also works perfectly", Toast.LENGTH_LONG).show()
//                Toast.makeText(
//                    ClientProperties.getActivity(), "ads chal gaya succesfully!!",
//                    Toast.LENGTH_LONG
//                ).show()
            }
        })
    }



    fun loadBanner() {
        val view = BannerView(this@SpinWheelActivity, constants.banner, UnityBannerSize(320, 50))
        view.load()
        binding.bannerAd.addView(view)
    }

    companion object {

        val UNITY_GAME_ID = "4429147"
        private val INTERSTITIAL_ID = "Interstitial_Android"
        private val REWARDED_ID = "Rewarded_Android"
        private val BANNER_ID = "Banner_Android"

        private const val TAG = "SpinWheelActivity"
    }


    fun gandu () {





//            Toast.makeText(this@SpinWheelActivity, "Ads are ready, ads will run in 10 seconds plz wait", Toast.LENGTH_LONG).show()

//            Timer().scheduleAtFixedRate(object : TimerTask() {
//                override fun run() {
//                    Log.d(TAG, "run: running ad in MainActivity")
//                    if (UnityAds.isReady (constants.interstitial)) {
//                        Log.d(TAG, "showAd: ad ready and showing...")
//                        UnityAds.show (activity, constants.interstitial)
//                    }
//                }
//            }, 9000, 180000)


        val manager = PrefManager(this)

            if (manager.getInt("limitOfWheel") > 0) {
                manager.setInt("limitOfWheel", manager.getInt("limitOfWheel") - 1)
                binding.limit.text = manager.getInt("limitOfWheel").toString()

                binding.spinButton.isClickable = false
                randomNumber = Random.nextInt(1, listSize)
                binding.wheelview.startLuckyWheelWithTargetIndex(randomNumber)

                binding.wheelview.setLuckyRoundItemSelectedListener {
                    val coins = list[randomNumber].coins
                    repo.addCoins(coins = coins.toString().toDouble())
                    Handler(Looper.getMainLooper()).postDelayed(Runnable { finish() }, 2000)

                }
            } else {
                showToast("Daily limit Over")
            }


    }

}