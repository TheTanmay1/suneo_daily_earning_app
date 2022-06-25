package com.unwiringapps.earningnapp.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.unwiringapps.earningnapp.Adapter.EarningMainAdapter
import com.unwiringapps.earningnapp.Extra.constants
import com.unwiringapps.earningnapp.Extra.dismissProgress
import com.unwiringapps.earningnapp.Extra.showProgress
import com.unwiringapps.earningnapp.model.EarningCatModel
import com.unwiringapps.earningnapp.databinding.FragmentHomeBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import com.unwiringapps.earningnapp.model.Message
import com.unwiringapps.earningnapp.R
import android.text.method.ScrollingMovementMethod
import com.unity3d.ads.UnityAds
import com.unwiringapps.earningnapp.Extra.constants.interstitial


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var navController: NavController

//    companion object {
//        private const val UNITY_GAME_ID = "4429147"
//        private const val INTERSTITIAL_ID = "Interstitial_Android"
//        private const val BANNER_ID = "Banner_Android"
//        private const val REWARDED_ID = "Rewarded_Android"
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        loadBanner()
        showProgress()



        db.collection("Earning").orderBy("position").addSnapshotListener { value, error ->

            val allCatList = arrayListOf<EarningCatModel>()
            val data = value?.toObjects(EarningCatModel::class.java)
            allCatList.addAll(data!!)

            binding.allEarningOptions.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.allEarningOptions.adapter = EarningMainAdapter(requireActivity(), allCatList)
            dismissProgress()

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        initMessageView()
    }

    //1st yo 15 jan

    private fun initMessageView() {
        binding.seeAllBtn.setOnClickListener {

            UnityAds.show (activity, interstitial)
            navController.navigate(R.id.action_homeFragment_to_allMessagesFragment)
        }

        val collRef = db.collection("messages")
        val query = collRef.orderBy("time", Query.Direction.DESCENDING)
        query.addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if(value == null || value.isEmpty) {
                return@addSnapshotListener
            }

            val message = value.documents[0].toObject<Message>()
            if(Message.isValidMessage(message)) {
                binding.messageTitle.text = message!!.title
                binding.messageTitle.visibility = if(message.title == null) {
                    View.GONE
                }
                else {
                    View.VISIBLE
                }
                binding.messageContent.text = message.content
                binding.messageContent.movementMethod = ScrollingMovementMethod()
                binding.timeTv.text = message.time!!.toDate().toString()
            }
        }
    }


    fun loadBanner() {
        val view = BannerView(requireActivity(), constants.banner, UnityBannerSize(320, 50))
        view.load()
        binding.bannerAd.addView(view)
    }

    companion object {
        private const val TAG = "HomeFragment"
    }

}