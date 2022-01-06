package iooojik.anon.meet

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AdUtil {
    companion object {
        private var mInterstitialAd: InterstitialAd? = null

        fun initializeAd(context: Context) {
            MobileAds.initialize(context) {

            }
        }

        fun loadInterstitialAd(activity: Activity, show: Boolean = false) {
            InterstitialAd.load(
                activity.applicationContext,
                activity.applicationContext.resources.getString(R.string.interstitial_id),
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        log(adError.message)
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        log("onAdLoaded")
                        mInterstitialAd = interstitialAd
                        if (show)
                            showInterstitialAd(activity)
                    }
                })
        }

        fun showInterstitialAd(activity: Activity) {
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(activity)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
        }
    }
}

