package com.nineleaps.eazipoc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nineleaps.eazipoc.R
import com.nineleaps.eazipoc.models.GroupModel

class GroupListAdapter(
    groupList: ArrayList<GroupModel>,
    private val cellClickListener: CellClickListener
) :
    RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {
    private var groupModelList = ArrayList<GroupModel>()

    init {
        groupModelList = groupList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_of_group_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return groupModelList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.groupName.text = groupModelList[position].groupName
        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(groupModelList[position])
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.group_name)
    }

    interface CellClickListener {
        fun onCellClickListener(groupData: GroupModel)
    }
}

