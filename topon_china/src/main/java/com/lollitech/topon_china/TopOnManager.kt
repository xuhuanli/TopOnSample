package com.lollitech.topon_china

import android.content.Context
import android.util.Log
import com.anythink.core.api.ATDebuggerConfig
import com.anythink.core.api.ATSDK
import com.anythink.network.gdt.GDTATConst.DEBUGGER_CONFIG.GDT_NETWORK
import com.anythink.network.ks.KSATConst.DEBUGGER_CONFIG.KS_NETWORK
import com.anythink.network.mintegral.MintegralATConst.DEBUGGER_CONFIG.Mintegral_NETWORK
import com.anythink.network.toutiao.TTATConst.DEBUGGER_CONFIG.TT_NETWORK
import com.lollitech.mylibrary.ITopOnManager
import com.lollitech.mylibrary.TopOnAdType

/**
 * Copyright (c) 2022-10, lollitech
 * All rights reserved
 * Author: xuhuanli@lollitech.com
 */

object TopOnManager : ITopOnManager {
    private val TAG = "TopOnManager"

    // placementId顺序 优量汇(腾讯)、快手、穿山甲、Mintegral中国
    private val idsMap = mapOf<TopOnAdType, List<String>>(
        TopOnAdType.AppOpen to listOf(
            "b62b03605066c1",
            "b62b02dd64934e",
            "b62b035f70b2ac",
            "b62b01702ad55b"
        ),
        TopOnAdType.Banner to listOf(
            "b62b03bbdd6cf5",
            "b62b03bacdcf28",
            "b5dd388839bf5e"
        ), // 快手没有Banner的广告placementId
        TopOnAdType.Interstitial to listOf(
            "b62b03987b081c",
            "b62b032a9446be",
            "b62b0397ba87a8",
            "b62b015a7ee3fb"
        ),
        TopOnAdType.Native to listOf(
            "b62ea5487275ef",
            "b62ea2a1ff0a02",
            "b62ea2e2ae729e",
            "b62ea4f546a3b8"
        ), // 自渲染原生广告
//        TopOnAdType.Native to listOf(
//            "b62ea546e02034",
//            "b62ea2a0843eb9",
//            "b62ea2e1248bfa",
//            "b62ea4f34d5b14"
//        ), // 模板渲染原生广告
        TopOnAdType.RewardVideo to listOf(
            "b62b0355867507",
            "b62b032b44e3e2",
            "b62b03c000844f",
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
            ATDebuggerConfig.Builder(GDT_NETWORK).build()
        )

        ATSDK.setDebuggerConfig(
            context,
            deviceId,
            ATDebuggerConfig.Builder(TT_NETWORK).build()
        )

        ATSDK.setDebuggerConfig(
            context,
            deviceId,
            ATDebuggerConfig.Builder(Mintegral_NETWORK).build()
        )

        ATSDK.setDebuggerConfig(
            context,
            deviceId,
            ATDebuggerConfig.Builder(KS_NETWORK).build()
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