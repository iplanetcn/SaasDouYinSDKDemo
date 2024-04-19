package com.bytedance.android.douyin.saas.series.pay


/**
 *
 */
class DemoPackagePrice : Cloneable {

    var unlockCount: Int = 0 //该套餐解锁集数

    var discount: Double = 0.0// 批量折扣

    var unitPrice: Long = 0L

    var packagePrice: Long = 0L

    var isSelected: Boolean = false

}