package com.lukepothier.turnoffmyfuckingdata

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class LocationsAdapter(private val list:ArrayList<String>):RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: LocationsAdapter.ViewHolder, position: Int) {
        holder.bindItems(list[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindItems(data : String){
            val textView:TextView = itemView.findViewById(R.id.text_view_location_name)
            textView.text = data

            //set the onclick listener for the single list item
            itemView.setOnClickListener({
                Log.e("ItemClicked", data)
            })
        }
    }
}