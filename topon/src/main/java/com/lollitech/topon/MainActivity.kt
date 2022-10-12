package com.lollitech.topon

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anythink.banner.api.ATBannerListener
import com.anythink.core.api.ATAdConst
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitialAutoAd
import com.anythink.interstitial.api.ATInterstitialAutoEventListener
import com.anythink.interstitial.api.ATInterstitialAutoLoadListener
import com.anythink.rewardvideo.api.*
import com.lollitech.mylibrary.TopOnAdType
import com.lollitech.topon.databinding.ActivityMainBinding
import com.lollitech.topon_china.TopOnManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var mRewardVideoAd: ATRewardVideoAd
    private var isLoadingRewardVideo = false
    private var isLoadedRewardVideo = false

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBanner.setOnClickListener {
            binding.adBanner.setPlacementId(TopOnManager.getPlacementIds(TopOnAdType.Banner).first())
            binding.adBanner.setBannerAdListener(object : ATBannerListener {
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
            })
            binding.adBanner.loadAd()
        }

        binding.btnVideo.setOnClickListener {
            Toast.makeText(this, "推荐用激励视频全自动", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
            if (isLoadedRewardVideo) {
                showRewardVideoAd()
            } else if (!isLoadingRewardVideo) {
                loadRewardVideoAd()
            }
        }

        binding.btnVideoAuto.setOnClickListener {
            ATRewardVideoAutoAd.init(this,
                TopOnManager.getPlacementIds(TopOnAdType.RewardVideo).toTypedArray(),
                object : ATRewardVideoAutoLoadListener {
                    override fun onRewardVideoAutoLoaded(placementId: String) {
                        Log.d(TAG, "onRewardVideoAutoLoaded: $placementId")
                        showRewardVideoAutoAd(placementId)
                    }

                    override fun onRewardVideoAutoLoadFail(placementId: String?, error: AdError?) {
                        Log.e(
                            TAG,
                            "onRewardVideoAutoLoadFail: $placementId ${error?.fullErrorInfo}"
                        )
                    }

                })
        }

        binding.btnInterstitial.setOnClickListener {
            Toast.makeText(this, "推荐用插页全自动", Toast.LENGTH_SHORT).show()
        }

        binding.btnInterstitialAuto.setOnClickListener {
            ATInterstitialAutoAd.init(
                this,
                TopOnManager.getPlacementIds(TopOnAdType.Interstitial).toTypedArray(),
                object : ATInterstitialAutoLoadListener {
                    override fun onInterstitialAutoLoaded(placementId: String) {
                        Log.d(TAG, "onInterstitialAutoLoaded: $placementId")
                        showInterstitialAutoAd(placementId)
                    }

                    override fun onInterstitialAutoLoadFail(placementId: String?, error: AdError?) {
                        Log.e(
                            TAG,
                            "onInterstitialAutoLoadFail: $placementId ${error?.fullErrorInfo}"
                        )
                    }

                })
        }
    }

    // ===== 激励视频 =====

    private fun loadRewardVideoAd() {
        if (!this::mRewardVideoAd.isInitialized) {
            mRewardVideoAd = ATRewardVideoAd(this, TopOnManager.getPlacementIds(TopOnAdType.RewardVideo).first())
            mRewardVideoAd.setAdListener(object : ATRewardVideoListener {
                override fun onRewardedVideoAdLoaded() {
                    Log.d(TAG, "onRewardedVideoAdLoaded: success")
                }

                override fun onRewardedVideoAdFailed(error: AdError?) {
                    Log.e(TAG, "onRewardedVideoAdFailed: ${error?.fullErrorInfo}")
                }

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

            })
        }
        val userid = "test_userid_001"
        val userdata = "test_userdata_001"
        val localMap: MutableMap<String, Any> = HashMap()
        localMap[ATAdConst.KEY.USER_ID] = userid
        localMap[ATAdConst.KEY.USER_CUSTOM_DATA] = userdata
        mRewardVideoAd.setLocalExtra(localMap)
        mRewardVideoAd.load()
    }

    private fun showRewardVideoAd() {
        if (this::mRewardVideoAd.isInitialized && mRewardVideoAd.isAdReady) {
            mRewardVideoAd.show(this)
        }
    }

    // ===== 激励视频 =====


    // ===== 激励视频全自动加载 =====
    private fun showRewardVideoAutoAd(placement: String) {
        if (ATRewardVideoAutoAd.isAdReady(placement)) {
            ATRewardVideoAutoAd.show(this, placement, autoRewardVideoAdEventListener)
        }
    }

    private fun setRewardVideoAutoLocalExtra(placementId: String) {
        val map = getPlacementIdLocalExtra(placementId)
        //从下一次的广告加载开始生效
        ATRewardVideoAutoAd.setLocalExtra(placementId, map)
    }

    // ===== 激励视频全自动加载 =====

    // ===== 插页广告全自动加载 =====
    private fun showInterstitialAutoAd(placement: String) {
        if (ATInterstitialAutoAd.isAdReady(placement)) {
            ATInterstitialAutoAd.show(this, placement, autoInterstitialAdEventListener)
        }
    }

    private fun setInterstitialLocalExtra(placementId: String) {
        val map = getPlacementIdLocalExtra(placementId)
        //从下一次的广告加载开始生效
        ATInterstitialAutoAd.setLocalExtra(placementId, map)
    }

    // ===== 插页广告全自动加载 =====

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