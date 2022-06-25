package com.unwiringapps.earningnapp.Fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.unwiringapps.earningnapp.Activity.WalletActivity
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.Extra.constants
import com.unwiringapps.earningnapp.Extra.dismissProgress
import com.unwiringapps.earningnapp.Extra.showProgress
import com.unwiringapps.earningnapp.model.UserModel
import com.unwiringapps.earningnapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import com.unwiringapps.earningnapp.Extra.constants.interstitial
import com.unwiringapps.earningnapp.repo.MyRepo
import java.lang.Exception


class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    var myReferCode: String? = ""
    var inviteCoins = 100.0
    lateinit var repo: MyRepo
    private var adShown: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

//        if (UnityAds.isReady (interstitial)) {
//            Log.d(TAG, "showAd: ad ready and showing...")
//            UnityAds.show (activity, interstitial)
//        }

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        repo = MyRepo(requireActivity())


        loadBanner()

        binding.btnLogOut.setOnClickListener {

            val dialog = AlertDialog.Builder(activity)
            dialog.setMessage("Are you sure want to log out?")
            dialog.setPositiveButton("yes", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                auth.signOut()
                val manager = PrefManager(requireContext())
                manager.setBool("login", false)
                activity?.finishAffinity()
            })
            dialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            dialog.show()

        }

        showProgress()

        db.collection("user").document(auth.uid!!)
            .addSnapshotListener { value, error ->

                dismissProgress()

                val data = value?.toObject(UserModel::class.java)
                binding.txtName.text = data?.name

                binding.txtNumber.text = data?.number
                binding.txtReferCode.text = data?.referCode
                binding.txtCoins.text = data?.coins.toString()
                try {
                    Glide.with(requireContext()).load(data?.imageURL).into(binding.profileImage)
                } catch (e: Exception) {
                    e.stackTrace
                }

            }


        binding.linearLayout.setOnClickListener {
            startActivity(Intent(requireContext(), WalletActivity::class.java))
        }

        return binding.root
    }

    fun loadBanner() {
        val view = BannerView(requireActivity(), constants.banner, UnityBannerSize(320, 50))
        view.load()
        binding.bannerAd.addView(view)
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}