package com.bytedance.android.douyin.saas.series.ad.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.bytedance.android.douyin.saas.R
import kotlin.random.Random

class FakeSeriesDrawAdView : LinearLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, -1)

    constructor(context: Context, attributeSet: AttributeSet?, defaultStyle: Int) : super(
        context,
        attributeSet,
        defaultStyle
    )

    private val listeners = mutableListOf<AdEventListener>()

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_fake_series_ad_view, this, true)
        findViewById<View>(R.id.fake_series_ad_content).setBackgroundColor(Color.CYAN)
        setOnClickListener {
            listeners.forEach {
                it.onClick()
            }
        }
        findViewById<Button>(R.id.bt_report_cpm).setOnClickListener {
            val cpm = Random.nextInt(1, Integer.MAX_VALUE)
            listeners.forEach {
                it.onReportCPM(cpm)
            }
        }
    }

    fun startPlay() {
    }

    fun stopPlay() {
    }

    fun onShow() {
        listeners.forEach { it.onShow() }
    }

    fun addListener(listener: AdEventListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: AdEventListener) {
        listeners.remove(listener)
    }

    interface AdEventListener {

        fun onStartPlay()

        fun onStopPlay()

        fun onCompletePlay()

        fun onReportCPM(cpm: Int)

        fun onClick()

        fun onShow()
    }
}