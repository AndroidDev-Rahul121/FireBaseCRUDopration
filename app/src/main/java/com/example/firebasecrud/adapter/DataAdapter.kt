package com.example.firebasecrud.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasecrud.R
import com.example.firebasecrud.model.User



class DataAdapter(
    private val context: Context,
    private val arrayList: ArrayList<User>
) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.data_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = arrayList[position]
        holder.name.text = data.name
        holder.email.text = data.email
        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(data)
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.list_item_name)
        val email: TextView = itemView.findViewById(R.id.list_item_email)

    }

    interface OnItemClickListener {
        fun onClick(data:User)
    }
}
