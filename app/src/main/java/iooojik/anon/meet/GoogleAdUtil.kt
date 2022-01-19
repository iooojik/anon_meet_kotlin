package iooojik.anon.meet

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class GoogleAdUtil {
    companion object {
        //полностраничное объявление
        private var mInterstitialAd: InterstitialAd? = null

        fun initializeAd(context: Context) {
            //инициализация рекламы
            MobileAds.initialize(context) {
                loadInterstitialAd(context)
            }
        }

        private fun loadInterstitialAd(context: Context) {
            InterstitialAd.load(
                context,
                context.resources.getString(R.string.interstitial_id),
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        log(adError.message, priority = Log.ERROR)
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        log("onAdLoaded", priority = Log.INFO)
                        mInterstitialAd = interstitialAd
                    }
                })
        }

        fun showInterstitialAd(activity: Activity) {
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(activity)
            } else {
                log("The interstitial ad wasn't ready yet.", priority = Log.DEBUG)
                loadInterstitialAd(activity.applicationContext)
            }
        }
    }
}

