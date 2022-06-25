package com.unwiringapps.earningnapp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.unwiringapps.earningnapp.model.Message
import com.unwiringapps.earningnapp.databinding.MessageLayoutBinding

class MessageListAdapter(
    context: Context,
    val fragment: Fragment,
    options: FirestoreRecyclerOptions<Message>,
    val loadingComplete: (itemCount: Int) -> Unit,
): FirestoreRecyclerAdapter<Message, MessageListAdapter.ListViewHolder>(options) {
    var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = MessageLayoutBinding.inflate(inflater, parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int, model: Message) {
        holder.setBookDetails(model)
    }

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
        Log.e(TAG, "onError: error while fetching inventory data : ", e)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        loadingComplete(itemCount)
    }

    class ListViewHolder(val binding: MessageLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setBookDetails(message: Message) {
            binding.messageTitle.text = message.title
            binding.messageTitle.visibility = if(message.title == null) {
                View.GONE
            }
            else {
                View.VISIBLE
            }
            binding.messageContent.text = message.content
            binding.timeTv.text = message.time!!.toDate().toString()
        }
    }

    companion object {
        private const val TAG = "InventoryListAdapter"
    }
}