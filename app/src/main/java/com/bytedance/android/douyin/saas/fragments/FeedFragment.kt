package com.bytedance.android.douyin.saas.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bytedance.android.douyin.saas.R
import com.bytedance.android.douyin.saas.base.BaseFragment
import com.bytedance.android.dy.sdk.DouYinSDK
import com.bytedance.android.dy.sdk.api.InitializeListener
import com.bytedance.android.dy.sdk.api.feed.FeedLayoutConfig
import com.bytedance.android.dy.sdk.api.feed.FeedLayoutLoadListener
import com.bytedance.android.dy.sdk.api.feed.FeedLayoutPlayerListener
import com.bytedance.android.dy.sdk.api.feed.IFeedLayout

class FeedFragment : BaseFragment() {
    private val TAG = "FeedFragment"

    var feedLayout: IFeedLayout? = null
    private lateinit var tvState : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val frameLayout = view.findViewById<FrameLayout>(R.id.feed_layout)
        tvState = view.findViewById(R.id.tv_video_state)

        val feedBuilder = FeedLayoutConfig.Builder()
        feedBuilder.loadListener(object : FeedLayoutLoadListener {
            override fun onLoadError(code: Int, msg: String) {
                Log.d(TAG, "errorCode = \$code, msg = \$msg")
            }

            override fun onLoadSuccess() {
                Log.d(TAG, "load success")
            }
        }).playerListener(object : FeedLayoutPlayerListener {
            override fun onPlayerStart(videoId: String, extra: Map<String, Any>?) {
                Log.d(TAG, "player start: \$videoId, \$extra")
            }

            override fun onPlayerStop(videoId: String, extra: Map<String, Any>?) {
                Log.d(TAG, "player start: \$videoId, \$extra")
            }

            override fun onPlayerResume(videoId: String, extra: Map<String, Any>?) {
                Log.d(TAG, "player start: \$videoId, \$extra")
            }

            override fun onPlayerPause(videoId: String, extra: Map<String, Any>?) {
                Log.d(TAG, "player start: \$videoId, \$extra")
            }

            override fun onPlayerComplete(videoId: String, extra: Map<String, Any>?) {
                Log.d(TAG, "player start: \$videoId, \$extra")
            }

            override fun onPlayerRestart(videoId: String, extra: Map<String, Any>?) {
                Log.d(TAG, "player start: \$videoId, \$extra")
            }
        }).isTeenagerModel(false)

        if (DouYinSDK.getInstance().isSVideoReady) {
            feedLayout = DouYinSDK.getInstance().createSVideoFeedLayout(activity, feedBuilder.build())
            feedLayout?.let {
                frameLayout.addView(it.view)
            }
        } else {
            DouYinSDK.getInstance().registerInitializeListener(object : InitializeListener {
                override fun onInitializeSuccess() {
                    feedLayout = DouYinSDK.getInstance().createSVideoFeedLayout(activity, feedBuilder.build())
                    feedLayout?.let {
                        frameLayout.addView(it.view)
                    }
                    feedLayout?.onResume() // 主动调用一次onResume，防止feedLayout已经加载后，不获取feed
                }

                override fun onInitializeFail(code: Int) {
                }
            })
        }
    }

    fun onBackPressed() : Boolean {
        return feedLayout?.onActivityBackPressed() ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feedLayout?.onCreate()
    }

    override fun onStart() {
        super.onStart()
        feedLayout?.onStart()
    }

    override fun onResume() {
        super.onResume()
        feedLayout?.onResume()
    }

    override fun onPause() {
        super.onPause()
        feedLayout?.onPause()
    }

    override fun onStop() {
        super.onStop()
        feedLayout?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        feedLayout?.onDestroy()
    }

}