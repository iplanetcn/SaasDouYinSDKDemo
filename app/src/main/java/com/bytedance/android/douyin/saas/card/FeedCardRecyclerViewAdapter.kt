package com.bytedance.android.douyin.saas.card

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.android.douyin.saas.card.model.HorScrollSmallVideoCardModel
import com.bytedance.android.douyin.saas.card.model.MiddleVideoCardModel
import com.bytedance.android.dy.sdk.DouYinSDK
import com.bytedance.android.dy.sdk.DouYinVideoSDK
import com.bytedance.android.dy.sdk.api.card.ICardView
import com.bytedance.android.dy.sdk.api.card.IVideoHozScrollCard
import com.bytedance.android.dy.sdk.api.card.IVideoStdCard

/**
 * 中视频插卡、小视频横滑卡的Adapter
 */
class FeedCardRecyclerViewAdapter(videoItemList: MutableList<Any>, val stdCardClick: OnStdCardClickInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        //小视频横滑卡
        private const val HORIZONTAL_SCROLL_VIDEO_CARD = 1

        //中视频横屏卡片
        private const val MIDDLE_HORIZONTAL_VIDEO_CARD = 2

        //中视频竖屏卡片
        private const val MIDDLE_PORTRAIT_VIDEO_CARD = 3
    }

    public var jumpToFeedFragment = true

    private val videos: MutableList<Any> = videoItemList

    private val viewToPosition = mutableMapOf<View, Int>()

    private val viewToStdListener = mutableMapOf<IVideoStdCard, View>()

    private val viewToHozListener = mutableMapOf<IVideoHozScrollCard, View>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HORIZONTAL_SCROLL_VIDEO_CARD -> {
                val hozScrollCardListener = object : IVideoHozScrollCard {

                    /**
                     * 取消按钮回调
                     */
                    override fun onCloseClick() {
                        val view = viewToHozListener[this]
                        removeItem(viewToPosition[view])
                    }

                    /**
                     * 卡片点击时回调
                     * @param position 被点击的卡片位置，以0开始
                     * @return true: 拦截跳转FeedActivity，跳转至Feed频道。false：不拦截跳转，跳转至FeedActivity.
                     */
                    override fun onItemClick(position: Int): Boolean {
                        return if (jumpToFeedFragment) {
                            switchToFeedFragment()
                            true
                        } else {
                            false
                        }
                    }

                    /**
                     * 进入Feed内流时回调
                     * @return true: 拦截跳转FeedActivity，跳转至Feed频道。false：不拦截跳转，跳转至FeedActivity.
                     */
                    override fun onPullToFeed(): Boolean {
                        return if (jumpToFeedFragment) {
                            switchToFeedFragment()
                            true
                        } else {
                            false
                        }
                    }

                    /**
                     * 数据加载失败时回调
                     */
                    override fun onCardDataError() {
                    }
                }
                val hozScrollCard = DouYinSDK.getInstance().createVideoHozScrollCard(hozScrollCardListener)
                viewToHozListener[hozScrollCardListener] = hozScrollCard.view
                HorScrollVideoCardViewHolder(hozScrollCard)
            }
            MIDDLE_HORIZONTAL_VIDEO_CARD -> {
                val orientation = 1
                val stdCardListener = object : IVideoStdCard {

                    /**
                     * 取消按钮回调
                     */
                    override fun onCloseClick() {
                        val view = viewToStdListener[this]
                        removeItem(viewToPosition[view])
                    }

                    /**
                     * 卡片点击时回调
                     * @return true: 拦截跳转FeedActivity，跳转至Feed频道。false：不拦截跳转，跳转至FeedActivity.
                     */
                    override fun onItemClick(): Boolean {
                        return if (jumpToFeedFragment) {
                            switchToFeedFragment()
                            true
                        } else {
                            false
                        }
                    }

                    /**
                     * 数据加载失败时回调
                     */
                    override fun onCardDataError() {
                    }
                }
                val cardView = DouYinSDK.getInstance().createVideoStdCard(orientation, stdCardListener)
                viewToStdListener[stdCardListener] = cardView.view
                MiddleVideoCardViewHolder(cardView)
            }
            else -> {
                val orientation = 2
                val stdCardListener = object : IVideoStdCard {

                    /**
                     * 取消按钮回调
                     */
                    override fun onCloseClick() {
                        val view = viewToStdListener[this]
                        removeItem(viewToPosition[view])
                    }

                    /**
                     * 卡片点击时回调
                     */
                    override fun onItemClick(): Boolean {
                        return if (jumpToFeedFragment) {
                            switchToFeedFragment()
                            true
                        } else {
                            false
                        }
                    }

                    /**
                     * 数据加载失败时回调
                     */
                    override fun onCardDataError() {

                    }
                }
                val cardView = DouYinVideoSDK.getInstance().createVideoStdCard(orientation, stdCardListener)
                viewToStdListener[stdCardListener] = cardView.view
                MiddleVideoCardViewHolder(cardView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (videos[position] is MiddleVideoCardModel) {
            if ((videos[position] as MiddleVideoCardModel).orientation == 1) {
                return MIDDLE_HORIZONTAL_VIDEO_CARD
            }
            return MIDDLE_PORTRAIT_VIDEO_CARD
        } else {
            return HORIZONTAL_SCROLL_VIDEO_CARD
        }
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HorScrollVideoCardViewHolder -> {
                holder.bind(videos[position] as HorScrollSmallVideoCardModel, position)
            }
            is MiddleVideoCardViewHolder -> {
                holder.bind(videos[position] as MiddleVideoCardModel, position)
            }
        }
    }

    private fun removeItem(position: Int?) {
        if (position == null)   return
        if (position >= 0 && position < videos.size) {
            videos.removeAt(position)
            notifyDataSetChanged()
            resetMap(position)
        }
    }

    private fun resetMap(position: Int) {
        for ((view, pos) in viewToPosition){
            if (pos > position) {
                viewToPosition[view] = pos - 1
            }
        }
    }

    private fun switchToFeedFragment() {
        stdCardClick.onJumpToFeedLayout()
    }

    /**
     * 小视频横滑卡
     */
    inner class HorScrollVideoCardViewHolder(private val horScrollVideoCard: ICardView) :
        RecyclerView.ViewHolder(horScrollVideoCard.view) {
        private var itemPosition: Int = -1

        fun bind(data: HorScrollSmallVideoCardModel, position: Int) {
            itemPosition = position
            viewToPosition[horScrollVideoCard.view] = position
        }
    }

    /**
     * 中视频插卡
     */
    inner class MiddleVideoCardViewHolder(private val feedCardView: ICardView) :
        RecyclerView.ViewHolder(feedCardView.view) {
        private var itemPosition: Int = -1

        fun bind(data: MiddleVideoCardModel, position: Int) {
            itemPosition = position
            viewToPosition[feedCardView.view] = position
        }
    }

    interface OnStdCardClickInterface {
        fun onJumpToFeedLayout()
    }

}