package com.nineleaps.eazipoc.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nineleaps.eazipoc.R
import com.nineleaps.eazipoc.models.UserModel

class UserListAdapter(
    userList: ArrayList<UserModel>,
    numberList: ArrayList<String>,
    private val listener: CheckBoxClickListener
) :
    RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    private var userModelList = ArrayList<UserModel>()
    private var userNumberList = ArrayList<String>()
    private var selectList = ArrayList<String>()

    init {
        userModelList = userList
        userNumberList = numberList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_of_contact_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userModelList.size
    }

    override fun onBindViewHolder(holder: UserListAdapter.ViewHolder, position: Int) {
        holder.contactName.text = userModelList[position].name
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Log.d("CheckBoxChange", userModelList[position].toString())
                selectList.add(userNumberList[position])
                listener.onClick(selectList)
            } else {
                selectList.remove(userNumberList[position])
                listener.onClick(selectList)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contact_name)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_select)
    }

    interface CheckBoxClickListener {
        fun onClick(selectedUserList: ArrayList<String>)
    }
}

