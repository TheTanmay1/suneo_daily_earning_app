package com.unwiringapps.earningnapp.Activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.unwiringapps.earningnapp.Extra.showToast
import com.unwiringapps.earningnapp.R
import com.unwiringapps.earningnapp.databinding.ActivityAppUpdateBinding
import com.unwiringapps.earningnapp.repo.AppUpdatesManager

class AppUpdateActivity : AppCompatActivity() {
    private var _binding: ActivityAppUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAppUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appUpdateManager = AppUpdatesManager(this)
        binding.startUpdateBtn.setOnClickListener {
            appUpdateManager.startImmediateUpdate()
        }

        binding.btn123.setOnClickListener{

            playstorebabu()

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppUpdatesManager.UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
                // If the update is cancelled or fails,
                // you can request to start the update again.
                val toastText = if(resultCode == RESULT_CANCELED) {
                    "You cancelled the update."
                }
                else {
                    "Some error came. Try Again."
                }
                showToast(toastText)

            }
        }
    }

    override fun onResume() {
        super.onResume()
        val appUpdatesManager = AppUpdatesManager(this)
        appUpdatesManager.handleImmediateUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "AppUpdateActivity"
    }

    private fun playstorebabu () {

        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

}