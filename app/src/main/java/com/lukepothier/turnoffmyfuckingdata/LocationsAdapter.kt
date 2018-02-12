package com.lukepothier.turnoffmyfuckingdata

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class LocationsAdapter(private val list:ArrayList<Geofence>) : RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: LocationsAdapter.ViewHolder, position: Int) {
        holder.bindItems(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindItems(data : Geofence){
            val textView:TextView = itemView.findViewById(R.id.text_view_location_name)
            textView.text = data.name

            itemView.setOnClickListener({
                val intent = Intent(itemView.context, EditGeofenceActivity::class.java)
                intent.putExtra("geofenceId", data.id.toString())
                itemView.context.startActivity(intent)
            })
        }
    }
}
