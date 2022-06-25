package com.unwiringapps.earningnapp.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.unwiringapps.earningnapp.Extra.dismissProgress
import com.unwiringapps.earningnapp.Extra.showProgress
import com.unwiringapps.earningnapp.Extra.showToast
import com.unwiringapps.earningnapp.model.UserModel
import com.unwiringapps.earningnapp.databinding.ActivityCheckNumberBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.Extra.LimitOfEarnings
import com.unwiringapps.earningnapp.MainActivity
import com.unwiringapps.earningnapp.model.InviteModel
import com.unwiringapps.earningnapp.model.UserLimits
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CheckNumberActivity : AppCompatActivity() {

    lateinit var binding: ActivityCheckNumberBinding
    var TAG = "@@@@"
    lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationIdMy = ""


    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    var referWinningCoins=0.0

//    private var countryCode:String?=null
//    private var countryName:String?=null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        binding.countryCodePicker.setOnCountryChangeListener(this)
//        binding.countryCodePicker.setDefaultCountryUsingNameCode("IN")
        db = FirebaseFirestore.getInstance()
        db.collection("Earning").document("invite")
            .addSnapshotListener { value, error ->
            val data=value?.toObject(InviteModel::class.java)
                referWinningCoins=data?.coins.toString().toDouble()
        }

        auth = FirebaseAuth.getInstance()

        val name = intent.getStringExtra("name")
        val referCode: String? = intent.getStringExtra("referCode")
        val image = intent.getStringExtra("image")
        val number = intent.getStringExtra("number")
        val myReferCode = intent.getStringExtra("myReferCode")
        val countryCode = intent.getStringExtra("countrykacode")


        sendVerificationToUser(number, countryCode)

        binding.btnVerifyOTP.setOnClickListener {
            showProgress("Checking...")
            val otp = binding.edtOTPNumber.text.toString()
            val credential = PhoneAuthProvider.getCredential(verificationIdMy, otp)
            signInWithPhoneAuthCredential(
                credential,
                name,
                referCode.toString(),
                image,
                number!!,
                myReferCode.toString()
            )
        }


    }


    private fun sendVerificationToUser(number: String?, countryCode: String?) {

        showProgress("OTP Sending...")

        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @SuppressLint("SetTextI18n")
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                binding.textView11.text = "OTP sent on +${countryCode} $number Successfully"
            }

            override fun onVerificationFailed(e: FirebaseException) {
                dismissProgress()
                showToast(e.localizedMessage!!)
                Log.e(TAG, "onVerificationFailed: ${e.localizedMessage}")
            }

            @SuppressLint("SetTextI18n")
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                showToast("OTP sent Successfully")
                dismissProgress()
                Log.e(TAG, "onCodeSent: ")
                verificationIdMy = verificationId
                binding.textView11.text = "OTP sent on + $countryCode $number"

            }
        }
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+${countryCode}" + number!!)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }


    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        name: String?,
        referCode: String,
        image: String?,
        number: String,
        myReferCode: String?
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    Log.e(TAG, "signInWithPhoneAuthCredential:my refer code$referCode")
                    db.collection("user").whereEqualTo("myReferCode", referCode.toString())
                        .addSnapshotListener { value, error ->
                            Log.e(TAG, "signInWithPhoneAuthCredential: $error")
                            if (error == null) {
                                val allUser = arrayListOf<UserModel>()
                                val data = value?.toObjects(UserModel::class.java)
                                allUser.addAll(data!!)

                                if (allUser.size == 0) {
                                    db.collection("user").document(auth.uid!!)
                                        .addSnapshotListener { value, error ->
                                            val data = value?.toObject(UserModel::class.java)
                                            Log.e(TAG, "signInWithPhoneAuthCredential: $data")
                                            if (data == null) {
                                                val manager =
                                                    PrefManager(this@CheckNumberActivity)
                                                val user = UserModel(
                                                    auth.uid.toString(),
                                                    name,
                                                    image,
                                                    number,
                                                    referCode,
                                                    0.0,
                                                    myReferCode,
                                                    limits = UserLimits()
                                                )

                                                db.collection("user")
                                                    .document(auth.uid.toString())
                                                    .set(user)
                                                    .addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            showToast("Account Created Successfully")
                                                            manager.setBool("login", true)
                                                            startActivity(
                                                                Intent(
                                                                    this,
                                                                    MainActivity::class.java
                                                                )
                                                            )

                                                        } else {
                                                            dismissProgress()
                                                            showToast(task.exception!!.toString())
                                                            Log.e(
                                                                TAG,
                                                                "signInWithPhoneAuthCredential: " + task.exception!!.toString()
                                                            )
                                                        }
                                                    }


                                            } else {
                                                dismissProgress()
                            showToast("This Mobile Number is Already Registered")
                                            }
                                        }

                                } else {

                                    db.collection("user").document(auth.uid!!)
                                        .addSnapshotListener { value, error ->
                                            val data = value?.toObject(UserModel::class.java)
                                            Log.e(TAG, "signInWithPhoneAuthCredential: $data")
                                            if (data == null) {
                                                val manager =
                                                    PrefManager(this@CheckNumberActivity)
                                                val user = UserModel(
                                                    auth.uid.toString(),
                                                    name,
                                                    image,
                                                    number,
                                                    referCode,
                                                    referWinningCoins,
                                                    myReferCode,
                                                    limits = UserLimits()
                                                )


                                                db.collection("user")
                                                    .document(auth.uid.toString())
                                                    .set(user)
                                                    .addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {

                                                            db.collection("user")
                                                                .document(allUser[0].uid.toString())
                                                                .update(
                                                                    "coins",
                                                                    FieldValue.increment(referWinningCoins)
                                                                ).addOnCompleteListener {
                                                                    if (it.isSuccessful) {
                                                                        showToast("Account Created Successfully")
                                                                        manager.setBool("login", true)
                                                                        startActivity(
                                                                            Intent(
                                                                                this,
                                                                                MainActivity::class.java
                                                                            )
                                                                        )
                                                                    } else
                                                                        Toast.makeText(
                                                                            this@CheckNumberActivity,
                                                                            "${it.exception?.localizedMessage}",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                }



                                                        } else {
                                                            dismissProgress()
                                                            showToast(task.exception!!.toString())
                                                            Log.e(
                                                                TAG,
                                                                "signInWithPhoneAuthCredential: " + task.exception!!.toString()
                                                            )
                                                        }
                                                    }


                                            } else {
                                                dismissProgress()
                            showToast("This Mobile Number is Already Registered")
                                            }
                                        }
                                }
                            }

                        }

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                    }
                }
            }


    }


}