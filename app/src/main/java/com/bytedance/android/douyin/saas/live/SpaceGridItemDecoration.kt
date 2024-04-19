package com.bytedance.android.douyin.saas.live

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class SpaceGridItemDecoration(private val spanCount: Int, space: Int) : RecyclerView.ItemDecoration() {
    private val space: Int

    init {
        this.space = dpToPx(space).toInt()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position: Int = parent.getChildAdapterPosition(view) // 获取view 在adapter中的位置
        val column = position % spanCount // view 所在的列
        outRect.left = column * space / spanCount // column * (列间距 * (1f / 列数))
        outRect.right = space - (column + 1) * space / spanCount // 列间距 - (column + 1) * (列间距 * (1f /列数))
        outRect.top = space
    }

    private fun dpToPx(dp: Int): Float {
        return dp * Resources.getSystem().displayMetrics.density
    }
}
