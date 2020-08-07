package com.nineleaps.eazipoc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nineleaps.eazipoc.R
import com.nineleaps.eazipoc.models.GroupDatabaseModel

/**
 * @param groupList List of groups
 * @param cellClickListener Listener to handle onCLick event of a particular group
 * adapter for groups activity
 */
class GroupListAdapter(
    groupList: ArrayList<GroupDatabaseModel>,
    private val cellClickListener: CellClickListener
) :
    RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {
    private var groupModelList = ArrayList<GroupDatabaseModel>()

    init {
        groupModelList = groupList
    }

    /**
     * @param parent  ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_of_group_item_layout, parent, false)
        return ViewHolder(view)
    }

    /**
     * Returns the total number of items in the adapter
     */
    override fun getItemCount(): Int {
        return groupModelList.size
    }

    /**
     * @param holder ViewHolder which should be updated
     * @param position position of the item
     * Called by RecyclerView to display the data at the specified position
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.groupName.text = groupModelList[position].groupName

        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(groupModelList[position])
        }
    }

    /**
     * @param itemView View of each item in the recyclerview
     * Initializes UI elements present in the recycler layout
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.group_name)
    }

    /**
     * Interface with function to handle click of recyclerview item
     */
    interface CellClickListener {
        fun onCellClickListener(groupData: GroupDatabaseModel)
    }
}

