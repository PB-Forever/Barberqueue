package com.example.barberqueue

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(private val spaceWidth: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val pos = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (pos == 0) outRect.left = spaceWidth
        else if(pos == itemCount-1) outRect.right = spaceWidth
    }
}