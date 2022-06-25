package com.unwiringapps.earningnapp.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.unwiringapps.earningnapp.Adapter.RankAdapter
import com.unwiringapps.earningnapp.Extra.constants
import com.unwiringapps.earningnapp.model.UserModel
import com.unwiringapps.earningnapp.databinding.FragmentRankBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

class RankFragment : Fragment() {

    lateinit var binding: FragmentRankBinding
    lateinit var db: FirebaseFirestore
    var allRankList = arrayListOf<UserModel>()
    lateinit var rankAdapter: RankAdapter
    private var adShown: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRankBinding.inflate(layoutInflater, container, false)
//        if (UnityAds.isReady (constants.interstitial)) {
//            Log.d(TAG, "showAd: ad ready and showing...")
//            UnityAds.show (requireActivity(), constants.interstitial)
//        }

        db = FirebaseFirestore.getInstance()
        loadBanner()



        db.collection("user").orderBy("rankerscoins", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error == null) {
                    val data = value?.toObjects(UserModel::class.java)
                    allRankList.addAll(data!!)
                    binding.rcvRank.layoutManager = LinearLayoutManager(activity)
                    rankAdapter = RankAdapter(allRankList)
                    binding.rcvRank.adapter = rankAdapter

                    if (allRankList.size > 3) {
                        try {
                            Glide.with(this).load(allRankList[1].imageURL)
                                .into(binding.twoProfileImage)
                            binding.twoCoins.text = allRankList[1].rankerscoins.toString()

                            Glide.with(this).load(allRankList[3].imageURL)
                                .into(binding.threeProfileImage)
                            binding.threeCoins.text = allRankList[3].rankerscoins.toString()

                            Glide.with(this).load(allRankList[0].imageURL)
                                .into(binding.oneProfileImage)
                            binding.oneCoins.text = allRankList[0].rankerscoins.toString()
                        } catch (e: Exception) {
                            e.stackTrace
                        }
                    } else {
                        binding.constraintLayout3.visibility = View.GONE
                    }

                }
            }


        return binding.root
    }

    fun loadBanner() {
        val view = BannerView(requireActivity(), constants.banner, UnityBannerSize(320, 50))
        view.load()
        binding.bannerAd.addView(view)
    }

    companion object {
        private const val TAG = "RankFragment"
    }

}