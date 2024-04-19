package com.bytedance.android.douyin.saas.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.bytedance.android.douyin.saas.base.BaseFragment
import com.bytedance.android.douyin.saas.card.VideoCardActivity
import com.bytedance.android.douyin.saas.card.WaterfallVideoCardActivity
import com.bytedance.android.douyin.saas.databinding.FragmentMainBinding
import com.bytedance.android.douyin.saas.live.LiveActivity
import com.bytedance.android.dy.sdk.DouYinSDK
import com.bytedance.android.dy.sdk.api.InitializeListener
import com.bytedance.android.dy.sdk.api.feed.FeedActivityConfig
import com.bytedance.android.live.base.api.IBaseHorizontalLiveListView
import com.bytedance.android.live.base.api.ILiveInitCallback
import com.bytedance.android.openliveplugin.LivePluginHelper
import com.google.gson.Gson
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "MainFragment"
class MainFragment : BaseFragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //该方法要放在onCreate里面，不能放在监听器里
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra("data_return")?.let { Log.d("data_return", it) }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnOpenAwemeFeed.setOnClickListener {
            DouYinSDK.getInstance().openSVideoFeedActivity(
                requireContext(),
                FeedActivityConfig.Builder().showBackButton(false).build()
            )
        }
        binding.btnOpenAwemeCardDemo.setOnClickListener {
            launcher.launch(Intent(context, VideoCardActivity::class.java))
        }
        binding.btnOpenLiveDemo.setOnClickListener {
            launcher.launch(Intent(context, LiveActivity::class.java))
        }

        binding.btnOpenAwemeWaterfallCardDemo.setOnClickListener {
            launcher.launch(Intent(context, WaterfallVideoCardActivity::class.java))
        }

        binding.btnOpenLiveOrderList.setOnClickListener {
            runAfterLivePluginInit {
                lifecycleScope.launch {
                    delay(3000)
                    try {
                        // !!! 本功能为单独支持能力，需要提供 hostAppId 给运营侧，进行配置后才能正常使用。
//                        LivePluginHelper.enterCommerceOrderList(activity)
                    } catch (e: Exception) {
                        Toast.makeText(context, "进入订单中心失败:\n ${Gson().toJson(e)}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnOpenLiveHotWindow.setOnClickListener {
            runAfterLivePluginInit {
                lifecycleScope.launch {
                    delay(3000)
                    addLiveHotWindow()
                }
            }
        }

        if (DouYinSDK.getInstance().isSVideoReady) {
            binding.btnOpenAwemeFeed.isEnabled = true
            binding.btnOpenAwemeCardDemo.isEnabled = true
            binding.btnOpenAwemeWaterfallCardDemo.isEnabled = true
        } else {
            DouYinSDK.getInstance().registerInitializeListener(object : InitializeListener {
                override fun onInitializeSuccess() {
                    Log.d(TAG, "初始化成功")
                    binding.btnOpenAwemeFeed.isEnabled = true
                    binding.btnOpenAwemeCardDemo.isEnabled = true
                    binding.btnOpenAwemeWaterfallCardDemo.isEnabled = true
                }

                override fun onInitializeFail(code: Int) {
                    Log.d(TAG, "初始化失败, 错误码${code}")
                }
            })
        }
        DouYinSDK.getInstance().registerPluginLoadListener { _, eventMsg ->
            binding.tvVideoPluginState.text = "小视频插件加载状态: $eventMsg"
        }
    }

    private fun addLiveHotWindow() {
        binding.layoutLiveHotWindowContainer.removeAllViews()
        // 1. 获取接口
        var horizontalLiveListView: IBaseHorizontalLiveListView? = null
        LivePluginHelper.getLiveRoomService()?.makeFollowListView(context, Bundle(), null)?.let { horView ->
                if (horView.self() != null) {
                    horizontalLiveListView = horView
                    // 2. horView.self() 获取view，添加到布局中
                    binding.layoutLiveHotWindowContainer.addView(horView.self())
                    // 3. 监听天窗列表为空时隐藏view
                    horView.setEmptyListener { empty ->
                        horView.self()?.visibility = if (empty) View.GONE else View.VISIBLE
                    }
                    // 4. 发生错误时隐藏view
                    horView.setErrorListener { error ->
                        if (error) {
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                        }
                        horView.self()?.visibility = if (error) View.GONE else View.VISIBLE
                    }
                    // 5. 监听数据个数变化
                    horView.setRoomCountListener { count ->
                        Toast.makeText(context, "数据个数=$count", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        // 6. 发出请求，刷新数据
        horizontalLiveListView?.refresh()
    }

    private fun runAfterLivePluginInit(action: () -> Unit) {
        if (LivePluginHelper.isLiveInited()) {
            Log.d(TAG, "直播组件初始化完毕")
            action.invoke()
        } else {
            LivePluginHelper.addInitListener(object : ILiveInitCallback {
                override fun onLiveInitFinish() {
                    Log.d(TAG, "直播组件初始化完毕~")
                    action.invoke()
                }

                override fun onLiveInitFailed(p0: String?) {
                    Log.e(TAG, "直播组件初始化失败~")
                }
            })
        }
    }
}