package com.unwiringapps.earningnapp.earningOption

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.Extra.constants.reward
import com.unwiringapps.earningnapp.Extra.dismissProgress
import com.unwiringapps.earningnapp.Extra.showProgress
import com.unwiringapps.earningnapp.Extra.showToast
import com.unwiringapps.earningnapp.model.ExtraModel.WatchVideoModel
import com.unwiringapps.earningnapp.repo.MyRepo
import com.unwiringapps.earningnapp.databinding.ActivityWatchVideoBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds

class WatchVideoActivity : AppCompatActivity() {
    var winningCoins: String? = "0"
    var limit: String? = "0"
    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityWatchVideoBinding
    private final var TAG = "@@@@"
    lateinit var manager: PrefManager
    private var unityAdsListener: UnityAdsListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatchVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        manager = PrefManager(this)
        showProgress()



        db.collection("Earning").document("video")
            .addSnapshotListener { value, error ->
                if (error == null) {
                    val data = value?.toObject<WatchVideoModel>()
                    winningCoins = data?.winningCoins
                    limit = data?.limit
                    Log.e("@@@@", "onCreate: Coins $winningCoins limit $limit")
                    binding.limit.text = limit.toString()

                    if(unityAdsListener != null) {
                        UnityAds.removeListener(unityAdsListener)
                    }

                    unityAdsListener = UnityAdsListener(
                        this@WatchVideoActivity,
                        winningCoins,
                        binding.limit
                    )
                    UnityAds.addListener(unityAdsListener)
                }

                dismissProgress()

                if (manager.getInt("limitOfVideo") == 102) {
                    Log.e("@@@@", "onCreate: First time")
                    Log.e("@@@@", "onCreate: Limit Set " + limit.toString())
                    binding.limit.text = limit.toString()
                    manager.setInt("limitOfVideo", limit.toString().toInt())
                } else {
                    Log.e("@@@@", "onCreate: Second Time time")
                    binding.limit.text = manager.getInt("limitOfVideo").toString()
                }

            }

        binding.btnWatchVideo.setOnClickListener {

            if (manager.getInt("limitOfVideo") > 0) {
                manager.setInt("limitOfVideo", manager.getInt("limitOfVideo") - 1)
                binding.limit.text = manager.getInt("limitOfVideo").toString()
                if (UnityAds.isReady(reward)) {
                    UnityAds.show(this, reward)
                }
                binding.btnWatchVideo.isEnabled = false
                binding.btnWatchVideo.alpha = 0.5f
            } else {
                showToast("Daily limit over")
                binding.btnWatchVideo.isEnabled = false
                binding.btnWatchVideo.alpha = 0.5f
            }
        }

    }

    private class UnityAdsListener(
        val watchVideoActivity: WatchVideoActivity,
        val winningCoins: String?,
        val limit: TextView,
    ) : IUnityAdsListener {


        override fun onUnityAdsReady(adUnitId: String) {
            Log.e("===>", "onUnityAdsReady: ")

        }

        override fun onUnityAdsStart(adUnitId: String) {
            Log.e("===>", "onUnityAdsStart: ")
        }

        override fun onUnityAdsFinish(adUnitId: String, finishState: UnityAds.FinishState) {
            when (finishState) {
                UnityAds.FinishState.COMPLETED -> {
                    Log.e("===>", "onUnityAds Complete: Coins Added Successfully")
                    if(adUnitId == reward) {
                        addCoins()
                    }
                }
                UnityAds.FinishState.SKIPPED -> {
                    Log.e("===>", "onUnityAds  Skipped: ")
                }
                UnityAds.FinishState.ERROR -> {
                    Log.e("===>", "onUnityAds Error: ")
                }
            }
        }

        private fun addCoins() {
            val repo = MyRepo(watchVideoActivity)
            repo.addCoins(winningCoins.toString().toDouble())
        }


        override fun onUnityAdsError(error: UnityAds.UnityAdsError, message: String) {
            Log.e("===>", "onUnityAdsError: ")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        UnityAds.removeListener(unityAdsListener)
    }

}

