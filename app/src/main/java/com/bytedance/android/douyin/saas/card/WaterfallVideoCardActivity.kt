package com.bytedance.android.douyin.saas.card

import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bytedance.android.douyin.saas.R
import com.bytedance.android.douyin.saas.base.BaseActivity
import com.bytedance.android.douyin.saas.card.model.HorScrollSmallVideoCardModel
import com.bytedance.android.douyin.saas.card.model.MiddleVideoCardModel
import com.bytedance.android.douyin.saas.databinding.ActivityWaterfallCardBinding

class WaterfallVideoCardActivity : BaseActivity() {
    private val videoItems: MutableList<Any> = mutableListOf()
    private lateinit var mAdapter: FeedCardRecyclerViewAdapter
    private lateinit var binding: ActivityWaterfallCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterfallCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.rbCardGroup.setOnCheckedChangeListener { group, _ ->
            when (group.checkedRadioButtonId) {
                R.id.rb_card_feed_fragment -> mAdapter.jumpToFeedFragment = true
                R.id.rb_card_feed_activity -> mAdapter.jumpToFeedFragment = false
                else -> mAdapter.jumpToFeedFragment = true
            }
        }
        val layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager
        mAdapter = FeedCardRecyclerViewAdapter(videoItems,
            object : FeedCardRecyclerViewAdapter.OnStdCardClickInterface {
                override fun onJumpToFeedLayout() {
                    setResult(10000)
                    finish()
                }
            })
        binding.recyclerView.adapter = mAdapter

        //模拟请求
        binding.recyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            videoItems.clear()

            videoItems.add(MiddleVideoCardModel("1", 1))
            videoItems.add(MiddleVideoCardModel("2", 2))
            videoItems.add(HorScrollSmallVideoCardModel("3"))
            videoItems.add(MiddleVideoCardModel("4", 1))
            videoItems.add(MiddleVideoCardModel("5", 2))
            videoItems.add(MiddleVideoCardModel("6", 1))
            videoItems.add(MiddleVideoCardModel("7", 2))
            mAdapter.notifyDataSetChanged()
        }, 500)
    }

}