package com.unwiringapps.earningnapp.repo

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.unwiringapps.earningnapp.model.UserLimits


class MyRepo(val activity: Activity) {

    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun addCoins(coins: Double, showToast: Boolean = true) {
        Log.d(TAG, "addCoins: addCoins called with coins $coins")

        val updateMap = mapOf(

            "coins" to FieldValue.increment(coins),
            "rankerscoins" to FieldValue.increment(coins),
            "limits" to UserLimits.getFromPref(activity.applicationContext).toMap()
        )
        db.collection("user").document(auth.currentUser!!.uid).update(updateMap)
            .addOnCompleteListener {
                Log.d(TAG, "addCoins: user collection coins add callback called... with coins $coins")
                if (it.isSuccessful) {
                    if (showToast) Toast.makeText(
                        activity,
                        "$coins Coins Added Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (showToast) Toast.makeText(
                        activity,
                        it.exception?.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun removeCoins(coins: Double) {
        val updateMap = mapOf(
            "coins" to FieldValue.increment(-coins),
            "rankerscoins" to FieldValue.increment( 0),
            "limits" to UserLimits.getFromPref(activity.applicationContext).toMap()
        )
        db.collection("user").document(auth.uid!!).update(updateMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(activity, "You loss $coins Coins ", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(activity, it.exception?.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    companion object {
        private const val TAG = "MyRepo"
    }
}