package com.unwiringapps.earningnapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unwiringapps.earningnapp.model.UserModel
import com.unwiringapps.earningnapp.databinding.ItemRankBinding

class RankAdapter(val allRankList: ArrayList<UserModel>) :
    RecyclerView.Adapter<RankAdapter.RankHolder>() {


    class RankHolder(val binding: ItemRankBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankHolder {
        return RankHolder(
            ItemRankBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RankHolder, position: Int) {


        holder.binding.rankCoins.text = allRankList[position].rankerscoins.toString()
        holder.binding.rankName.text = allRankList[position].name.toString()
        Glide.with(holder.binding.root).load(allRankList[position].imageURL)
            .into(holder.binding.rankImage)
        holder.binding.rankNumber.text = position.plus(1).toString()

    }

    override fun getItemCount() = allRankList.size
}