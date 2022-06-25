package com.unwiringapps.earningnapp.earningOption

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.unwiringapps.earningnapp.Extra.showToast
import com.unwiringapps.earningnapp.model.QuizModel
import com.unwiringapps.earningnapp.R
import com.unwiringapps.earningnapp.databinding.ActivityPlayQuizBinding
import com.google.firebase.firestore.FirebaseFirestore

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper

import android.view.LayoutInflater
import android.view.View
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unwiringapps.earningnapp.Extra.constants
import com.unwiringapps.earningnapp.MainActivity
import com.unwiringapps.earningnapp.model.ExtraModel.DailyRewardModel
import com.unwiringapps.earningnapp.repo.MyRepo
import com.unwiringapps.earningnapp.databinding.DialogResultBinding
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import com.unwiringapps.earningnapp.Extra.LimitOfEarnings
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.Extra.dismissProgress
import com.unwiringapps.earningnapp.Extra.showProgress
import java.util.*
import kotlin.collections.ArrayList


class PlayQuizActivity : AppCompatActivity() {

    lateinit var binding: ActivityPlayQuizBinding
    var cureentpos = 1
    var selectedoptionInt = 0
    var wrong = 0
    var canGoNextQuestion: Boolean = false
    lateinit var allqestionlist: ArrayList<QuizModel>
    var coinsMy = 0.0
    var prefManager: PrefManager? = null
    val activity = this
    var clickcount = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            supportActionBar?.hide()
        }

        val db = FirebaseFirestore.getInstance()
        prefManager = PrefManager(this)
        showProgress()



        loadBanner()


        db.collection("Earning").document("quiz").addSnapshotListener { value, error ->

            val data = value?.toObject(DailyRewardModel::class.java)

            if(prefManager?.isLimitDefault(LimitOfEarnings.LimitOfQuiz) == true) {
                binding.limit.text = "Limit: ${data?.limit} left"
                data?.limit?.let {
                    prefManager?.setInt(LimitOfEarnings.LimitOfQuiz.prefName, it)
                }
            }
            else {
                binding.limit.text = "Limit: ${prefManager?.getInt(LimitOfEarnings.LimitOfQuiz.prefName)} left"
            }

            coinsMy = data?.coins.toString().toDouble()
            dismissProgress()
        }

        db.collection("quiz").limit(20)
            .addSnapshotListener { snapshot, e ->
                allqestionlist = ArrayList<QuizModel>()
                allqestionlist.clear()
                val data = snapshot?.toObjects(QuizModel::class.java)
                allqestionlist.addAll(data!!)
                if (allqestionlist.size == 0) {
                    showToast("No Question")
                } else {
                    setQuestions()
                }
            }

        val optionTextViews = listOf(binding.optionA, binding.optionB, binding.optionC, binding.optionD)
        optionTextViews.forEachIndexed { index, option ->
            option.setOnClickListener {
                    selectedoption(option, index + 1)
                }
        }




        setListener()

// change mode before you release


        // change mode before you release
        val isTestMode = false
        UnityAds.initialize(applicationContext, PlayQuizActivity.UNITY_GAME_ID, isTestMode)

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


        binding.next.setOnClickListener {
            clickcount += 1
            if (clickcount == 1) {
                //first time clicked to do this
                showAd(
                    PlayQuizActivity.INTERSTITIAL_ID
                )

            }

            else if (clickcount == 5) {
                //first time clicked to do this
                showAd(
                    PlayQuizActivity.REWARDED_ID
                )



            }

            else if (clickcount == 9) {
                //first time clicked to do this
                showAd(
                    PlayQuizActivity.INTERSTITIAL_ID
                )




            }


            else if (clickcount == 13) {
                //first time clicked to do this
                showAd(
                    PlayQuizActivity.REWARDED_ID
                )




            }

            else if (clickcount == 17) {
                //first time clicked to do this
                showAd(
                    PlayQuizActivity.INTERSTITIAL_ID
                )




            }

            else if (clickcount == 21) {
                //first time clicked to do this
                showAd(
                    PlayQuizActivity.REWARDED_ID
                )




            }


            else if (clickcount == 25) {
                //first time clicked to do this
                showAd(
                    PlayQuizActivity.INTERSTITIAL_ID
                )




            }


            else if (clickcount == 29) {
                //first time clicked to do this
                showAd(
                    PlayQuizActivity.REWARDED_ID
                )




            }


            else if (clickcount == 33) {
                //first time clicked to do this
                showAd(
                    PlayQuizActivity.INTERSTITIAL_ID
                )



            }

            else if (clickcount == 37) {
                //first time clicked to do this
                showAd(
                    PlayQuizActivity.REWARDED_ID
                )



            }


            else if (clickcount == 41) {
                //first time clicked to do this
                showAd(
                    PlayQuizActivity.INTERSTITIAL_ID
                )



            }

            else if (clickcount == 45) {
                //first time clicked to do this
                showAd(
                    PlayQuizActivity.REWARDED_ID
                )



            }

            else if (clickcount == 49) {
                //first time clicked to do this
                showAd(
                    PlayQuizActivity.INTERSTITIAL_ID
                )



            }

            else if (clickcount == 53) {
                //first time clicked to do this
                showAd(
                    PlayQuizActivity.REWARDED_ID
                )



            }

            else {
                //check how many times clicked and so on
                gandu4()

            }
        }


//        findViewById<View>(R.id.next).setOnClickListener { v: View? ->
//            showAd(
//                PlayQuizActivity.INTERSTITIAL_ID
//            )
//
//
//        }

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
                Toast.makeText(this@PlayQuizActivity, "Ads didn't load, please wait and then again click or try this task after few seconds, or try using vpn if this issue occurs everytime ", Toast.LENGTH_LONG).show()
                clickcount = 0
//                binding.next.isClickable = false

//                val handler = Handler(Looper.getMainLooper())
//                handler.postDelayed({
//                    // do something after 1000ms
//                    binding.next.isClickable = true
//
//                }, 5000)




            }

            override fun onUnityAdsShowStart(s: String) {}
            override fun onUnityAdsShowClick(s: String) {}
            override fun onUnityAdsShowComplete(
                s: String,
                unityAdsShowCompletionState: UnityAds.UnityAdsShowCompletionState
            ) {







                gandu4()





                Toast.makeText(this@PlayQuizActivity, "Ads succesfully load, you can proceed now", Toast.LENGTH_LONG).show()
//                Toast.makeText(
//                    ClientProperties.getActivity(), "ads chal gaya succesfully!!",
//                    Toast.LENGTH_LONG
//                ).show()
            }
        })
    }


    @SuppressLint("SetTextI18n")
    private fun winngDialog(wrong: Int) {

        val dialog = Dialog(this@PlayQuizActivity)
        dialog.setCancelable(false)
        val dialogBinding = DialogResultBinding.inflate(LayoutInflater.from(this@PlayQuizActivity))
        dialog.setContentView(dialogBinding.root)

        dialogBinding.resultText.text =
            "You got ${allqestionlist.size - wrong} Questions correct and you got ${(allqestionlist.size - wrong) * coinsMy}"
        dialogBinding.dialogCoins.text = "${(allqestionlist.size - wrong) * coinsMy}"

        if (dialog.window != null) dialog.window!!.setBackgroundDrawable(ColorDrawable(0))

        loadBanner()


        // idhar se hutaya hai buttonokay ka code callback start

        dialogBinding.btnOkay.setOnClickListener {

            prefManager?.withLimitCheck(LimitOfEarnings.LimitOfQuiz) {
                val coins = (allqestionlist.size - wrong) * coinsMy
                val repo = MyRepo(this@PlayQuizActivity)
                prefManager?.decrementLimit(LimitOfEarnings.LimitOfQuiz)
                binding.limit.text = "Limit: ${prefManager?.getInt(LimitOfEarnings.LimitOfQuiz.prefName)} left"
                repo.addCoins(coins = coins)
            }

            dialog.dismiss()
            startActivity(Intent(this@PlayQuizActivity, MainActivity::class.java))
            finish()
        }

        // idhar se hutaya hai buttonokay ka code callback end

        dialog.show()
    }


    private fun setalloptionDisable() {
        binding.optionA.isClickable = false
        binding.optionB.isClickable = false
        binding.optionC.isClickable = false
        binding.optionD.isClickable = false
    }

    private fun setalloptionEnable() {
        binding.optionA.isClickable = true
        binding.optionB.isClickable = true
        binding.optionC.isClickable = true
        binding.optionD.isClickable = true
    }

    fun setColor(option: Int, color: Int) {
        when (option) {

            1 -> {
                binding.optionA.background = ContextCompat.getDrawable(this, color)
            }
            2 -> {
                binding.optionB.background = ContextCompat.getDrawable(this, color)
            }
            3 -> {
                binding.optionC.background = ContextCompat.getDrawable(this, color)
            }
            4 -> {
                binding.optionD.background = ContextCompat.getDrawable(this, color)
            }

        }
    }

    @SuppressLint("SetTextI18n")
    fun setQuestions() {

        canGoNextQuestion = false
        setalloptionEnable()
        setStyle()
        val currentQ = allqestionlist!![cureentpos - 1]
        binding.progressbar.progress = cureentpos
        binding.progressbar.max = allqestionlist!!.size

        if (cureentpos != allqestionlist!!.size) {
            binding.next.text = "Submit"
        }

        binding.counter.text = "$cureentpos" + "/" + "${allqestionlist.size}"
        binding.quizQuestion.text = currentQ.question
        binding.optionA.text = "A. " + currentQ.optionA
        binding.optionB.text = "B. " + currentQ.optionB
        binding.optionC.text = "C. " + currentQ.optionC
        binding.optionD.text = "D. " + currentQ.optionD
    }


    fun setStyle() {

        binding.optionA.setTextColor(Color.parseColor("#ffffff"))
        binding.optionA.background = ContextCompat.getDrawable(this, R.drawable.option_shape)

        binding.optionB.setTextColor(Color.parseColor("#ffffff"))
        binding.optionB.background = ContextCompat.getDrawable(this, R.drawable.option_shape)

        binding.optionC.setTextColor(Color.parseColor("#ffffff"))
        binding.optionC.background = ContextCompat.getDrawable(this, R.drawable.option_shape)

        binding.optionD.setTextColor(Color.parseColor("#ffffff"))
        binding.optionD.background = ContextCompat.getDrawable(this, R.drawable.option_shape)

    }

    fun selectedoption(selectedTextview: TextView, option: Int) {
        canGoNextQuestion = true

        setStyle()
        selectedTextview.background =
            ContextCompat.getDrawable(this, R.drawable.selected_option_shape)
        selectedoptionInt = option
    }

    fun loadBanner() {
        val view = BannerView(this@PlayQuizActivity, constants.banner, UnityBannerSize(320, 50))
        view.load()
        binding.bannerAd.addView(view)
    }
    companion object {

        val UNITY_GAME_ID = "4429147"
        private val INTERSTITIAL_ID = "Interstitial_Android"
        private val REWARDED_ID = "Rewarded_Android"
        private val BANNER_ID = "Banner_Android"
        private const val TAG = "PlayQuizActivity"
    }


    @SuppressLint("SetTextI18n")
    fun gandu4 () {


        if (canGoNextQuestion) {
            if (selectedoptionInt != 0) {
                val currentQ = allqestionlist[cureentpos - 1]
                if (selectedoptionInt != currentQ.ans.toInt()) {
                    wrong++
                    setColor(selectedoptionInt, R.drawable.wrong_option_shape)

                }
                setColor(currentQ.ans.toInt(), R.drawable.right_option_shape)

                if (cureentpos == allqestionlist.size) {
                    binding.next.text = "Finish"
                } else {
                    binding.next.text = "Next"
                    setalloptionDisable()
                }

            } else {
                cureentpos++
                when {
                    cureentpos <= allqestionlist.size -> {
                        setQuestions()
                    }
                    else -> {
                        Toast.makeText(
                            this@PlayQuizActivity,
                            "Question Over",
                            Toast.LENGTH_SHORT
                        ).show()
                        winngDialog(wrong)
                    }
                }
            }
        } else {
            Toast.makeText(this, "Please Select any Options", Toast.LENGTH_SHORT).show()
        }

        selectedoptionInt = 0

    }



}