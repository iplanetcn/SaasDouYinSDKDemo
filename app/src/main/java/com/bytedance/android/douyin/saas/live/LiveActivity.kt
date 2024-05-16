package com.bytedance.android.douyin.saas.live

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bytedance.android.douyin.saas.R
import com.bytedance.android.douyin.saas.base.BaseActivity
import com.bytedance.android.douyin.saas.databinding.ActivityLiveBinding
import com.bytedance.android.live.base.api.ILiveInitCallback
import com.bytedance.android.live.base.api.outer.data.RoomInfo
import com.bytedance.android.openliveplugin.LivePluginHelper
import com.bytedance.android.openliveplugin.LivePluginHelper.enterCommerceOrderList
import com.bytedance.android.openliveplugin.LiveReflectFacade
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private const val TAG = "LiveActivity"

class LiveActivity : BaseActivity() {
    private lateinit var binding: ActivityLiveBinding
    private var data: MutableList<RoomInfo> = mutableListOf()
    private var adapter: LiveCardAdapter = LiveCardAdapter(data)
    private var ioScope: CoroutineScope? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.toolbar)
        binding.recyclerView.adapter = adapter
//        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.addItemDecoration(SpaceGridItemDecoration(2, 8))
        binding.refreshLayout.setOnRefreshListener {
            runAfterLivePluginInit { getRoomInfoListAfterInit() }
        }

        binding.tvMessage.text = "直播组件加载中..."

        runAfterLivePluginInit {
            getRoomInfoListAfterInit()
            createLiveCard()
        }
    }

    private fun runAfterLivePluginInit(action: () -> Unit) {
        if (LivePluginHelper.isLiveInited()) {
            binding.tvMessage.text = "直播组件初始化完毕"
            action.invoke()
        } else {
            LivePluginHelper.addInitListener(object : ILiveInitCallback {
                override fun onLiveInitFinish() {
                    binding.tvMessage.text = "直播组件初始化完毕~"
                    action.invoke()
                }

                override fun onLiveInitFailed(p0: String?) {
                    binding.tvMessage.text = "直播组件初始化失败~"
                }
            })
        }
    }

    private fun createLiveCard() {
        try {
            val preview =
                LiveReflectFacade.getOuterLiveService().liveProvider.makeStandalonePreviewView(this, 0, Bundle())
            binding.layoutContainer.addView(preview.view)
            preview.stream()
            preview.show()
        } catch (e: Exception) {
            Log.e(TAG, "createLiveCard error: ${Gson().toJson(e)}")
        }
    }

    private fun getRoomInfoListAfterInit() {
        ioScope?.cancel()
        ioScope = CoroutineScope(Dispatchers.IO)
        ioScope?.launch {
            Log.d(TAG, "ioScope" + Thread.currentThread().name.toString())
            try {
                val result = requestRoomInfoList()
                lifecycleScope.launch {
                    Log.d(TAG, "lifecycleScope:" + Thread.currentThread().name.toString())
                    Log.d(TAG, "roomInfoList: ${Gson().toJson(result)}")
                    result?.apply {
                        if (isNotEmpty()) {
                            data.addAll(this)
                            adapter.notifyDataSetChanged()
                        }
                    }

                    binding.refreshLayout.isRefreshing = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "roomInfoList error: ${Gson().toJson(e)}")
                binding.refreshLayout.isRefreshing = false
            }
        }


        Log.d(TAG, "Outside Global Scope:" + Thread.currentThread().name.toString())
    }

    private fun requestRoomInfoList(): List<RoomInfo>? {
        val position: Int = 0 // 场景值，Int
        val count: Int = 6 // 预期获取元素个数，Int，最多为返回 6 个
        val pullType: Int = 1 // 数据获取类型，传1表示刷新，传2表示加载更多
        return LiveReflectFacade.getOuterLiveService()?.liveProvider?.getRoomInfoList(
            position,
            count,
            pullType,
            Bundle()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        ioScope?.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_live, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_enter_live_orders) {
            LiveReflectFacade.getOuterLiveService()
        }
        return super.onOptionsItemSelected(item)
    }
}