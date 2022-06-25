package com.unwiringapps.earningnapp.Adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unwiringapps.earningnapp.model.EarningCatModel
import com.unwiringapps.earningnapp.databinding.ItemEarningMainBinding
import android.graphics.drawable.GradientDrawable

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.unwiringapps.earningnapp.DB.PrefManager
import com.unwiringapps.earningnapp.earningOption.*
import com.unwiringapps.earningnapp.Extra.constants.interstitial
import com.unwiringapps.earningnapp.model.ExtraModel.DailyRewardModel
import com.unwiringapps.earningnapp.R
import com.unwiringapps.earningnapp.repo.MyRepo
import com.unwiringapps.earningnapp.databinding.DialogDailyRewardBinding
import com.github.hariprasanths.bounceview.BounceView
import com.google.firebase.firestore.FirebaseFirestore
import com.unity3d.ads.UnityAds
import com.unwiringapps.earningnapp.EarningOption.earnwithtasks1
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EarningMainAdapter(
    val requireActivity: FragmentActivity,
    val list: ArrayList<EarningCatModel>,

    ) : RecyclerView.Adapter<EarningMainAdapter.EarningViewHolder>() {
    class EarningViewHolder(val binding: ItemEarningMainBinding) :
        RecyclerView.ViewHolder(binding.root)

    val db = FirebaseFirestore.getInstance()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarningViewHolder {
        return EarningViewHolder(
            ItemEarningMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: EarningViewHolder, position: Int) {

        loadInterstitial()

        if (list[position].status == false) {

            holder.itemView.setOnClickListener {
                Toast.makeText(
                    requireActivity,
                    "This Task is not Available for Now",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            holder.binding.txtEarningCat.text = list[position].name
            holder.binding.txtEarningCat.setTextColor(Color.parseColor(list[position].textColor))
            Glide.with(requireActivity).load(list[position].image)
                .into(holder.binding.imgEarningCat)

            try {
                val gradientDrawable = GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    intArrayOf(
                        Color.parseColor(list[position].color2),
                        Color.parseColor(list[position].color1)


                    )
                )
                gradientDrawable.cornerRadius = 0f
                holder.binding.bacEarningGradient.setBackgroundDrawable(gradientDrawable)
            } catch (e: Exception) {
                e.stackTrace
            }

            BounceView.addAnimTo(holder.binding.root)

            holder.itemView.setOnClickListener {
//                if (UnityAds.isReady(interstitial)) {
//                    UnityAds.show(requireActivity, interstitial)
//                }

                val activityList = listOf(
                    earnwithtasks1::class.java,
                    SpinWheelActivity::class.java,
                    ScratchCardActivity::class.java,
                    SurveyActivity::class.java,
                    GuessNumberActivity::class.java,
                    LuckyBoxActivity::class.java,
                    WatchVideoActivity::class.java,
                    PlayQuizActivity::class.java,

                )

//                val fragmentlistbro = listOf(
//                    RankFragment(),
//                    ProfileFragment()
//
//                )


                if(position == 0) {
                    requireActivity.let {
                        val intent = Intent(it, activityList[position])
                        intent.putExtra("id", list[position].id)
                        it.startActivity(intent)
                    }
                }
                else {
                    requireActivity.let {
                        val intent = Intent(it, activityList[position])
                        intent.putExtra("id", list[position].id)
                        it.startActivity(intent)
                    }
                }


            }



        }






    }

    override fun getItemCount() = list.size

    @SuppressLint("SetTextI18n")
    private fun DailyRewardDialog(id: String?) {

        try {
            val dialog = Dialog(requireActivity)
            val binding = DialogDailyRewardBinding.inflate(LayoutInflater.from(requireActivity))
            dialog.setContentView(binding.root)

            if (dialog.window != null) {
                dialog.window?.setBackgroundDrawable(ColorDrawable(0))
            }

            var winningCoins: String? = "0"
            val manager = PrefManager(requireActivity)


            db.collection("Earning").document("reward")
                .addSnapshotListener { value, error ->
                    val data = value?.toObject(DailyRewardModel::class.java)
                    winningCoins = data?.coins

                    val date: String =
                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                    if (manager.getString("today")!! == "no") {
                        val repo = MyRepo(requireActivity)
                        repo.addCoins(winningCoins.toString().toDouble())
                        manager.setString("today", date)
                    } else {
                        if (date != manager.getString("today")) {
                            val repo = MyRepo(requireActivity)
                            repo.addCoins(winningCoins.toString().toDouble())
                            manager.setString("today", date)
                        } else {
                            binding.textView5.text = "Already claimed"
                            binding.textView6.text =
                                "You already complete today reward try Tomorrow"
                        }
                    }
                }

            dialog.window?.attributes?.windowAnimations = R.style.AlertDialogTheme
            binding.btnOkay.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    private fun loadInterstitial() {

        if (UnityAds.isInitialized()) {
            UnityAds.load(interstitial)
        } else {
            Handler(Looper.myLooper()!!).postDelayed(Runnable {
                UnityAds.load(interstitial)
            }, 5000)
        }
    }
}