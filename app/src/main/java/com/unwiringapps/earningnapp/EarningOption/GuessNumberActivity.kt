package com.unwiringapps.earningnapp.earningOption

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.Extra.LimitOfEarnings
import com.unwiringapps.earningnapp.Extra.constants.banner
import com.unwiringapps.earningnapp.Extra.dismissProgress
import com.unwiringapps.earningnapp.Extra.showProgress
import com.unwiringapps.earningnapp.R
import com.unwiringapps.earningnapp.databinding.ActivityGuessNumberBinding
import com.unwiringapps.earningnapp.model.ExtraModel.GuessNumberModel
import com.unwiringapps.earningnapp.repo.MyRepo
import kotlin.random.Random


class GuessNumberActivity : AppCompatActivity() {

    lateinit var binding: ActivityGuessNumberBinding
    var winningCoins: String? = "0"
    var losscoins: String? = "0"
    lateinit var myRepo: MyRepo
    var prefManager: PrefManager? = null

    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    val activity = this
    private val random = Random.nextInt(0, 100)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuessNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)



        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        prefManager = PrefManager(this)

        showProgress()
        loadBanner()


        db = FirebaseFirestore.getInstance()
        myRepo = MyRepo(this)

        db.collection("Earning").document("number")
            .addSnapshotListener { value, error ->
                if (error == null) {
                    val data = value?.toObject(GuessNumberModel::class.java)
                    winningCoins = data?.coins
                    losscoins = data?.losscoins

                    if(prefManager?.isLimitDefault(LimitOfEarnings.LimitOfGuessNumber) == true) {
                        Log.d(TAG, "onCreate: limit is 102 now")
                        binding.limit.text = "Limit: ${data?.limit} left"
                        data?.limit?.let {
                            prefManager?.setInt(LimitOfEarnings.LimitOfGuessNumber.prefName, it)
                        }
                    }
                    else {
                        Log.d(
                            TAG,
                            "onCreate: limit is ${prefManager?.getInt(LimitOfEarnings.LimitOfGuessNumber.prefName)}"
                        )
                        binding.limit.text = "Limit: ${prefManager?.getInt(LimitOfEarnings.LimitOfGuessNumber.prefName)} left"
                    }

                    binding.textView10.text = ("if you will win you get $winningCoins coins and if not \nthen you will lose $losscoins coins")

                }
                dismissProgress()

            }

        binding.txtFirst.text = random.toString()

        setListener()

// change mode before you release


        // change mode before you release
        val isTestMode = false
        UnityAds.initialize(applicationContext, GuessNumberActivity.UNITY_GAME_ID, isTestMode)

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
         findViewById<View>(R.id.btnHigh).setOnClickListener { v: View? ->
            showAd(
                GuessNumberActivity.INTERSTITIAL_ID
            )


        }
        findViewById<View>(R.id.btnLow).setOnClickListener { v: View? ->
            showAd(
                GuessNumberActivity.REWARDED_ID
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
                Toast.makeText(this@GuessNumberActivity, "Ads didn't load, please either wait for ads to load, or try this task after few seconds, or try using VPN if this issue occurs everytime", Toast.LENGTH_LONG).show()
            }

            override fun onUnityAdsShowStart(s: String) {}
            override fun onUnityAdsShowClick(s: String) {}
            override fun onUnityAdsShowComplete(
                s: String,
                unityAdsShowCompletionState: UnityAds.UnityAdsShowCompletionState
            ) {

                binding.btnHigh.setOnClickListener {
                    gandu1a()

                }

                binding.btnLow.setOnClickListener {
                    gandu1b()

                }

                Toast.makeText(this@GuessNumberActivity, "Ads succesfully appeared , now you can continue the task", Toast.LENGTH_LONG).show()
//                Toast.makeText(
//                    ClientProperties.getActivity(), "ads chal gaya succesfully!!",
//                    Toast.LENGTH_LONG
//                ).show()
            }
        })
    }

    fun loadBanner() {
        val view = BannerView(this@GuessNumberActivity, banner, UnityBannerSize(320, 50))
        view.load()
        binding.bannerAd.addView(view)
    }



    companion object {

        val UNITY_GAME_ID = "4429147"
        private val INTERSTITIAL_ID = "Interstitial_Android"
        private val REWARDED_ID = "Rewarded_Android"
        private val BANNER_ID = "Banner_Android"

        private const val TAG = "GuessNumberActivity"

    }


    @SuppressLint("SetTextI18n")
    fun gandu1a () {

        binding.btnHigh.isClickable = false
        binding.btnLow.isClickable = false
        binding.btnHigh.alpha = 0.5f
        binding.btnLow.alpha = 0.5f

        prefManager?.withLimitCheck(LimitOfEarnings.LimitOfGuessNumber) {
            val random2 = Random.nextInt(0, 100)
            binding.txtSecond.text = random2.toString()
            Log.d(
                TAG,
                "onCreate: limit now is: ${prefManager!!.getInt(LimitOfEarnings.LimitOfGuessNumber.prefName)}"
            )
            prefManager!!.decrementLimit(LimitOfEarnings.LimitOfGuessNumber)
            binding.limit.text = "Limit: ${prefManager?.getInt(LimitOfEarnings.LimitOfGuessNumber.prefName)} left"

            if (random < random2) {
                binding.resultOfGuess.text = "You won"
                myRepo.addCoins(winningCoins.toString().toDouble())
            } else {
                binding.resultOfGuess.text = "You Loss"
                myRepo.removeCoins(losscoins.toString().toDouble())
            }

        }


    }


    @SuppressLint("SetTextI18n")
    fun gandu1b () {

        binding.btnHigh.isClickable = false
        binding.btnLow.isClickable = false
        binding.btnHigh.alpha = 0.5f
        binding.btnLow.alpha = 0.5f

//            Toast.makeText(this@GuessNumberActivity, "Ads are ready, ads will run in 10 seconds plz wait", Toast.LENGTH_LONG).show()
//            Timer().scheduleAtFixedRate(object : TimerTask() {
//                override fun run() {
//                    Log.d(TAG, "run: running ad in MainActivity")
//                    if (UnityAds.isReady (constants.interstitial)) {
//                        Log.d(TAG, "showAd: ad ready and showing...")
//                        UnityAds.show (activity, constants.interstitial)
//                    }
//                }
//            }, 1000, 180000)

        prefManager?.withLimitCheck(LimitOfEarnings.LimitOfGuessNumber) {
            val random2 = Random.nextInt(0, 100)
            binding.txtSecond.text = random2.toString()
            prefManager!!.decrementLimit(LimitOfEarnings.LimitOfGuessNumber)
            binding.limit.text = "Limit: ${prefManager?.getInt(LimitOfEarnings.LimitOfGuessNumber.prefName)} left"

            if (random > random2) {
                binding.resultOfGuess.text = "You won"
                myRepo.addCoins(winningCoins.toString().toDouble())
            } else {
                binding.resultOfGuess.text = "You Loss"
                myRepo.removeCoins(losscoins.toString().toDouble())
            }
        }


    }

}