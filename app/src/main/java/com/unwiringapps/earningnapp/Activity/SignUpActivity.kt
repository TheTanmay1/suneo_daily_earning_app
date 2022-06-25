package com.unwiringapps.earningnapp.Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.unwiringapps.earningnapp.Extra.dismissProgress
import com.unwiringapps.earningnapp.Extra.showProgress
import com.unwiringapps.earningnapp.Extra.showToast
import com.unwiringapps.earningnapp.databinding.ActivitySignUpBinding
import com.github.hariprasanths.bounceview.BounceView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hbb20.CountryCodePicker
import com.unwiringapps.earningnapp.Extra.setConnectionLiveDataObserver
import java.util.*

class SignUpActivity : AppCompatActivity() , CountryCodePicker.OnCountryChangeListener {

    lateinit var binding: ActivitySignUpBinding
    lateinit var db: FirebaseFirestore
    var isImageSelected = false
    private var imageUrl: Uri? = null
    private val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm@#$%^&*()!"
    lateinit var storage: FirebaseStorage
    var TAG = "@@@@"

    private var countryCode:String?=null
    private var countryName:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.countryCodePicker.setOnCountryChangeListener(this)
        binding.countryCodePicker.setDefaultCountryUsingNameCode("US")
        binding.countryCodePicker.resetToDefaultCountry()

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }

        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                binding.regImage.setImageURI(uri)
                isImageSelected = true
                imageUrl = uri
            }
        )

        binding.regImage.setOnClickListener {
            getAction.launch("image/*")
        }

        binding.btnSignUp.setOnClickListener {
            BounceView.addAnimTo(binding.btnSignUp)
            showProgress()
            val name = binding.edtName.text.toString()
            val number = binding.edtNumber.text.toString()
            val referCode = binding.edtReferCode.text.toString()


            if (TextUtils.isEmpty(number)
            ) {
                dismissProgress()
                showToast("Please Enter Valid Data")
            }
            else if (TextUtils.isEmpty(name)) {
                dismissProgress()
                showToast("Please Enter your correct Name")
            }

            else if (!isImageSelected) {
                dismissProgress()
                showToast("Please Select Profile Picture")
            }
            else {
                     Log.e(TAG,"kya hua 1")
                imageUrl?.let { it1 ->
                    storage.reference.child("profile").child(number).putFile(it1)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.e(TAG, "onCreate: Image done")
                                storage.reference.child("profile").child(number)
                                    .downloadUrl.addOnSuccessListener { ImagePathURL ->
                                        val imagePath = ImagePathURL.toString()
                                        dismissProgress()
                                        Log.e(TAG,"kya hua 2")
                                        startActivity(
                                            Intent(
                                                this@SignUpActivity,
                                                CheckNumberActivity::class.java
                                            ).also { data ->
                                                data.putExtra("image", imagePath)
                                                data.putExtra("name", name)
                                                data.putExtra("referCode", referCode)
                                                Log.d("tanbro", referCode)
                                                data.putExtra("number", number)
                                                data.putExtra("myReferCode", getRandomString(6))
                                                data.putExtra("countrykacode", countryCode)
                                            })
                                        finish()
                                        Log.e(TAG,"kya hua 3")
                                    }
                            } else {
                                Log.e(TAG,"kya hua 4")
                                dismissProgress()
                                showToast(it.exception!!.localizedMessage!!)
                                Log.e(TAG,"kya hua 5")
                            }
                        }
                }
            }

        }

    }



    override fun onCountrySelected() {
        countryCode= binding.countryCodePicker.selectedCountryCode
        countryName= binding.countryCodePicker.selectedCountryName

        Toast.makeText(this,"Country Code : +${ countryCode } and Country Name: ${countryName}",
            Toast.LENGTH_SHORT).show()
        Log.e(TAG,"kya hua 6")
    }

    private fun getRandomString(sizeOfRandomString: Int): String {
        val random = Random()
        val sb = StringBuilder(sizeOfRandomString)
        for (i in 0 until sizeOfRandomString)
            sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
        return sb.toString()
    }
}