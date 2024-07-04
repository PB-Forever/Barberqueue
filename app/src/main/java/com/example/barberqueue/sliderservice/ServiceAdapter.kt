package com.example.barberqueue.sliderservice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.barberqueue.R

class ServiceAdapter (private val serviceTv: List<String>) :
    RecyclerView.Adapter<ServiceAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slide_service_container, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val text = serviceTv[position]
        holder.textView.text = text
    }

    override fun getItemCount(): Int {
        return serviceTv.size
    }

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.serviceTv)
    }
}