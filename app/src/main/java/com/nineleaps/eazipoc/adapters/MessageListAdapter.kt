package com.nineleaps.eazipoc.adapters

import android.R.attr.button
import android.R.attr.layout_gravity
import android.util.LayoutDirection
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.models.MessageModel


class MessageListAdapter(
    private val mMessageList: ArrayList<MessageModel>
) : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    // Inflates the appropriate layout according to the ViewType.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.nineleaps.eazipoc.R.layout.message_received, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = mMessageList[position].name

        if (name == ApplicationClass.connection.user.toString().split("@")[0]) {
            holder.senderLayout.visibility = View.VISIBLE
            holder.tvSenderName.text = mMessageList[position].name
            holder.tvSenderMessage.text = mMessageList[position].body
        } else {
            holder.receiverLayout.visibility = View.VISIBLE
            holder.tvReceiverName.text = mMessageList[position].name
            holder.tvReceiverMessage.text = mMessageList[position].body
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSenderName: TextView =
            itemView.findViewById(com.nineleaps.eazipoc.R.id.tv_sender_name)
        val tvSenderMessage: TextView =
            itemView.findViewById(com.nineleaps.eazipoc.R.id.tv_sender_message)

        val tvReceiverName: TextView =
            itemView.findViewById(com.nineleaps.eazipoc.R.id.tv_receiver_name)
        val tvReceiverMessage: TextView =
            itemView.findViewById(com.nineleaps.eazipoc.R.id.tv_receiver_message)

        val senderLayout: LinearLayout =
            itemView.findViewById(com.nineleaps.eazipoc.R.id.ll_sender_layout)
        val receiverLayout: LinearLayout =
            itemView.findViewById(com.nineleaps.eazipoc.R.id.ll_receiver_layout)
    }
}