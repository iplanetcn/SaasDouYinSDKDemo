package com.bytedance.android.douyin.saas.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bytedance.android.douyin.saas.R
import com.bytedance.android.douyin.saas.base.BaseFragment
import com.bytedance.android.dy.sdk.internal.awemeopen.AwemeReflectFacade
import com.bytedance.android.dy.sdk.DouYinSDK
import com.bytedance.android.dy.sdk.api.InitializeListener
import com.bytedance.android.dy.sdk.api.SdkInitConfig
import com.bytedance.android.dy.sdk.api.series.ISeriesHomeLayout
import com.bytedance.android.dy.sdk.api.series.SeriesHomeActivityConfig
import com.bytedance.android.dy.sdk.api.series.SeriesHomeLayoutConfig
import org.json.JSONArray
import org.json.JSONObject

class SeriesHomeFragment : BaseFragment() {
    private val TAG = "FeedFragment"
    var seriesHomeLayout: ISeriesHomeLayout? = null
    private lateinit var tvState : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_series_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val frameLayout = view.findViewById<FrameLayout>(R.id.series_home_layout)
        tvState = view.findViewById<TextView>(R.id.tv_video_state)


        val albumId = "7274407381524546104".toLong()
        Log.e("log_qhp", "albumId=$albumId")

        val config = SeriesHomeLayoutConfig.Builder()

            .setSeries(
                JSONObject().apply {
//                    this.put("purchase_series_id", 7274162910438523449L)
//                    this.put("channel1_series_id", JSONArray().apply {
//                        this.put(7274407381524546104L)
//                    })
//                    this.put("channel2_series_id", JSONArray().apply {
//                        this.put(7274407381524546104L)
//                    })
//                    this.put("channel3_series_id", JSONArray().apply {
//                        this.put(7274407381524546104L)
//                    })
//                    this.put("channel4_series_id", JSONArray().apply {
//                        this.put(7274407381524546104L)
//                    })
                })
            .build()
        if (DouYinSDK.getInstance().isSVideoReady) {
            seriesHomeLayout = DouYinSDK.getInstance().createSeriesHomeLayout(activity, config)
            seriesHomeLayout?.let {
                frameLayout.addView(it.view)
            }
        } else {
            DouYinSDK.getInstance().registerPluginLoadListener { eventCode, eventMsg ->
                tvState.text = "小视频插件加载状态: $eventMsg"
            }
            DouYinSDK.getInstance().registerInitializeListener(object : InitializeListener {
                override fun onInitializeSuccess() {
                    seriesHomeLayout = DouYinSDK.getInstance().createSeriesHomeLayout(activity, config)
                    seriesHomeLayout?.let {
                        frameLayout.addView(it.view)
                    }
                    seriesHomeLayout?.onResume() // 主动调用一次onResume，防止feedLayout已经加载后，不获取feed
                }

                override fun onInitializeFail(code: Int) {
                }
            })
        }
    }

    fun onBackPressed() : Boolean {
        return seriesHomeLayout?.onActivityBackPressed() ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        seriesHomeLayout?.onCreate()
    }

    override fun onStart() {
        super.onStart()
        seriesHomeLayout?.onStart()
    }

    override fun onResume() {
        super.onResume()
        seriesHomeLayout?.onResume()
        hideKeyboard()
    }

    override fun onPause() {
        super.onPause()
        seriesHomeLayout?.onPause()
    }

    override fun onStop() {
        super.onStop()
        seriesHomeLayout?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        seriesHomeLayout?.onDestroy()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive && activity?.currentFocus != null) {
            if (activity?.currentFocus?.windowToken != null) {
                imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

}