package com.lollitech.topon_google

import android.content.Context
import android.util.Log
import com.anythink.core.api.ATDebuggerConfig
import com.anythink.core.api.ATSDK
import com.anythink.network.admob.AdmobATConst
import com.anythink.network.facebook.FacebookATConst
import com.anythink.network.mintegral.MintegralATConst
import com.anythink.network.pangle.PangleATConst
import com.lollitech.mylibrary.ITopOnManager
import com.lollitech.mylibrary.TopOnAdType

/**
 * Copyright (c) 2022-10, lollitech
 * All rights reserved
 * Author: xuhuanli@lollitech.com
 */

object TopOnManager : ITopOnManager {
    private val TAG = "TopOnManager"

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

    override fun initTopOn(context: Context, appId: String, appKey: String) {
        ATSDK.init(context, appId, appKey)
    }

    override fun setTopOnDebugConfig(context: Context, deviceId: String) {
        ATSDK.setDebuggerConfig(
            context,
            deviceId,
            ATDebuggerConfig.Builder(FacebookATConst.DEBUGGER_CONFIG.Facebook_NETWORK).build()
        )

        ATSDK.setDebuggerConfig(
            context,
            deviceId,
            ATDebuggerConfig.Builder(AdmobATConst.DEBUGGER_CONFIG.Admob_NETWORK).build()
        )

        ATSDK.setDebuggerConfig(
            context,
            deviceId,
            ATDebuggerConfig.Builder(MintegralATConst.DEBUGGER_CONFIG.Mintegral_NETWORK).build()
        )

        ATSDK.setDebuggerConfig(
            context,
            deviceId,
            ATDebuggerConfig.Builder(PangleATConst.DEBUGGER_CONFIG.Pangle_NETWORK).build()
        )
    }

    override fun enableDebug(context: Context) {
        // 使用调试模式验证广告平台的接入 打开SDK的日志功能  debugtool里添加一个开关
        ATSDK.setNetworkLogDebug(true);//应用上线前须关闭

        ATSDK.integrationChecking(context);//注意：不要在提交上架审核的包中带上此API，避免影响上架

        ATSDK.testModeDeviceInfo(context) { deviceInfo ->
            Log.d(TAG, "deviceInfo: $deviceInfo")
        }
    }

    override fun getPlacementIds(type: TopOnAdType) = idsMap[type]!!

}