package com.bytedance.android.douyin.saas.series.pay

/**
 * 打包价格
 */
class DemoPriceDetail : Cloneable {

    var seriesId: Long = 0L

    var unitPrice: Long = 0L // 单集购买单价 单位：分

    var segmentUnlock: Boolean = false // 支持部分批量购买解锁

    var packagePrice: ArrayList<DemoPackagePrice>? = null


    fun formatData() {
        if (packagePrice == null) {
            packagePrice = arrayListOf()
        }
        val assertSinglePrice = DemoPackagePrice().apply {
            unlockCount = 1
            discount = 1.0
            isSelected = true
        }
        packagePrice!!.add(0, assertSinglePrice)
        packagePrice?.forEach {
            it.unitPrice = unitPrice
            it.packagePrice = (unitPrice * it.discount * it.unlockCount).toLong()
        }
    }

    fun isPriceValid(): Boolean {
        return unitPrice > 0 && seriesId > 0 && !packagePrice.isNullOrEmpty()
    }
}