package com.bytedance.android.douyin.saas.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bytedance.android.douyin.saas.R
import com.bytedance.android.douyin.saas.databinding.ItemLiveCardBinding
import com.bytedance.android.live.base.api.outer.data.RoomInfo
import com.bytedance.android.openliveplugin.LivePluginHelper

/**
 * LiveCardAdapter
 *
 * @author john
 * @since 2024-04-18
 */
class LiveCardAdapter(val data: MutableList<RoomInfo> = mutableListOf()) : RecyclerView.Adapter<LiveCardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveCardViewHolder {
        val binding = ItemLiveCardBinding.inflate(LayoutInflater.from(parent.context))
        return LiveCardViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: LiveCardViewHolder, position: Int) {
        holder.bind(data[position])
    }
}

class LiveCardViewHolder(private val binding: ItemLiveCardBinding) : ViewHolder(binding.root) {
    fun bind(roomInfo: RoomInfo) {
        Glide.with(itemView)
            .load(roomInfo.cover)
            .centerCrop()
            .into(binding.ivLiveRoomCover)

        Glide.with(itemView)
            .load(roomInfo.owner.avatar)
            .centerCrop()
            .into(binding.ivLiveAuthorAvatar)

        binding.tvRoomStatus.text = convertStatus(roomInfo.status)
        binding.tvAuthorName.text = roomInfo.owner.nickname

        binding.ivLiveRoomCover.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("request_id", roomInfo.requestId)
            bundle.putInt("enter_from_tob", 0)
            LivePluginHelper.getLiveRoomService()?.liveProvider?.startLive(itemView.context, 0, roomInfo.openRoomId, bundle)
        }
    }

    // 1. 创建；2. 开播；3. 暂停；4.恢复；5. 关播
    private fun convertStatus(status: Long): String {
        return when (status) {
            1L -> "创建"
            2L -> "直播中"
            3L -> "暂停"
            4L -> "恢复"
            5L -> "关播"
            else -> "未知"
        }
    }
}