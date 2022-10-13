package com.lollitech.topon_google

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.anythink.banner.api.ATBannerListener
import com.anythink.banner.api.ATBannerView
import com.anythink.core.api.*
import com.anythink.interstitial.api.ATInterstitialAutoEventListener
import com.anythink.network.admob.AdmobATConst
import com.anythink.network.facebook.FacebookATConst
import com.anythink.network.mintegral.MintegralATConst
import com.anythink.network.pangle.PangleATConst
import com.anythink.rewardvideo.api.ATRewardVideoAutoAd
import com.anythink.rewardvideo.api.ATRewardVideoAutoEventListener
import com.anythink.rewardvideo.api.ATRewardVideoAutoLoadListener
import com.lollitech.mylibrary.ITopOnManager
import com.lollitech.mylibrary.TopOnAdType
import java.util.concurrent.ArrayBlockingQueue

/**
 * Copyright (c) 2022-10, lollitech
 * All rights reserved
 * Author: xuhuanli@lollitech.com
 */

object TopOnManager : ITopOnManager {
    private const val TAG = "TopOnManager"

    // placementId顺序 admob、meta(Facebook)、Pangle、Mintegral
    val idsMap = mapOf<TopOnAdType, List<String>>(
        TopOnAdType.AppOpen to listOf(
            "b62b028dcba917",
            "b62b023453afba",
            "b62b01702ad55b"
        ), // meta(Facebook)没有开屏广告placementId
        TopOnAdType.Banner to listOf(
            "b62b03bca998e3",
            "b62b038fdb69f0",
            "b62b023625cf59",
            "b5dd388839bf5e"
        ),
        TopOnAdType.Interstitial to listOf(
            "b62b03b9a8bff9",
            "b62b03879ec4ef",
            "b62b023555ba04",
            "b62b015a7ee3fb"
        ),
        TopOnAdType.Native to listOf(
            "b62b03e2b6c205",
            "b62ebb0af4011b",
            "b62b023b30350d",
            "b62ea4f546a3b8"
        ), // 自渲染原生广告
//        TopOnAdType.Native to listOf(
//            "b62ea4a37114a4",
//            "b62ea4f34d5b14"
//        ), // 模板渲染原生广告 admob、Pangle没有开屏广告placementId
        TopOnAdType.RewardVideo to listOf(
            "b62b03da735701",
            "b62b01b55f0d95",
            "b62b023704d609",
            "b62b015d8b88b0"
        ),
    )

    // Banner横幅加载回调
    private val bannerEventListener = object : ATBannerListener {
        override fun onBannerLoaded() {
            Log.d(TAG, "onBannerLoaded: success")
        }

        override fun onBannerFailed(error: AdError?) {
            Log.e(TAG, "onBannerFailed: ${error?.fullErrorInfo}")
        }

        override fun onBannerClicked(info: ATAdInfo?) {
            Log.d(TAG, "onBannerClicked: $info")
        }

        override fun onBannerShow(info: ATAdInfo?) {
            Log.d(TAG, "onBannerShow: $info")
        }

        override fun onBannerClose(info: ATAdInfo?) {
            Log.d(TAG, "onBannerClose: $info")
        }

        override fun onBannerAutoRefreshed(info: ATAdInfo?) {
            Log.d(TAG, "onBannerAutoRefreshed: $info")
        }

        override fun onBannerAutoRefreshFail(error: AdError?) {
            Log.e(TAG, "onBannerAutoRefreshFail: ${error?.fullErrorInfo}")
        }
    }

    private val autoInterstitialAdEventListener = object : ATInterstitialAutoEventListener() {
        override fun onInterstitialAdClicked(info: ATAdInfo?) {
            Log.d(TAG, "onInterstitialAdClicked: $info")
        }

        override fun onInterstitialAdShow(info: ATAdInfo?) {
            Log.d(TAG, "onInterstitialAdClicked: $info")
        }

        override fun onInterstitialAdClose(info: ATAdInfo?) {
            Log.d(TAG, "onInterstitialAdClose: $info")
        }

        override fun onInterstitialAdVideoStart(info: ATAdInfo?) {
            Log.d(TAG, "onInterstitialAdVideoStart: $info")
        }

        override fun onInterstitialAdVideoEnd(info: ATAdInfo?) {
            Log.d(TAG, "onInterstitialAdVideoEnd: $info")
        }

        override fun onInterstitialAdVideoError(info: AdError?) {
            Log.e(TAG, "onInterstitialAdVideoError: $info")
        }

    }

    // 加载成功的广告位id
    private val mRewardVideoQueue = ArrayBlockingQueue<String>(10, false)

    // 激励视频加载回调
    private val autoRewardVideoAdEventListener = object : ATRewardVideoAutoEventListener() {
        override fun onRewardedVideoAdPlayStart(info: ATAdInfo?) {
            //ATAdInfo可区分广告平台以及获取广告平台的广告位ID等
            //请参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_sdk_callback_access?id=callback_info

            //建议在此回调中调用load进行广告的加载，方便下一次广告的展示（不需要调用isAdReady()）
            Log.d(TAG, "onRewardedVideoAdLoaded: $info")
        }

        override fun onRewardedVideoAdPlayEnd(info: ATAdInfo?) {
            Log.d(TAG, "onRewardedVideoAdPlayEnd: $info")
        }

        override fun onRewardedVideoAdPlayFailed(error: AdError?, info: ATAdInfo?) {
            Log.e(TAG, "onRewardedVideoAdPlayFailed: error = ${error?.fullErrorInfo} info = $info")
        }

        override fun onRewardedVideoAdClosed(info: ATAdInfo?) {
            Log.d(TAG, "onRewardedVideoAdClosed: $info")
        }

        override fun onRewardedVideoAdPlayClicked(info: ATAdInfo?) {
            Log.d(TAG, "onRewardedVideoAdPlayClicked: $info")
        }

        override fun onReward(info: ATAdInfo?) {
            Log.d(TAG, "onReward: $info")
        }

    }

    override fun initTopOn(context: Context, appId: String, appKey: String) {
        ATSDK.init(context, appId, appKey)
    }

    override fun setTopOnDebugConfig(context: Context, deviceId: String) {
        ATSDK.setDebuggerConfig(
            context,
            deviceId,
            ATDebuggerConfig.Builder(AdmobATConst.DEBUGGER_CONFIG.Admob_NETWORK).build()
        )

//        ATSDK.setDebuggerConfig(
//            context,
//            deviceId,
//            ATDebuggerConfig.Builder(FacebookATConst.DEBUGGER_CONFIG.Facebook_NETWORK).build()
//        )

//        ATSDK.setDebuggerConfig(
//            context,
//            deviceId,
//            ATDebuggerConfig.Builder(MintegralATConst.DEBUGGER_CONFIG.Mintegral_NETWORK).build()
//        )

//        ATSDK.setDebuggerConfig(
//            context,
//            deviceId,
//            ATDebuggerConfig.Builder(PangleATConst.DEBUGGER_CONFIG.Pangle_NETWORK).build()
//        )
    }

    override fun enableDebug(context: Context) {
        // 使用调试模式验证广告平台的接入 打开SDK的日志功能  debugtool里添加一个开关
        ATSDK.setNetworkLogDebug(true)//应用上线前须关闭

        ATSDK.integrationChecking(context)//注意：不要在提交上架审核的包中带上此API，避免影响上架

        ATSDK.testModeDeviceInfo(context) { deviceInfo ->
            Log.d(TAG, "deviceInfo: $deviceInfo")
        }
    }

    override fun getPlacementIds(type: TopOnAdType) = idsMap[type]!!

    // ===== Banner横幅广告 =====
    fun loadAndShowBannerAd(adView: ATBannerView, placementId: String? = null) {
        if (placementId != null) {
            adView.setPlacementId(placementId)
        } else {
            adView.setPlacementId(getPlacementIds(TopOnAdType.Banner).first())
        }
        val width = adView.context.resources.displayMetrics.widthPixels //定一个宽度值，比如屏幕宽度
        //TODO 1.4.0 xhl 必须跟TopOn后台配置的Banner广告源宽高比例一致，假设尺寸为320x50
        val height = (width / (320 / 50f)).toInt() //按照比例转换高度的值

        adView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            this.width = width
            this.height = height
        }

        val localMap: MutableMap<String, Any> = HashMap()
        localMap[ATAdConst.KEY.AD_WIDTH] = width
        localMap[ATAdConst.KEY.AD_HEIGHT] = height
        adView.setLocalExtra(localMap)
        // Banner的load是异步执行的 不需要在listener Load成功后调用loadAd
        adView.setBannerAdListener(bannerEventListener)
        adView.loadAd()
    }
    // ===== Banner横幅广告 =====

    // ===== 激励视频全自动加载 =====
    fun loadRewardVideoAuto(context: Context) {
        ATRewardVideoAutoAd.init(context,
            getPlacementIds(TopOnAdType.RewardVideo).toTypedArray(),
            object : ATRewardVideoAutoLoadListener {
                override fun onRewardVideoAutoLoaded(placementId: String) {
                    Log.d(TAG, "onRewardVideoAutoLoaded: $placementId")
                    mRewardVideoQueue.offer(placementId)
                }

                override fun onRewardVideoAutoLoadFail(placementId: String?, error: AdError?) {
                    Log.e(
                        TAG,
                        "onRewardVideoAutoLoadFail: $placementId ${error?.fullErrorInfo}"
                    )
                }

            })
    }

    /**
     * 获取一个激励视频有效的placementId
     */
    private fun getRewardVideoValidId() = mRewardVideoQueue.poll()

    /**
     * 激励视频是否准备好
     */
    fun isRewardVideoReady() = mRewardVideoQueue.isNotEmpty()

    /**
     * 显示激励视频广告
     * @param activity
     * @param isAutoFill true 会自动去拿一个有效的广告加载 false 手动加载 传入第三个参数
     * @param placementId
     */
    fun showRewardVideoAutoAd(
        activity: Activity,
        isAutoFill: Boolean = true,
        placementId: String? = null
    ) {
        if (isAutoFill) {
            val validId = getRewardVideoValidId()
            if (validId != null) {
                if (ATRewardVideoAutoAd.isAdReady(validId)) {
                    setRewardVideoAutoLocalExtra(validId)
                    ATRewardVideoAutoAd.show(activity, validId, autoRewardVideoAdEventListener)
                }
            }
        } else {
            if (placementId != null) {
                if (ATRewardVideoAutoAd.isAdReady(placementId)) {
                    setRewardVideoAutoLocalExtra(placementId)
                    ATRewardVideoAutoAd.show(activity, placementId, autoRewardVideoAdEventListener)
                }
            }
        }
    }

    fun setRewardVideoAutoLocalExtra(placementId: String) {
        val map = getPlacementIdLocalExtra(placementId)
        //从下一次的广告加载开始生效
        ATRewardVideoAutoAd.setLocalExtra(placementId, map)
    }
    // ===== 激励视频全自动加载 =====

    //设置服务器回调localExtra信息
    private fun getPlacementIdLocalExtra(placementId: String): MutableMap<String, Any> {
        val userid = "test_userid_001"
        val userdata = "test_userdata_001_" + placementId + "_" + System.currentTimeMillis()
        val localMap: MutableMap<String, Any> = HashMap()
        localMap[ATAdConst.KEY.USER_ID] = userid
        localMap[ATAdConst.KEY.USER_CUSTOM_DATA] = userdata
        return localMap
    }
}