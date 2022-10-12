package com.lollitech.mylibrary

import android.content.Context

/**
 * Copyright (c) 2022-10, lollitech
 * All rights reserved
 * Author: xuhuanli@lollitech.com
 */

interface ITopOnManager {
    fun initTopOn(context: Context, appId: String, appKey: String)

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