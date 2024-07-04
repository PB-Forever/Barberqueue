package com.example.barberqueue.slidepromo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.barberqueue.R
import com.makeramen.roundedimageview.RoundedImageView

class BarberAdapter(private val imageIds: List<Int>) :
    RecyclerView.Adapter<BarberAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slide_barber_container, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val imageId = imageIds[position]
        holder.imageView.setImageResource(imageId)
    }

    override fun getItemCount(): Int {
        return imageIds.size
    }

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: RoundedImageView = itemView.findViewById(R.id.imageBarberSlide)
    }
}