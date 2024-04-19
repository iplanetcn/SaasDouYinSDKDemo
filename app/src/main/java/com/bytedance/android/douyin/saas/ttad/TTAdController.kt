package com.bytedance.android.douyin.saas.ttad

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.bytedance.android.douyin.saas.DouyinDemoApplication
import com.bytedance.android.dy.sdk.api.series.AoSeriesAdListener
import com.bytedance.sdk.openadsdk.*
import com.bytedance.sdk.openadsdk.TTRewardVideoAd.RewardAdInteractionListener


object TTAdController {

    var ttAdManager: TTAdManager = TTAdSdk.getAdManager()
    var ttAdNative = ttAdManager.createAdNative(DouyinDemoApplication.application)

    var adSlot = AdSlot.Builder()
        .setCodeId("953388798") // 广告代码位Id 953388798(旧) 953671038（新）
        .setAdLoadType(TTAdLoadType.LOAD) // 本次广告用途：TTAdLoadType.LOAD实时；TTAdLoadType.PRELOAD预请求
        .build()

    var ttRewardVideoAd : TTRewardVideoAd? = null

    fun initAd(mActivity: Activity, seriesAdListener: AoSeriesAdListener?) {
        ttAdNative.loadRewardVideoAd(adSlot, object : TTAdNative.RewardVideoAdListener {
            override fun onError(p0: Int, p1: String?) {
                Log.e("TTAdController", "onError p0= $p0, p1 = $p1")
                seriesAdListener?.onLoadRewardVideoError()
            }

            override fun onRewardVideoAdLoad(p0: TTRewardVideoAd?) {
            }

            override fun onRewardVideoCached() {
            }

            override fun onRewardVideoCached(ad: TTRewardVideoAd?) {
                // 此处的ad对象将用于广告展示
                Log.e("TTAdController", "get reward video ad = $ad")
                ttRewardVideoAd = ad
                showAd(mActivity, seriesAdListener)
            }
        })
    }

    fun showAd(mActivity: Activity, seriesAdListener: AoSeriesAdListener?) {
        if (mActivity.isFinishing) {
            return
        }
        // 展示广告前设置回调
        ttRewardVideoAd?.setRewardAdInteractionListener(object :
            RewardAdInteractionListener {
            override fun onAdShow() {
                // 广告展示
                seriesAdListener?.onAdShow()
            }

            override fun onAdVideoBarClick() {
                // 广告点击
                seriesAdListener?.onAdVideoBarClick()
            }

            override fun onAdClose() {
                // 广告关闭
                seriesAdListener?.onAdClose()
            }

            override fun onVideoComplete() {
                // 广告素材播放完成，例如视频未跳过，完整的播放了
                seriesAdListener?.onVideoComplete()
            }

            override fun onVideoError() {
                // 广告展示时出错
                seriesAdListener?.onVideoError()
            }

            override fun onRewardVerify(
                rewardVerify: Boolean,
                rewardAmount: Int,
                rewardName: String,
                errorCode: Int,
                errorMsg: String
            ) {
                // 已废弃 请使用 onRewardArrived 替代
            }

            override fun onRewardArrived(
                isRewardValid: Boolean,
                rewardType: Int,
                extraInfo: Bundle?
            ) {
                // 奖励发放
                // 达到观看时长，解锁剧集
                seriesAdListener?.onRewardArrived(isRewardValid, rewardType, 50, 1, extraInfo)
            }

            override fun onSkippedVideo() {
                // // 用户在观看时点击了跳过
                seriesAdListener?.onSkippedVideo()
            }
        })

        // 展示广告
        ttRewardVideoAd?.showRewardVideoAd(mActivity)
    }

}