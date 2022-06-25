package com.unwiringapps.earningnapp.Fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import com.unwiringapps.earningnapp.Adapter.MessageListAdapter
import com.unwiringapps.earningnapp.Extra.constants
import com.unwiringapps.earningnapp.model.Message
import com.unwiringapps.earningnapp.databinding.FragmentAllMessagesBinding

class AllMessagesFragment : Fragment() {
    private var _binding: FragmentAllMessagesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAllMessagesBinding.inflate(inflater, container, false)

//        if (UnityAds.isReady (constants.interstitial)) {
//            Log.d(TAG, "showAd: ad ready and showing...")
//            UnityAds.show (activity, constants.interstitial)
//        }

        loadBanner()

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListAdapter()
    }

    private fun setListAdapter() {
        val collRef = Firebase.firestore.collection("messages")
        val query = collRef.orderBy("time", Query.Direction.DESCENDING)

        val options: FirestoreRecyclerOptions<Message> = FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(query, Message::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        val adapter = MessageListAdapter(
            context = requireContext(),
            fragment = this,
            options = options,
            loadingComplete = {
                if(binding.progress.visibility == View.VISIBLE) {
                    binding.progress.visibility = View.GONE
                }

                if(it > 0) {
                    if(binding.messagesListContainer.visibility == View.GONE || binding.messagesListContainer.visibility == View.INVISIBLE) {
                        binding.messagesListContainer.visibility = View.VISIBLE
                    }

                    if(binding.placeholder.visibility == View.VISIBLE) {
                        binding.placeholder.visibility = View.GONE
                    }
                }
                else {
                    if(binding.messagesListContainer.visibility == View.VISIBLE) {
                        binding.messagesListContainer.visibility = View.GONE
                    }

                    if(binding.placeholder.visibility == View.GONE || binding.placeholder.visibility == View.INVISIBLE) {
                        binding.placeholder.visibility = View.VISIBLE
                    }

                }


            },
        )






        val linearLayoutManager = LinearLayoutManager(context)
        binding.messagesListContainer.layoutManager = linearLayoutManager
        binding.messagesListContainer.adapter = adapter


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance() = AllMessagesFragment()
    }

    fun loadBanner() {
        val view = BannerView(requireActivity(), constants.banner, UnityBannerSize(320, 50))
        view.load()
        binding.bannerAd.addView(view)
    }

}