package com.unwiringapps.earningnapp

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.unwiringapps.earningnapp.DB.PrefManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.unity3d.ads.UnityAds
import com.unwiringapps.earningnapp.Activity.WalletActivity
import com.unwiringapps.earningnapp.Activity.AboutUs
import com.unwiringapps.earningnapp.Activity.privacy_policyyy
import com.unwiringapps.earningnapp.Extra.constants.interstitial
import com.unwiringapps.earningnapp.Extra.setConnectionLiveDataObserver
import com.unwiringapps.earningnapp.Fragment.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle
    lateinit var drawerlayoutbro : DrawerLayout
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setConnectionLiveDataObserver()


        val activity = this

//        Timer().scheduleAtFixedRate(object : TimerTask() {
//            override fun run() {
//                Log.d(TAG, "run: running ad in MainActivity")
//                if (UnityAds.isReady (interstitial)) {
//                    Log.d(TAG, "showAd: ad ready and showing...")
//                    UnityAds.show (activity, interstitial)
//                }
//            }
//        }, 1000, 300000)

        drawerlayoutbro = findViewById(R.id.drawkarbhai)
        val navviewbro : NavigationView = findViewById(R.id.navibhaiya)

        toggle = ActionBarDrawerToggle(this,drawerlayoutbro,R.string.open, R.string.close)
        drawerlayoutbro.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navviewbro.setNavigationItemSelectedListener {
            it.isChecked = true
            when(it.itemId) {
                R.id.home_yaarr -> {

                    UnityAds.show (activity, interstitial)

                    replaceFragment(HomeFragment()) }

                R.id.messages_from_me ->  {

                    UnityAds.show (activity, interstitial)

                    replaceFragment(AllMessagesFragment())
                }


                R.id.leaders_here -> {

                    UnityAds.show (activity, interstitial)

                    replaceFragment(RankFragment())
                }


                R.id.profile_yaar -> {

                    UnityAds.show (activity, interstitial)

                    replaceFragment(ProfileFragment())
                }


                R.id.withdraw_yaar ->  {

                    UnityAds.show (activity, interstitial)

                    startActivity(Intent(this, WalletActivity::class.java))
                }


                R.id.telegram_yaar -> telegrambro()

                R.id.invite_yaar ->{

                    UnityAds.show (activity, interstitial)

                 replaceFragment(InviteFragment()) }

                R.id.rate_yaar -> playstorebabu()

                R.id.about_yaar -> {

                    UnityAds.show (activity, interstitial)

                    startActivity(Intent(this, AboutUs::class.java))
                }


                R.id.logout_yaar -> logoutkaro()


                R.id.policy_yaar -> {

                    UnityAds.show (activity, interstitial)

                    startActivity(Intent(this, privacy_policyyy::class.java))

                }
            }
            true
        }

        val manager = PrefManager(this)
        manager.setBool("login", true)
        auth = FirebaseAuth.getInstance()


    }

    private fun replaceFragment (fragment: Fragment) {
        val frameLayout=findViewById<FrameLayout>(R.id.frame_bhaiyaa)
        frameLayout.removeAllViews()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
         fragmentTransaction.replace( R.id.frame_bhaiyaa , fragment).addToBackStack(fragment.tag)
        fragmentTransaction.commit()
        drawerlayoutbro.closeDrawers()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {

            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun logoutkaro () {

        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("Are you sure want to log out?")
        dialog.setPositiveButton("yes", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            auth.signOut()
            val manager = PrefManager(this)
            manager.setBool("login", false)
            this.finishAffinity()
        })
        dialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        dialog.show()

    }


    private fun telegrambro () {

        val telegram = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/unwiringapps"))
        telegram.setPackage("org.telegram.messenger")
        startActivity(telegram)

    }


    private fun playstorebabu () {

        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}


