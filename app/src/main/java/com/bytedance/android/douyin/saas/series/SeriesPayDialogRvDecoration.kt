package com.bytedance.android.douyin.saas.series

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SeriesPayDialogRvDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val gridLayoutManager = parent.layoutManager as? GridLayoutManager ?: return
        val layoutParams = view.layoutParams as? GridLayoutManager.LayoutParams ?: return
        val spanCount = gridLayoutManager.spanCount
        if (1 == layoutParams.spanSize) {
            val column = layoutParams.spanIndex
            outRect.left = column * space / spanCount
            outRect.right = space - (column + 1) * space / spanCount
            outRect.top = space
        }
    }
}