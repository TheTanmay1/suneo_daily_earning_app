package com.unwiringapps.earningnapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.unwiringapps.earningnapp.Extra.setConnectionLiveDataObserver
import com.unwiringapps.earningnapp.R

class NoInternetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)
        setConnectionLiveDataObserver()

    }
}