package com.unwiringapps.earningnapp.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.Extra.dismissProgress
import com.unwiringapps.earningnapp.Extra.showProgress
import com.unwiringapps.earningnapp.Extra.showToast
import com.unwiringapps.earningnapp.MainActivity
import com.unwiringapps.earningnapp.model.UserModel

import com.unwiringapps.earningnapp.databinding.ActivityLoginBinding
import com.github.hariprasanths.bounceview.BounceView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.hbb20.CountryCodePicker
import com.unwiringapps.earningnapp.Extra.LimitOfEarnings
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() , CountryCodePicker.OnCountryChangeListener {


    lateinit var binding: ActivityLoginBinding
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    var TAG = "@@@@"
    lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationIdMy = ""
    var otpSent = false
    private var countryCode:String?=null
    private var countryName:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.countryCodePicker.setOnCountryChangeListener(this)
        binding.countryCodePicker.setDefaultCountryUsingNameCode("US")
        binding.countryCodePicker.resetToDefaultCountry()


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        binding.btnBack.setOnClickListener {
            finishAffinity()
        }

        binding.sendOTP.setOnClickListener {
            otpSent = true

            val number = binding.edtNumber.text.toString()

//            countryCode= binding.countryCodePicker.selectedCountryCode
//            countryName=binding.countryCodePicker.selectedCountryName
//           Log.d("khalu", "Country Code : ${ countryCode } and Country Name: ${countryName}")

            if (number.isEmpty()) {
                showToast("Enter Valid Number")
            } else if (number.length != 10) {
                showToast("Enter Valid Number")
            } else {
                sendVerificationToUser(number)
            }

        }




        binding.btnLogin.setOnClickListener {
            if (binding.edtNumber.text.toString().isEmpty()) {
                showToast("Enter Valid Mobile Number")
            } else if (!otpSent) {
                showToast("Please sent the OTP")
//                Log.d("bhai", countryCode!!)
            } else if (binding.edtOTP.text.toString().isEmpty()) {
                showToast("Enter OTP")
            } else {
                showProgress("Checking...")
                BounceView.addAnimTo(binding.btnLogin)
                val otp = binding.edtOTP.text.toString()
                val credential = PhoneAuthProvider.getCredential(verificationIdMy, otp)
                signInWithPhoneAuthCredential(credential)
            }

        }

        binding.txtSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }


    override fun onCountrySelected() {
        countryCode= binding.countryCodePicker.selectedCountryCode
        countryName=binding.countryCodePicker.selectedCountryName

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    db.collection("user").document(auth.uid!!).addSnapshotListener { value, error ->
                        val data = value?.toObject(UserModel::class.java)
                        loadLimitsFromRemote(data)
                        if (data != null) {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                            val manager = PrefManager(this@LoginActivity)
                            manager.setBool("login", true)
                        } else {
                            dismissProgress()
                            showToast("Please Create Account by Sign Up")
                        }
                    }

                } else {
                    dismissProgress()
                    showToast(task.exception!!.localizedMessage!!)
                }
            }
    }

    private fun sendVerificationToUser(number: String?) {

        showProgress("OTP sending...")

        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

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
                binding.textView11.visibility = View.VISIBLE
                binding.textView11.text = "OTP sent on +$countryCode-$number Successfully"
                binding.edtOTP.visibility = View.VISIBLE

                Log.e(TAG, "onCodeSent: ")
                dismissProgress()
                verificationIdMy = verificationId

            }
        }
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+$countryCode" + number!!)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    fun loadLimitsFromRemote(userModel: UserModel?) {
        Log.d(TAG, "loadLimitsFromRemote: called")
        val date: String =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val user = Firebase.auth.currentUser
        if(user != null) {
            val manager = PrefManager(this)
            Log.d(TAG, "loadLimitsFromRemote: userModel is: $userModel")
            if(userModel!= null) {
                Log.d(TAG, "loadLimitsFromRemote: user model not null")
                if(userModel.limits.date != date) {
                    manager.setString("date", date)
                    //reset
                    Log.e(TAG, "resetValues: ")
                    manager.setInt("limitOfWheel", 102)
                    manager.setInt("limitOfCard", 102)
                    manager.setInt("limitOfBox", 102)
                    manager.setInt("limitOfVideo", 102)
                    manager.resetAllLimits()
                    return
                }
                manager.setString("date", userModel.limits.date)
                manager.setInt("limitOfWheel", userModel.limits.limitOfWheel)
                manager.setInt("limitOfCard", userModel.limits.limitOfCard)
                manager.setInt("limitOfBox", userModel.limits.limitOfBox)
                manager.setInt("limitOfVideo", userModel.limits.limitOfVideo)
                manager.setInt(LimitOfEarnings.LimitOfSurvey.prefName, userModel.limits.limitOfSurvey)
                manager.setInt(LimitOfEarnings.LimitOfGuessNumber.prefName, userModel.limits.limitOfGuessNumber)
                manager.setInt(LimitOfEarnings.LimitOfQuiz.prefName, userModel.limits.limitOfQuiz)
            }
        }
    }


}