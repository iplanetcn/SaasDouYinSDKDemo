package com.bytedance.android.douyin.saas.series

import android.view.View
import android.widget.TextView
import com.bytedance.android.douyin.saas.R
import com.bytedance.android.douyin.saas.series.pay.DemoPackagePrice

/**
 * 短剧支付tem
 */
class SeriesPayItemViewHolder(
    itemView: View,
    private val itemClickListener: SeriesPayContentView.OnItemClickListener
) : DSRecyclerItemViewHolder<DemoPackagePrice>(itemView) {
    private val itemContainer = findViewById<View>(R.id.series_pay_item_container)
    private val packagePrice = findViewById<TextView>(R.id.series_pay_episode_package_price)
    private val packageCount = findViewById<TextView>(R.id.series_pay_episode_count)

    override fun onBind(data: DemoPackagePrice, position: Int) {
        packageCount.text =
            String.format(itemView.context.getString(R.string.series_price_unlock_num), data.unlockCount)
        packagePrice.text =
            String.format(itemView.context.getString(R.string.series_price_package_price), data.packagePrice)

        itemContainer.background =
            if (data.isSelected) {
                itemView.context.getDrawable(R.drawable.aos_series_pay_item_select_bg)
            } else {
                itemView.context.getDrawable(R.drawable.aos_series_pay_item_unselected_bg)
            }

        itemContainer.setOnClickListener { itemClickListener.onItemClick(data, position) }
    }

    override fun onUnBind() {
    }

}
