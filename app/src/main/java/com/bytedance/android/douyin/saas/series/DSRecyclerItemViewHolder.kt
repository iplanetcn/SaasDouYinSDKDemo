package com.bytedance.android.douyin.saas.series

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

abstract class DSRecyclerItemViewHolder<Model>(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {
    companion object {
        private const val TAG = "AosRecyclerItemViewHolder"
    }

    protected var data: Model? = null
    private var itemPosition: Int = -1

    private var isBind = false

    abstract fun onBind(data: Model, position: Int)
    abstract fun onUnBind()

    fun bind(data: Model, position: Int) {
        if (isBind) {
            val errorMsg = "${javaClass.simpleName} already bind!"
        }
        isBind = true
        this.itemPosition = position
        this.data = data
        onBind(data, position)
    }

    fun unBind() {
        if (!isBind) {
            val errorMsg = "${javaClass.simpleName} must bind before unbind!"
        }
        isBind = false
        onUnBind()
        this.data = null
        this.itemPosition = -1
    }

    protected fun <T : View> findViewById(@IdRes id: Int): T {
        return itemView.findViewById(id)
    }

    fun getHolderData(): Model? {
        return data
    }

    fun getHolderPosition(): Int {
        return itemPosition
    }
}