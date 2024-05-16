package com.bytedance.android.douyin.saas

import android.app.Activity
import android.app.Application
import android.util.Log
import android.util.Pair
import android.view.View
import com.bytedance.android.douyin.saas.series.SeriesPayContentView
import com.bytedance.android.douyin.saas.series.ad.HostSeriesInsertAdProviderImpl
import com.bytedance.android.douyin.saas.series.ad.SeriesDrawAdConfigImpl
import com.bytedance.android.douyin.saas.series.pay.DemoPackagePrice
import com.bytedance.android.douyin.saas.series.pay.DemoPriceDetail
import com.bytedance.android.douyin.saas.ttad.TTAdController
import com.bytedance.android.douyin.saas.utils.ThreadUtils
import com.bytedance.android.dy.sdk.DouYinSDK
import com.bytedance.android.dy.sdk.api.InitializeListener
import com.bytedance.android.dy.sdk.api.SdkInitConfig
import com.bytedance.android.dy.sdk.api.privacy.DYPrivacyConfig
import com.bytedance.android.dy.sdk.api.series.AoSeriesAdListener
import com.bytedance.android.dy.sdk.api.series.AoSeriesPayResultCallback
import com.bytedance.android.dy.sdk.api.series.AoSeriesPlayConfig
import com.bytedance.android.dy.sdk.internal.awemeopen.series.SeriesInitConfig
import com.bytedance.android.live.base.api.ILiveHostContextParam
import com.bytedance.android.live.base.api.ILiveInitCallback
import com.bytedance.android.openliveplugin.LivePluginHelper
import com.bytedance.sdk.openadsdk.TTAdConfig
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdSdk
import org.json.JSONObject

/**
 * DouyinSdkManager
 *
 * @author john
 * @since 2024-05-11
 */

object DouyinSdkManager {
    const val TAG = "[DouyinSdkManager]"
    fun initSdk(context: Application) {
        Log.e(TAG, "开始初始化抖音SDK")
        initDouyinSdk(context)
        initSeriesPlugin(context)
        initTTAd(context)
    }

    private fun initDouyinSdk(context: Application) {
        // 配置视频参数
        val config: SdkInitConfig = SdkInitConfig.Builder(
            context,
            Constant.DOUYIN_APP_ID,
            Constant.APP_NAME,
            Constant.VERSION_NAME,
            Constant.VERSION_CODE
        ).channel(Constant.CHANNEL)
            .useLiveProcess(true)
            .privacyConfig(
                DYPrivacyConfig.Builder()
                    .isCanUseImei(false)
                    .isCanUseMac(false)
                    .build()
            )
            .initializeListener(object : InitializeListener {
                override fun onInitializeFail(code: Int) {
                    Log.e(TAG, "抖音SDK初始化失败: $code")
                }

                override fun onInitializeSuccess() {
                    Log.e(TAG, "抖音SDK初始化成功")
                }

            })
            .build()

        DouYinSDK.getInstance().init(config)
    }

    /**
     *  初始化短剧插件
     */
    private fun initSeriesPlugin(context: Application) {
        DouYinSDK.getInstance().intSeriesConfig(
            SeriesInitConfig.Builder().setSeriesPlayInject(object :
                AoSeriesPlayConfig {
                override fun showAd(activity: Activity, listener: AoSeriesAdListener) {
                    //进入激励广告
                    TTAdController.initAd(activity, listener)
                }

                override fun getLockIndexAndUnLockNum(p0: Long?): Pair<Int, Int> {
                    /**
                     * 配置短剧解锁集数：
                     * Pair.first -- 初始解锁集数
                     * Pair.second -- 激励广告解锁集数
                     */
                    return Pair(3, 3)
                }

                override fun getHostUid(): String {
                    return "host_uid"
                }

                override fun getPayDialogView(seriesPayObject: JSONObject, listener: AoSeriesPayResultCallback?): View {
                    // TODO
                    Log.d(TAG, "getPayDialogView")
                    val data = DemoPriceDetail()
                    data.seriesId = seriesPayObject.getLong("series_id")
                    data.unitPrice = seriesPayObject.getLong("unit_price")
                    data.segmentUnlock = seriesPayObject.getBoolean("segment_unlock")
                    data.packagePrice = ArrayList()
                    if (seriesPayObject.has("package_price")) {
                        val priceArray = seriesPayObject.getJSONArray("package_price")
                        for (index in 0 until priceArray.length()) {
                            val packagePriceObject = priceArray.get(index) as JSONObject
                            val packagePrice = DemoPackagePrice()
                            packagePrice.unlockCount = packagePriceObject.getInt("unlock_count")
                            packagePrice.discount = packagePriceObject.getDouble("discount")
                            data.packagePrice?.add(packagePrice)
                        }
                    }
                    data.formatData()

                    val contentView = SeriesPayContentView(context)
                    contentView.initData(data, listener)
                    return View(context)
                }

                override fun isAutoShowSeriesPayDialog(): Boolean {
                    return true
                }
            }).setSeriesDrawAdConfig(SeriesDrawAdConfigImpl())
                .setHostSeriesInsertAdProvider(HostSeriesInsertAdProviderImpl()).build()
        )
    }

    /**
     * 初始化抖音直播SDK
     */
    private fun initLivePlugin(application: Application) {
        // 初始化参数
        val builder = ILiveHostContextParam.Builder()
            .setAppName(Constant.APP_NAME)                 // 宿主的appName，用于内部一些场景文案
            .setVersion(Constant.VERSION_NAME)             // 宿主版本信息, 三段数字形式，不要带有字母后缀
            .setVersionCode(Constant.VERSION_CODE.toInt()) // 宿主版本号

        // 初始化直播回调
        val initCallback = object : ILiveInitCallback {
            override fun onLiveInitFinish() {
                Log.d(TAG, "抖音-直播组件初始化完成")
            }

            override fun onLiveInitFailed(errMsg: String?) {
                Log.d(TAG, "抖音-直播组件初始化失败，$errMsg")
            }
        }

        // 传入appId、回调等（内部已异步执行，不阻塞）
        // 最后一个参数传递false时，将不进行插件下载，使用内置插件
        LivePluginHelper.init(application, Constant.DOUYIN_APP_ID, builder, initCallback, true)
    }

    /**
     * 初始化穿山甲广告SDK
     */
    private fun initTTAd(context: Application) {
        // TODO 穿山甲广告冲突[重复初始化问题]
        val build = TTAdConfig.Builder().appId(Constant.CSJ_APPID) //穿山甲媒体id
            .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
            .appName(Constant.APP_NAME)
            .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
            .allowShowNotify(true) //是否允许sdk展示通知栏提示
            .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
            .directDownloadNetworkType(
                TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G
            ) //允许直接下载的网络状态集合
            /**设置支持多进程 */
            .supportMultiProcess(true) //是否支持多进程，此处必须为true
            .build()

        TTAdSdk.init(context, build)
        TTAdSdk.start(object : TTAdSdk.Callback {
            override fun success() {
                Log.d(TAG, "抖音-穿山甲广告SDK初始化成功")
                ThreadUtils.runOnUIThread {
                    DouYinSDK.getInstance().notifyTTAdLoadFinished()
                }
            }

            override fun fail(code: Int, msg: String) {
                Log.e(TAG, "抖音-穿山甲广告SDK初始化失败，{ code: $code, msg: $msg }")
            }
        })
    }
}