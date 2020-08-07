package com.nineleaps.eazipoc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.models.MessageModel


/**
 * @param messageList List of messages
 * adapter for chat activity
 */
class MessageListAdapter(
    private val messageList: ArrayList<MessageModel>
) : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {

    /**
     * @param parent  ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * Inflates the appropriate layout according to the ViewType.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.nineleaps.eazipoc.R.layout.message_received, parent, false)
        return ViewHolder(view)
    }

    /**
     * Returns the total number of items in the adapter
     */
    override fun getItemCount(): Int {
        return messageList.size
    }

    /**
     * Return the position of the item
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     * Returns the viewType of item at position
     */
    override fun getItemViewType(position: Int): Int {
        return position
    }

    /**
     * @param holder ViewHolder which should be updated
     * @param position position of the item
     * Called by RecyclerView to bind the appropriate viewholder class to the data
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = messageList[position].name

        if (name == ApplicationClass.connection.user.toString().split("@")[0]) {
            holder.senderLayout.visibility = View.VISIBLE
            holder.tvSenderName.text = messageList[position].name
            holder.tvSenderMessage.text = messageList[position].body
        } else {
            holder.receiverLayout.visibility = View.VISIBLE
            holder.tvReceiverName.text = messageList[position].name
            holder.tvReceiverMessage.text = messageList[position].body
        }
    }

    /**
     * @param itemView View of each item in the recyclerview
     * Initializes UI elements present in the recycler layout
     */
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