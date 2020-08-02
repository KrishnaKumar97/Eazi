package com.nineleaps.eazipoc.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nineleaps.eazipoc.models.MessageList


class MessageListAdapter(
    private val mContext: Context,
    private val mMessageList: ArrayList<MessageList>
) : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    // Inflates the appropriate layout according to the ViewType.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.nineleaps.eazipoc.R.layout.message_received, parent, false)
        return MessageListAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.messageSender.text = mMessageList[position].name
        holder.message.text = mMessageList[position].body
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageSender: TextView =
            itemView.findViewById(com.nineleaps.eazipoc.R.id.text_message_name)
        val message: TextView = itemView.findViewById(com.nineleaps.eazipoc.R.id.text_message_name)
    }
}