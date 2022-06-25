package com.unwiringapps.earningnapp.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.unwiringapps.earningnapp.Extra.showToast
import com.unwiringapps.earningnapp.databinding.FragmentInviteBinding
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import com.unwiringapps.earningnapp.Extra.dismissProgress
import com.unwiringapps.earningnapp.Extra.showProgress
import com.unwiringapps.earningnapp.model.UserModel
import com.unwiringapps.earningnapp.repo.MyRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.util.Log
import com.unity3d.ads.UnityAds
import com.unwiringapps.earningnapp.BuildConfig
import com.unwiringapps.earningnapp.Extra.constants
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import java.lang.Exception


class InviteFragment : Fragment() {

    lateinit var binding: FragmentInviteBinding
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    var myReferCode: String? = ""
    lateinit var repo: MyRepo
    var TAG = "@@@@"


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInviteBinding.inflate(layoutInflater, container, false)
//        if (UnityAds.isReady (constants.interstitial)) {
//            Log.d(TAG, "showAd: ad ready and showing...")
//            UnityAds.show (activity, constants.interstitial)
//        }

        loadBanner()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        repo = MyRepo(requireActivity())
        showProgress()

        binding.txtLink.text =
            "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"

        binding.txtLink.setOnClickListener {
            showToast("Link Copied Successfully")
            val clipboard: ClipboardManager? =
                requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText(
                "label",
                "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
            )
            clipboard?.setPrimaryClip(clip)
        }


        db.collection("user").document(auth.uid!!).addSnapshotListener { value, error ->
            val data = value?.toObject(UserModel::class.java)
            myReferCode = data?.myReferCode
            dismissProgress()

        }

        binding.textRandomCode.setOnClickListener {

            showToast("Code Copied Successfully")
            val clipboard: ClipboardManager? =
                requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("label", myReferCode)
            clipboard?.setPrimaryClip(clip)
        }



        binding.btnInviteEarn.setOnClickListener {

            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                var shareMessage =
                    "\nLet me recommend you this application , this is an awesome app with which i earn daily\n\n "
//                    "\nLet me recommend you this application and get 136 Coins  instantly by using this refer code \t$myReferCode\t\n\n"
                shareMessage =
                    """
                    ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}""".trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                e.toString();
            }
        }

        return binding.root
    }

    fun loadBanner() {
        val view = BannerView(requireActivity(), constants.banner, UnityBannerSize(320, 50))
        view.load()
        binding.bannerAd.addView(view)
    }

}