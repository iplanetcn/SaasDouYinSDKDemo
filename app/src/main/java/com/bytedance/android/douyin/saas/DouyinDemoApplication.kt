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
import com.bytedance.sdk.openadsdk.TTAdConfig
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdSdk
import org.json.JSONObject

/**
 * 应用 Application
 *
 * @author yuanzeng@bytedance.com
 * @since 2022/10/27 9:26 下午
 */
class DouyinDemoApplication : Application() {

    companion object {
        lateinit var application: Application
        const val TAG = "DouyinDemoApplication"
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        // 配置视频参数
        DouYinSDK.getInstance().init(
            SdkInitConfig.Builder(
                this,
                Constant.DOUYIN_APP_ID,
                Constant.APP_NAME,
                Constant.VERSION_NAME,
                Constant.VERSION_CODE
            )
                .channel(Constant.CHANNEL)
                .useLiveProcess(true)
                .privacyConfig(
                    DYPrivacyConfig.Builder()
                        .isCanUseImei(false)
                        .isCanUseMac(false)
                        .build()
                )
                .initializeListener(object : InitializeListener {
                    override fun onInitializeFail(code: Int) {
                        Log.e(TAG, "onInitializeFail: $code")
                    }

                    override fun onInitializeSuccess() {
                        Log.e(TAG, "onInitializeSuccess")
                    }

                })
                .build()
        )

        // 配置短剧参数
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

                    val contentView = SeriesPayContentView(this@DouyinDemoApplication)
                    contentView.initData(data, listener)
                    return contentView
                }

                override fun isAutoShowSeriesPayDialog(): Boolean {
                    return true
                }
            }).setSeriesDrawAdConfig(SeriesDrawAdConfigImpl())
                .setHostSeriesInsertAdProvider(HostSeriesInsertAdProviderImpl()).build()
        )

        initTTAdSdk()
    }

    private fun initTTAdSdk() {
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

        TTAdSdk.init(this, build)
        TTAdSdk.start(object : TTAdSdk.Callback {
            override fun success() {
                Log.d(TAG, "init ttad success")
                ThreadUtils.runOnUIThread {
                    DouYinSDK.getInstance().notifyTTAdLoadFinished()
                }
            }

            override fun fail(i: Int, s: String) {
                Log.d(TAG, "init ttad fail，code: $i, msg: $s ")
            }
        })
    }
}