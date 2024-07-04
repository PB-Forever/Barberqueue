package com.example.barberqueue.slidepromo

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.barberqueue.R

class PromoAdapter(private val sliderItems: List<PromoList>, private val viewPager2: ViewPager2) :
    RecyclerView.Adapter<PromoAdapter.SliderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slide_promo_container, parent, false)
        return SliderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.setImage(sliderItems[position])
    }

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView
        private val btnMore: Button
        init {
            image = itemView.findViewById(R.id.imagePromoSlide)
            btnMore = itemView.findViewById(R.id.btnMore)
            btnMore.setOnClickListener {
                val position =
                Toast.makeText(itemView.context, "More button clicked at position: $position", Toast.LENGTH_SHORT).show()
            }
        }
        fun setImage(sliderItem: PromoList){
            setBrightness(image, 0.7f)
            image.setImageResource(sliderItem.getImage())
        }
        fun setBrightness(imageView: ImageView, brightness: Float) {
            // Create a ColorMatrix and set its brightness
            val colorMatrix = ColorMatrix()
            colorMatrix.set(arrayOf(
                brightness, 0f, 0f, 0f, 0f,
                0f, brightness, 0f, 0f, 0f,
                0f, 0f, brightness, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            ).toFloatArray())

            // Create a ColorMatrixColorFilter
            val filter = ColorMatrixColorFilter(colorMatrix)
            imageView.colorFilter = filter
        }
    }
}