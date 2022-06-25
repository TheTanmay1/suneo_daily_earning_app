package com.unwiringapps.earningnapp.repo

import android.app.Activity
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.unwiringapps.earningnapp.Extra.showToast

class AppUpdatesManager(val activity: Activity) {
    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity.applicationContext)

    fun isImmediateUpdateRequired(onChecked: (Boolean, AppUpdateInfo?) -> Unit) {

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val isAvailable = (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                /*&& appUpdateInfo.updatePriority() >= 4 */
                && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)
            )
            onChecked(isAvailable, appUpdateInfo)
        }
            .addOnFailureListener {
                Log.e(TAG, "isImmediateUpdateRequired: error while checking updates", it)
                onChecked(false, null)
            }


    }

    fun startImmediateUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val isAvailable = (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    /*&& appUpdateInfo.updatePriority() >= 4 */
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                    )
            if(isAvailable) {
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    IMMEDIATE,
                    // The current activity making the update request.
                    activity,
                    // Include a request code to later monitor this update request.
                    UPDATE_REQUEST_CODE)
            }
        }
            .addOnFailureListener {
                Log.e(TAG, "isImmediateUpdateRequired: error while checking updates", it)
                activity.showToast("Error while updating... Try again.")
            }
    }

    fun handleImmediateUpdate() {
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        IMMEDIATE,
                        activity,
                        UPDATE_REQUEST_CODE
                    )
                }
            }
    }

    companion object {
        private const val TAG = "AppUpdatesManager"
        const val UPDATE_REQUEST_CODE = 10001
    }
}