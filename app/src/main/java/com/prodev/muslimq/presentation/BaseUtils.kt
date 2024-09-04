package com.prodev.muslimq.presentation

import android.util.DisplayMetrics
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.prodev.muslimq.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseUtils : AppCompatActivity() {
    private var adView: AdManagerAdView? =null
    private var mInterstitialAd: InterstitialAd? = null
    var id: String? = null
    private var isLoad = false


    fun setupInterstitial() {

        val unitIdInterstitial: String
        if(BuildConfig.DEBUG){
            unitIdInterstitial = "ca-app-pub-3940256099942544/1033173712" //debug version
        }else{
            unitIdInterstitial = "ca-app-pub-7025020357054894/2664543660" // prod Version
        }
        CoroutineScope(Dispatchers.Main).launch {

            try {
                val adRequest = AdRequest.Builder().build()
                InterstitialAd.load(this@BaseUtils, unitIdInterstitial, adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            mInterstitialAd = interstitialAd
                            isLoad = true
                            Log.d("success","AdMob Inters Loaded Success")

                            // Set the FullScreenContentCallback
                            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    // Handle the ad dismissed event
                                    Log.d("success","AdMob Inters Ad Dismissed")
                                    // Load a new interstitial ad
                                    mInterstitialAd = null
                                    setupInterstitial()
                                }

                                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                    // Handle the ad failed to show event
                                    Log.d("success","AdMob Inters Ad Failed to Show: ${adError.message}")

                                }

                                override fun onAdShowedFullScreenContent() {
                                    // Handle the ad showed event
                                    Log.d("success","AdMob Inters Ad Showed")
                                    mInterstitialAd = null // Reset the interstitial ad
                                }
                            }
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            mInterstitialAd = null
                            isLoad = false
                        }
                    })
            } catch (e: Exception) {
                Log.d("failed","asywalul inters :${e.message}")
            }
        }
    }


    fun loadBanner(adViewContainer: FrameLayout) {
        try {

            var unitIdBanner = ""
            if(BuildConfig.DEBUG){
                unitIdBanner = "ca-app-pub-3940256099942544/9214589741" //dev version
            }else{
                unitIdBanner = "ca-app-pub-7025020357054894/3892367996"  //prod version
            }
            adView?.adUnitId = unitIdBanner
            adView?.setAdSizes(getSize(adViewContainer))
            val adRequest = AdManagerAdRequest.Builder().build()
            adView?.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.d("AdMob", "Ad loaded successfully unit = $id")
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    Log.d("AdMob", "Ad failed to load:"+ p0.message + "id = "+id)
                }

                override fun onAdOpened() {
                    Log.d("AdMob", "Ad opened")
                }

                override fun onAdClicked() {
                    Log.d("AdMob", "Ad clicked")
                }

                override fun onAdClosed() {
                    Log.d("AdMob", "Ad closed")
                }
            }
            adView?.loadAd(adRequest)
        }catch (e : Exception){
            Log.d("failed",e.message.toString())
        }
    }

    private fun getSize(adViewContainer: FrameLayout): AdSize {
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = outMetrics.density

        var adWidthPixels = adViewContainer.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }

    fun showInterstitial(){
        try {
            Log.d("showInters","execute")
            if(isLoad){
                Log.d("showIntersAdmob","true")
                mInterstitialAd?.show(this)
            }
        }catch (e : Exception){
            Log.d("showInters","false")
        }
    }
}