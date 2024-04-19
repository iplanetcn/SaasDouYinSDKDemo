package com.bytedance.android.douyin.saas.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bytedance.android.douyin.saas.R
import com.bytedance.android.douyin.saas.base.BaseFragment
import com.bytedance.android.dy.sdk.DouYinSDK
import com.bytedance.android.dy.sdk.api.InitializeListener
import com.bytedance.android.dy.sdk.api.base.VideoCommonCallback
import com.bytedance.android.dy.sdk.api.series.light.ISeriesLightLayout
import com.bytedance.android.dy.sdk.api.series.light.ISeriesLightManager
import com.bytedance.android.dy.sdk.api.series.listener.SeriesLightPlayerListener
import com.bytedance.android.dy.sdk.api.series.listener.SeriesLightUIListener
import com.bytedance.android.dy.sdk.api.series.model.OpenSeriesDetail
import com.bytedance.android.dy.sdk.api.series.model.SeriesLightManagerConfig
import com.bytedance.android.dy.sdk.api.series.model.SeriesLightViewConfig

class SeriesLightFragment : BaseFragment() {
    private val TAG = "SeriesHomeFragment"
    var seriesLightManager: ISeriesLightManager? = null
    var seriesLightLayout: ISeriesLightLayout? = null
    private lateinit var tvState : TextView
    private lateinit var rootView : ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_series_light, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val frameLayout = view.findViewById<FrameLayout>(R.id.series_slim_layout)
        rootView = view.findViewById(R.id.fl_root_view)
        tvState = view.findViewById<TextView>(R.id.tv_video_state)


        val seriesLightBuilder = SeriesLightManagerConfig.Builder()
        seriesLightBuilder.playerListener(object : SeriesLightPlayerListener {
            override fun onPlayerStart(
                seriesId: Long?,
                episodeId: Long?,
                extra: MutableMap<String, Any>?
            ) {
                Log.d(TAG, "onPlayerStart seriesId = $seriesId, episodeId=$episodeId")
            }

            override fun onPlayerStop(
                seriesId: Long?,
                episodeId: Long?,
                extra: MutableMap<String, Any>?
            ) {
                Log.d(TAG, "onPlayerStop seriesId = $seriesId, episodeId=$episodeId")
            }

            override fun onPlayerResume(
                seriesId: Long?,
                episodeId: Long?,
                extra: MutableMap<String, Any>?
            ) {
                Log.d(TAG, "onPlayerResume seriesId = $seriesId, episodeId=$episodeId")
            }

            override fun onPlayerPause(
                seriesId: Long?,
                episodeId: Long?,
                extra: MutableMap<String, Any>?
            ) {
                Log.d(TAG, "onPlayerPause seriesId = $seriesId, episodeId=$episodeId")
            }

            override fun onPlayerComplete(
                seriesId: Long?,
                episodeId: Long?,
                extra: MutableMap<String, Any>?
            ) {
                Log.d(TAG, "onPlayerComplete seriesId = $seriesId, episodeId=$episodeId")
            }

            override fun onPlayerRestart(
                seriesId: Long?,
                episodeId: Long?,
                extra: MutableMap<String, Any>?
            ) {
                Log.d(TAG, "onPlayerRestart seriesId = $seriesId, episodeId=$episodeId")
            }

            override fun onPlayerProgress(p0: Long?, p1: Long?, p2: Int, p3: MutableMap<String, Any>?) {
                Log.d(TAG, "onPlayerProgress ")
            }

        })
        seriesLightManager = DouYinSDK.getInstance().initSeriesLight(seriesLightBuilder.build())

        val lightViewConfig = SeriesLightViewConfig.Builder().showLoadingUI(true).showPauseUI(true)
            .UIListener(object : SeriesLightUIListener {
                override fun onLoadingShow() {
                    Log.d(TAG, "onLoadingShow")
                }

                override fun onLoadingDismiss() {
                    Log.d(TAG, "onLoadingDismiss")
                }

                override fun onPauseShow() {
                    Log.d(TAG, "onPauseShow")
                }

                override fun onPauseDismiss() {
                    Log.d(TAG, "onPauseDismiss")
                }
            }).build()

        if (DouYinSDK.getInstance().isSVideoReady) {
            seriesLightLayout = DouYinSDK.getInstance().createSeriesLightView(lightViewConfig)
            seriesLightLayout?.let {
                frameLayout.addView(it.view)
            }
            initSeries()
        } else {
            DouYinSDK.getInstance().registerPluginLoadListener { eventCode, eventMsg ->
                tvState.text = "小视频插件加载状态: $eventMsg"
            }
            DouYinSDK.getInstance().registerInitializeListener(object : InitializeListener {
                override fun onInitializeSuccess() {
                    seriesLightLayout = DouYinSDK.getInstance().createSeriesLightView(lightViewConfig)
                    seriesLightLayout?.let {
                        frameLayout.addView(it.view)
                    }
                    initSeries()
                }

                override fun onInitializeFail(code: Int) {
                }
            })
        }
    }

    var currentSeriesId : Long = 0L
    var seriesDetail : OpenSeriesDetail? = null

    fun initSeries() {
        getDetail(true, 7274172581467390522L)
    }

    private fun getDetail(isFirst: Boolean, seriesId: Long) {
        DouYinSDK.getInstance().getSeriesDetail(seriesId, 0, 3, 3, "",
            object : VideoCommonCallback<OpenSeriesDetail, String>{
                override fun onSuccess(result: OpenSeriesDetail?) {

                    currentSeriesId = seriesId
                    seriesDetail = result

                    if (!isFirst) {
                        seriesLightLayout?.unSelected()
                        seriesLightLayout?.unBind()
                    }
                    seriesLightLayout?.bind(seriesId, result?.openEpisodeInfo?.get(0)?.episodeId!!, 0)
                    seriesLightLayout?.selected()
                }

                override fun onFail(msg: String?) {
                    Log.e(TAG, "getSeriesData onFail msg=$msg")

                    Toast.makeText(requireContext(), "获取短剧信息失败", Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        seriesLightManager?.onCreate()
    }

    override fun onStart() {
        super.onStart()
        seriesLightManager?.onStart()
    }

    override fun onResume() {
        super.onResume()
        seriesLightManager?.onResume()
    }

    override fun onPause() {
        super.onPause()
        seriesLightManager?.onPause()
    }

    override fun onStop() {
        super.onStop()
        seriesLightManager?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        seriesLightManager?.onDestroy()
    }

}