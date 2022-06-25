package com.unwiringapps.earningnapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unwiringapps.earningnapp.model.referEarn
import com.unwiringapps.earningnapp.model.UserModel
import com.unwiringapps.earningnapp.databinding.ItemReferUserBinding

class ReferUserAdapter(
    val requireContext: Context,
    val listOfRefer: ArrayList<UserModel>,
    listOfRefer1: ArrayList<referEarn>
) :
    RecyclerView.Adapter<ReferUserAdapter.ReferViewHolder>() {


    class ReferViewHolder(val binding: ItemReferUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReferViewHolder {
        return ReferViewHolder(
            ItemReferUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ReferViewHolder, position: Int) {
        Glide.with(requireContext).load(listOfRefer[position].uid).into(holder.binding.ProfileImage)
        holder.binding.referName.text = listOfRefer[position].name.toString()
    }

    override fun getItemCount() = listOfRefer.size

}