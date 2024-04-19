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
import com.bytedance.android.douyin.saas.databinding.FragmentLiveBinding
import com.bytedance.android.live.base.api.ILiveInitCallback
import com.bytedance.android.live.base.api.outer.ILivePreviewLayout
import com.bytedance.android.live.base.api.outer.ILivePreviewLayout.OnRefreshListener
import com.bytedance.android.openliveplugin.LivePluginHelper

private const val TAG = "LiveFragment"

/**
 * LiveFragment
 *
 * @author john
 * @since 2024-04-16
 */
class LiveFragment : BaseFragment() {
    private var previewLayout: ILivePreviewLayout? = null
    private lateinit var binding: FragmentLiveBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLiveBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (LivePluginHelper.isLiveInited()) {
            initLive()
            binding.tvLiveState.text = "直播已初始化"
        } else {
            LivePluginHelper.addInitListener(object : ILiveInitCallback{
                override fun onLiveInitFinish() {
                    binding.tvLiveState.text = "直播已初始化完成~"
                    initLive()
                }

                override fun onLiveInitFailed(message: String?) {
                    binding.tvLiveState.text = "直播已初始化失败~"
                    Toast.makeText(context, "直播初始化失败 $message", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun initLive() {
        val bundle = Bundle()
        val builder = ILivePreviewLayout.Builder(activity)
            .setShowTopList(true)//是否展示顶部天窗，默认false
            .setEnablePullToRefresh(true)//是否允许下拉刷新，默认true
            .setSmoothEnterEnable(false)//是否启用平滑进房，默认false
            .setAutoEnterEnable(false)//是否启用自动进房，默认false
            .setCanShowDislike(true)//是否启用长按触发dislike，默认true
            .setShowTopListAtOnce(false)//天窗能展示并且有数据时，首次加载是否直接展示，默认false
            .setArguments(bundle)//设置额外的配置参数，天窗样式、预览流主播标签圆角等, enter_from_tob 分入口参数
            .setLiveBorderAnimController(null)//天窗头像动效自定义，传null用默认动效果。一般使用默认即可

        // 创建并添加view到布局
        previewLayout = LivePluginHelper.getLiveRoomService()?.liveProvider?.getILivePreviewLayout(builder)?.also {
            binding.liveLayout.removeAllViews()
            binding.liveLayout.addView(it.view)
        }

        // 刷新
        previewLayout?.setRefreshListener(object: OnRefreshListener {
            override fun onSuccess() {
                Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onError(reason: String) {
                Toast.makeText(context, "刷新失败", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // 播放，onResume()时
        previewLayout?.onPlay()
    }

    override fun onStop() {
        super.onStop()
        // 暂停，退后台，页面覆盖等，onPause()时
        previewLayout?.onStop()
    }


    override fun onDestroy() {
        super.onDestroy()
        // 释放销毁，onDestroy()
        previewLayout?.onRelease()
    }
}