package com.unwiringapps.earningnapp.Extra

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.unwiringapps.earningnapp.Activity.LoginActivity
import com.unwiringapps.earningnapp.Activity.NoInternetActivity
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.MainActivity
import com.unwiringapps.earningnapp.R
import com.unwiringapps.earningnapp.repo.ConnectionLiveData

lateinit var dialog: Dialog
private const val TAG = "Extension"

fun Context.showToast(msg: String = "Message") {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

}


fun Fragment.showToast(msg: String = "Message") {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Context.showProgress(msg: String="Please Wait...") {

    dialog = Dialog(this)
    dialog.setContentView(R.layout.dialog_progress)
    if (dialog.window != null) {
        dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
    }
    val txt= dialog.findViewById<TextView>(R.id.txt)
    txt.text=msg.toString()
    dialog.setCancelable(false)
    dialog.show()

}

fun Fragment.showProgress() {

    dialog = Dialog(requireContext())
    dialog.setContentView(R.layout.dialog_progress)
    dialog.setCancelable(false)

    if (dialog.window != null) {
        dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
    }
    dialog.show()

}

fun Fragment.dismissProgress() {
    if(dialog.isShowing) {
        dialog.dismiss()
    }
}


fun Context.dismissProgress() {
    if(dialog.isShowing) {
        dialog.dismiss()
    }
}

fun Activity.setConnectionLiveDataObserver() {
    ConnectionLiveData(this).observe(this as LifecycleOwner) {
        Log.i(TAG, "setConnectionLiveDataObserver: is internetAvailable: $it; with ${this.localClassName}")
        if(it) {
            val manager = PrefManager(this)
            if (manager.getBool("login")) {
                if(this !is MainActivity) {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                    finish()
                }
            } else {
                if(this !is LoginActivity) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                    finish()
                }

            }
        }
        else {
            if(this !is NoInternetActivity) {
                startActivity(Intent(this, NoInternetActivity::class.java))
                this.finish()
            }
        }
    }
}

