package com.unwiringapps.earningnapp.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.PopupMenu
import android.widget.Toast
import com.bumptech.glide.Glide
import com.unwiringapps.earningnapp.Extra.dismissProgress
import com.unwiringapps.earningnapp.Extra.showToast
import com.unwiringapps.earningnapp.model.WithdrawModel
import com.unwiringapps.earningnapp.model.UserModel
import com.unwiringapps.earningnapp.R
import com.unwiringapps.earningnapp.databinding.ActivityWalletBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import com.unwiringapps.earningnapp.Extra.constants
import com.unwiringapps.earningnapp.model.InviteModel
import com.unwiringapps.earningnapp.repo.MyRepo
import java.text.SimpleDateFormat
import java.util.*

class WalletActivity : AppCompatActivity() {

    lateinit var binding: ActivityWalletBinding
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    var type = "Paytm"
    var isPaymentSelected = false
    var data: UserModel? = null
    var number = ""
    var myReferCode: String? = ""
    var inviteCoins = 1.0
    lateinit var repo: MyRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
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
//        }, 1000,40000)


            loadBanner()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        repo = MyRepo(this)


        referSystem()

        db.collection("user").document(auth.uid!!).addSnapshotListener { value, error ->
            val userValue = value?.toObject(UserModel::class.java)

            try {
                Glide.with(this).load(userValue?.imageURL).into(binding.profileImage)
                binding.profileCoins.text = userValue?.coins.toString()
                number = userValue?.number.toString()
                data = userValue
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }



        binding.ansDropDown.setOnClickListener {
            val popup = PopupMenu(this, binding.ansDropDown)
            popup.menuInflater.inflate(R.menu.answer_option, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener { item ->

                when (item.itemId) {
                    R.id.one -> {
                        isPaymentSelected = true
                        type = "Paytm"
                        binding.ansDropDown.text = type
                    }
                    R.id.two -> {
                        isPaymentSelected = true
                        type = "Google Pay"
                        binding.ansDropDown.text = type
                    }
                    R.id.three -> {
                        isPaymentSelected = true
                        type = "other payment methods but send me details on telegram(unwiringapps) or whatsapp(+91 6395962636) with withdraw screenshots"
                        binding.ansDropDown.text = type
                    }

                }
                true
            }
        }


        binding.btnWithdraw.setOnClickListener {
            binding.btnWithdraw.isClickable = false

            if (isPaymentSelected) {

                when {
                    TextUtils.isEmpty(binding.coinAmount.text) -> {
                        binding.coinAmount.text.clear()
                        Toast.makeText(this, "Please Enter Amount", Toast.LENGTH_SHORT).show()

                        Handler(Looper.getMainLooper()).postDelayed(Runnable { this.finish() }, 500)  // this is use to finish the activity so that withdraw button can work again
                        startActivity(Intent(this, WalletActivity::class.java))
                    }
                    binding.coinAmount.text.toString().toInt() < 1000 -> {
                        binding.coinAmount.text.clear()
                        Toast.makeText(
                            this,
                            "Minimum withdraw 1000 coins = 10 rupees",
                            Toast.LENGTH_LONG
                        ).show()
                        Handler(Looper.getMainLooper()).postDelayed(Runnable { this.finish() }, 500)  // this is use to finish the activity so that withdraw button can work again
                        startActivity(Intent(this, WalletActivity::class.java))
                    }
                    binding.coinAmount.text.toString().toInt() > data?.coins!! -> {
                        binding.coinAmount.text.clear()
                        Toast.makeText(this, "Enter Valid Amount", Toast.LENGTH_LONG).show()
                        Handler(Looper.getMainLooper()).postDelayed(Runnable { this.finish() }, 500)  // this is use to finish the activity so that withdraw button can work again
                        startActivity(Intent(this, WalletActivity::class.java))
                    }
                    else -> {
                        val coins = data!!.coins?.minus(binding.coinAmount.text.toString().toInt())
                        withdrawCoins(
                            data?.number!!,
                            coins!!.toInt(),
                            binding.coinAmount.text.toString().toInt()
                        )
                    }
                }


            } else {
                Toast.makeText(
                    this@WalletActivity,
                    "Please Select Payment Method ",
                    Toast.LENGTH_LONG
                ).show()
                Handler(Looper.getMainLooper()).postDelayed(Runnable { this.finish() }, 500)  // this is use to finish the activity so that withdraw button can work again
                startActivity(Intent(this, WalletActivity::class.java))
            }

        }
    }

    private fun withdrawCoins(number: String, coins: Int, withdrawCoins: Int) {

        db.collection("user").document(auth.currentUser!!.uid).update("coins", coins)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    dismissProgress()
                    Toast.makeText(
                        this,
                        "Withdraw Money Successfully for Payment you have to wait 72 hours, IF POSSIBLE PLEASE SEND SCREENSHOT ON TELEGRAM FOR FASTER WITHDRAW",
                        Toast.LENGTH_LONG
                    ).show()

                    storeDataInDB(withdrawCoins, number, type)

                    binding.coinAmount.text.clear()
                } else {
                    dismissProgress()
                    showToast(it.exception?.localizedMessage.toString())
                    binding.coinAmount.text.clear()
                }
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun storeDataInDB(withdrawCoins: Int, number: String, type: String) {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentDateTime: String = dateFormat.format(Date())
        val uid = db.collection("withdraw").document().id

        val money =
            WithdrawModel(
                name = data?.name!!,
                number = number,
                coins = withdrawCoins,
                authid = uid,
                timeStemp = currentDateTime,
                paymentMode = type,
                status = "Pending",
            )

        db.collection("withdraw").document(uid).set(money).addOnCompleteListener {
            if (it.isCanceled) {
                Toast.makeText(this, "" + it.exception?.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            } else {
                finish()
            }
        }
    }

    private fun referSystem() {
        db.collection("user").document(auth.uid!!).addSnapshotListener { value, error ->
            val data = value?.toObject(UserModel::class.java)
            myReferCode = data?.myReferCode

            db.collection("Earning").document("invite").addSnapshotListener { value, error ->

                val inviteData = value?.toObject(InviteModel::class.java)
                inviteCoins = inviteData?.coins.toString().toDouble()

                db.collection("refer")
                    .document(myReferCode!!)
                    .collection("users").get().addOnCompleteListener {
                        it.let {
                            for (im in it.result) {
                                if (im.get("status") == false) {
                                    db.collection("refer").document(myReferCode!!)
                                        .collection("users")
                                        .document(im.get("uid").toString()).update("status", true)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                repo.addCoins(inviteCoins)
                                            }
                                        }
                                }
                            }
                        }
                    }
            }
        }
    }

    companion object {
        private const val TAG = "WalletActivity"
    }

    fun loadBanner() {
        val view = BannerView(this, constants.banner, UnityBannerSize(320, 50))
        view.load()
        binding.bannerAd.addView(view)
    }




}