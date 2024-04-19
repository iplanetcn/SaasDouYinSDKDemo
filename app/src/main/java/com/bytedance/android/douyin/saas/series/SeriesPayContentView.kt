package com.bytedance.android.douyin.saas.series

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.android.douyin.saas.R
import com.bytedance.android.douyin.saas.series.pay.DemoPackagePrice
import com.bytedance.android.douyin.saas.series.pay.DemoPriceDetail
import com.bytedance.android.dy.sdk.api.series.AoSeriesPayResultCallback
import org.json.JSONObject


/**
 * 短剧支付
 */
class SeriesPayContentView constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    FrameLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        LayoutInflater.from(context).inflate(R.layout.aos_series_pay_list, this, true)
        initView()
    }

    private var seriesChooseItemAdapter: SeriesPayItemAdapter<SeriesPayItemViewHolder>? = null
    private var contentView: ViewGroup? = null
    private var title: TextView? = null
    private var desTv: TextView? = null

    private var recyclerView: RecyclerView? = null
    private var close: View? = null
    private var confirm: View? = null

    private lateinit var action: () -> Unit
    private var lastSelectedIndex: Int = 0

    private var priceDetail: DemoPriceDetail? = null
    private var callback: AoSeriesPayResultCallback? = null


    companion object {
        private const val TAG = "SeriesPayContentView"
    }

    fun initView() {
        contentView = findViewById(R.id.series_pay_root_layout)
        title = findViewById(R.id.series_pay_title)
        desTv = findViewById(R.id.series_pay_des)
        close = findViewById(R.id.series_pay_iv_close)
        recyclerView = findViewById(R.id.series_pay_list)
        confirm = findViewById(R.id.series_pay_confirm)
        close?.setOnClickListener {
            callback?.onPayFail(JSONObject().put("errorMsg", "关闭面板"))
        }
        confirm?.setOnClickListener {
            action.invoke()
        }
    }

    fun initData(priceDetail: DemoPriceDetail?, callback: AoSeriesPayResultCallback?) {
        this.priceDetail = priceDetail
        this.callback = callback
        if (priceDetail?.isPriceValid() == false) {
            return
        }
        desTv?.text = String.format(context.getString(R.string.series_price_des), priceDetail?.unitPrice)
        initRecycleView()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecycleView() {
        recyclerView?.apply {
            itemAnimator = null
            isNestedScrollingEnabled = true
        }
        val linearLayoutManager = GridLayoutManager(context, 2)
        recyclerView?.layoutManager = linearLayoutManager
        seriesChooseItemAdapter = SeriesPayItemAdapter()
        seriesChooseItemAdapter!!.setData(priceDetail?.packagePrice!!)
        seriesChooseItemAdapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(demoPackagePrice: DemoPackagePrice, position: Int) {
                if (lastSelectedIndex == position) {
                    return
                }
                priceDetail?.packagePrice!![lastSelectedIndex].isSelected = false
                priceDetail?.packagePrice!![position].isSelected = true
                seriesChooseItemAdapter?.notifyDataSetChanged()
                lastSelectedIndex = position
                action = {
                    mockOnItemClick(demoPackagePrice)
                }
            }
        })
        recyclerView?.adapter = seriesChooseItemAdapter
        recyclerView?.addItemDecoration(SeriesPayDialogRvDecoration(12))
        action = {
            mockOnItemClick(priceDetail?.packagePrice!![0])
        }
        lastSelectedIndex = 0

    }

    private fun mockOnItemClick(demoPackagePrice: DemoPackagePrice) {
        /**
         * TODO:
         * 1. 通知服务端调用开平服务端接口
         * 2. 回调onPaySuccess
         */
        callback?.onPaySuccess(demoPackagePrice.unlockCount)
    }

    @Suppress("UNCHECKED_CAST")
    inner class SeriesPayItemAdapter<VH : DSRecyclerItemViewHolder<DemoPackagePrice>> :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val holderSet = HashSet<VH>()
        lateinit var listOfPackagePrice: List<DemoPackagePrice>
        private lateinit var onItemClickListener: OnItemClickListener
        override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
            super.onViewRecycled(holder)
            //因为有Footer，所以这里尝试转换
            (holder as? VH)?.let {
                it.unBind()
                holderSet.remove(it)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val iteView = LayoutInflater.from(context).inflate(R.layout.seriese_pay_item, parent, false)
            return SeriesPayItemViewHolder(iteView, onItemClickListener)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as VH).let {
                it.bind(listOfPackagePrice[position], position)
                holderSet.add(it)
            }
        }

        override fun getItemCount(): Int {
            return listOfPackagePrice.size
        }

        fun recycle() {
            holderSet.forEach {
                it.unBind()
            }
            holderSet.clear()
        }

        fun setData(listOfCurrentSeries: List<DemoPackagePrice>) {
            this.listOfPackagePrice = listOfCurrentSeries
        }

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
            this.onItemClickListener = onItemClickListener
        }


    }

    interface OnItemClickListener {
        fun onItemClick(demoPackagePrice: DemoPackagePrice, position: Int)
    }


}