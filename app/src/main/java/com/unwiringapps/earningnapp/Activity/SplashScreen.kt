package com.unwiringapps.earningnapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.MainActivity
import com.unwiringapps.earningnapp.R
import com.unwiringapps.earningnapp.repo.AppUpdatesManager

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val appUpdatesManager = AppUpdatesManager(this)
        appUpdatesManager.isImmediateUpdateRequired { isUpdateAvailable, _ ->
            if(isUpdateAvailable) {
                val i = Intent(this, AppUpdateActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                finish()
            }
            else {
                initApp()
            }
        }

    }

    private fun initApp() {
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        supportActionBar?.hide()

        val manager = PrefManager(this)


        if (manager.getBool("login")) {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                finish()
            }, 3000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                startActivity(Intent(this, LoginActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                finish()
            }, 3000)
        }
    }

    companion object {
        private const val TAG = "SplashScreen"
    }
}