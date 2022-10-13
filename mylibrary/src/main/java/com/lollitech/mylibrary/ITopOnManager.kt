package com.lollitech.mylibrary

import android.content.Context

/**
 * Copyright (c) 2022-10, lollitech
 * All rights reserved
 * Author: xuhuanli@lollitech.com
 */

interface ITopOnManager {
    fun initTopOn(context: Context, appId: String, appKey: String)

    /**
     * 打开调试模式后，测试设备上的所有广告位都只会请求调试模式中指定的广告平台。
     * 一次只能配置一个平台
     */
    fun setTopOnDebugConfig(context: Context, deviceId: String)

    fun enableDebug(context: Context, )

    /**
     * 获取广告id
     *
     * @param type 广告类型
     * @return 各广告商的广告id
     */
    fun getPlacementIds(type: TopOnAdType): List<String>
}