package com.nineleaps.eazipoc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nineleaps.eazipoc.R
import com.nineleaps.eazipoc.models.UserModel

/**
 * @param userList List of UserModel
 * @param numberList List of user numbers
 * @param listener Listener to handle onCLick event of a particular checkbox
 * adapter for ListOfContacts activity
 */
class UserListAdapter(
    userList: ArrayList<UserModel>,
    numberList: ArrayList<String>,
    private val listener: CheckBoxClickListener
) :
    RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    private var userModelList = ArrayList<UserModel>()
    private var userNumberList = ArrayList<String>()
    private var selectedNumberList = ArrayList<String>()

    init {
        userModelList = userList
        userNumberList = numberList
    }

    /**
     * @param parent  ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_of_contact_item_layout, parent, false)
        return ViewHolder(view)
    }

    /**
     * Returns the total number of items in the adapter
     */
    override fun getItemCount(): Int {
        return userModelList.size
    }

    /**
     * @param holder ViewHolder which should be updated
     * @param position position of the item
     * Called by RecyclerView to display the data at the specified position.
     * Keeps track of selected checkboxes and passes the selected list to the activity
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.contactName.text = userModelList[position].name
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectedNumberList.add(userNumberList[position])
                listener.onClick(selectedNumberList)
            } else {
                selectedNumberList.remove(userNumberList[position])
                listener.onClick(selectedNumberList)
            }
        }
    }

    /**
     * @param itemView View of each item in the recyclerview
     * Initializes UI elements present in the recycler layout
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contact_name)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_select)
    }

    /**
     * Interface with function to handle click of checkbox present in recyclerview item
     */
    interface CheckBoxClickListener {
        fun onClick(selectedUserList: ArrayList<String>)
    }
}

