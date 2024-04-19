package com.bytedance.android.douyin.saas.card

import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytedance.android.douyin.saas.R
import com.bytedance.android.douyin.saas.base.BaseActivity
import com.bytedance.android.douyin.saas.card.model.HorScrollSmallVideoCardModel
import com.bytedance.android.douyin.saas.card.model.MiddleVideoCardModel
import com.bytedance.android.douyin.saas.databinding.ActivityCardBinding

class VideoCardActivity : BaseActivity() {
    private val videoItems: MutableList<Any> = mutableListOf()
    private lateinit var mAdapter: FeedCardRecyclerViewAdapter
    private lateinit var binding: ActivityCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.rbCardGroup.setOnCheckedChangeListener { group, _ ->
            when (group.checkedRadioButtonId) {
                R.id.rb_card_feed_fragment -> mAdapter.jumpToFeedFragment = true
                R.id.rb_card_feed_activity -> mAdapter.jumpToFeedFragment = false
                else -> mAdapter.jumpToFeedFragment = true
            }
        }
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_mileage)!!)
        binding.recyclerView.addItemDecoration(divider)
        mAdapter = FeedCardRecyclerViewAdapter(videoItems, object : FeedCardRecyclerViewAdapter.OnStdCardClickInterface {
            override fun onJumpToFeedLayout() {
                setResult(10000)
                finish()
            }
        })
        binding.recyclerView.adapter = mAdapter

        //模拟请求
        binding.progressBar.visibility = View.VISIBLE
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            binding.progressBar.visibility = View.GONE
            videoItems.clear()

            videoItems.add(MiddleVideoCardModel("1", 1))
            videoItems.add(MiddleVideoCardModel("2",2))
            videoItems.add(HorScrollSmallVideoCardModel("3"))
            videoItems.add(MiddleVideoCardModel("4", 1))
            videoItems.add(MiddleVideoCardModel("5", 2))
            videoItems.add(MiddleVideoCardModel("6", 1))
            videoItems.add(MiddleVideoCardModel("7", 2))
            mAdapter.notifyDataSetChanged()
        }, 500)
    }

}