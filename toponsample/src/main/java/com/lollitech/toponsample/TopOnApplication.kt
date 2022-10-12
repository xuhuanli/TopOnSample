package com.lollitech.toponsample

import android.app.Application
import android.os.Build
import android.util.Log
import android.webkit.WebView
import com.anythink.core.api.ATDebuggerConfig
import com.anythink.core.api.ATSDK
import com.anythink.network.admob.AdmobATConst.DEBUGGER_CONFIG.Admob_NETWORK
import com.anythink.network.applovin.ApplovinATConst.DEBUGGER_CONFIG.Applovin_NETWORK
import com.anythink.network.facebook.FacebookATConst.DEBUGGER_CONFIG.Facebook_NETWORK
import dagger.hilt.android.HiltAndroidApp


/**
 * Copyright (c) 2022-10, lollitech
 * All rights reserved
 * Author: xuhuanli@lollitech.com
 */

const val TOPON_DEMO_APPID = "a62b013be01931"
const val TOPON_DEMO_APPKEY = "c3d0d2a9a9d451b07e62b509659f7c97"
const val TOPON_DEMO_PACKAGENAME = "com.anythink.sdk.demo" // 包名（部分平台需要）

const val PIXEL2_ANDROID_ID = "4b1334678036a6c6"

@HiltAndroidApp
class TopOnApplication : Application() {
    private val TAG = "TopOnApplication"

    override fun onCreate() {
        super.onCreate()
        //Android 9及以上必须设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getProcessName()
            if (packageName != processName) {
                WebView.setDataDirectorySuffix(processName)
            }
        }

        enableDebug()

        // TopOn SDK INIT
        ATSDK.init(this, TOPON_DEMO_APPID, TOPON_DEMO_APPKEY)

        if (BuildConfig.DEBUG) {
            // 使用调试模式验证广告平台的接入 打开SDK的日志功能  debugtool里添加一个开关
            ATSDK.setNetworkLogDebug(true);//应用上线前须关闭

            ATSDK.integrationChecking(applicationContext);//注意：不要在提交上架审核的包中带上此API，避免影响上架

            ATSDK.testModeDeviceInfo(
                this
            ) { deviceInfo -> Log.d(TAG, "deviceInfo: $deviceInfo") }
        }

    }

    private fun enableDebug() {
        /**
         * 建议在TopOn SDK初始化前调用
         * 打开在AndroidStudio中的Logcat，以 "anythink|HeadBidding" 为TAG进行过滤，查看SDK的日志
         *
         * anythink: ********************************** UA_5.9.96 *************************************
         * anythink: GAID(ADID): b796a53f-61bf-4e91-bc67-d505cdb97cf8 , AndroidID: f669f2b7137d82b9
         * anythink: ********************************** UA_5.9.96 *************************************
         */
        ATSDK.setDebuggerConfig(
            this,
            PIXEL2_ANDROID_ID,
            ATDebuggerConfig.Builder(Facebook_NETWORK).build()
        )

        ATSDK.setDebuggerConfig(
            this,
            PIXEL2_ANDROID_ID,
            ATDebuggerConfig.Builder(Admob_NETWORK).build()
        )

        ATSDK.setDebuggerConfig(
            this,
            PIXEL2_ANDROID_ID,
            ATDebuggerConfig.Builder(Applovin_NETWORK).build()
        )
    }
}