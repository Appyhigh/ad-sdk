# Ad SDK

## Introduction
An ad util library to facilitate easy and standardised implementation of AdMob SDK.
## Proguard Rules
```
-keep class io.jsonwebtoken.*.* { *; }
-keepnames class io.jsonwebtoken.* { *; }
-keepnames interface io.jsonwebtoken.* { *; }
```
## Initialization
Add it in your root `build.gradle` at the end of repositories:
```groovy
allprojects {  
 repositories {
  ...
  maven { url 'https://jitpack.io' } 
  }
}
 ```
In your app level `build.gradle` file add :
```groovy
dependencies {  
 implementation 'com.github.Appyhigh:ad-sdk.x.x'
}
 ```

Add these configurations to you `AndroidManifest.xml`
```xml
<manifest> 
  ... 
      <application>
          ...
            <meta-data 
            android:name="com.google.android.gms.ads.APPLICATION_ID"  
            android:value="ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX" />

            <meta-data  
            android:name="com.google.android.gms.ads.flag.OPTIMIZE_INITIALIZATION"
            android:value="true"/>

            <meta-data 
            android:name="com.google.android.gms.ads.flag.OPTIMIZE_AD_LOADING"
            android:value="true"/>
      </application>  
</manifest>
 ```

## Handle Consent for EU Countries [(Ref)](https://developers.google.com/admob/android/privacy)

Call getConsentForEU method to automatically handle the consent and forms inside the current activity. This method provides a callback with success and failure listener, handle them in your app.

For NON-EU countries / when a consent form is not available(i.e. if the user has already accepted the consent) onSuccess() method is automatically called.

_Debugging : Pass your test device hashed id, after getting it from logcat first (filter: UserMessagingPlatform)_ 

```kotlin
AdSdk.getConsentForEU( YOUR ACTIVITY , YOUR TEST DEVICE HASHED ID string, object : ConsentRequestListener{
    override fun onError(message: String, code: Int) {
       //Give the user a prompt or call initialize anyway
    }

    override fun onSuccess() {
	//Call initialize method now for AdSdk
    }
})
```
## SDK Initialization

```kotlin
/**  
 * @param application -> Pass your application context here  
 * @param testDevice -> Pass the test device id here. Eg: 59106BA0F480E2EC4CD8CC7AA2C49B81  
 * @param fileId -> Pass the local response file reference here. (R.raw.ad_utils_response)  
 * @param adInitializeListener -> register the adInitializeLister here to listen to success/failure of initialization.  
 */
fun initialize(  
    application: Application,  
    testDevice: String?,  
    fileId: Int,  
    adInitializeListener: AdInitializeListener  
)
```
### Example Implementation:
```kotlin
AdSdk.initialize(  
  application = application,  
  testDevice = "59106BA0F480E2EC4CD8CC7AA2C49B81", 
  fileId = R.raw.ad_utils_response,  
  adInitializeListener = object : AdInitializeListener() {  
      override fun onSdkInitialized() {  
      }  

      override fun onInitializationFailed(adSdkError: AdSdkError) {  
      }  
  }
)
 ```
> **Note:** By default `logging` is on for debug version. In case you want to turn it on for release version as well use :
```kotlin
AdSdkConstants.enableReleaseLogging = true
```
## Check If SDK is Initialized
```kotlin
fun isSdkInitialized() = isInitialized
```

## Setup Version Control
> **Note:** Make sure you do it only after you have successfully initialized the SDK.
``` kotlin
/**  
 * @param activity -> Pass the parent activity in which you want to configure Version Control  
 * @param view -> Pass any view to support SnackBar inflation  
 * @param buildVersion -> Pass the BuildConfig.VERSION_CODE here  
 * @param versionControlListener -> register VersionControlListener to listen to the type of update available  
 */
 fun setUpVersionControl(  
    activity: Activity,  
    view: View,  
    buildVersion: Int,  
    versionControlListener: VersionControlListener?  
)
```
### Example Implementation:
```kotlin
AdSdk.setUpVersionControl(  
    activity = this@MainActivity,  
    view = findViewById(R.id.tvDummyView),  
    buildVersion = BuildConfig.VERSION_CODE,  
    versionControlListener = object : VersionControlListener() {  
        override fun onUpdateDetectionSuccess(updateType: UpdateType) {  
            when (updateType) {  
                UpdateType.SOFT_UPDATE -> {  
                }  
                UpdateType.HARD_UPDATE -> {  
                }  
                else -> {}  
            }  
        }  
    }  
)
```

## Preload Banner Ad
```kotlin
AdSdk.preloadAd(  
    this@MainActivity,  
    adName = "test_banner"  
)
```
## Load Banner Ad
```kotlin
AdSdk.loadAd(  
    context = this,  
    parentView = findViewById(R.id.llAdView),  
    adName = "test_banner"  
)
```
## Preload Native Ad
 ```kotlin
AdSdk.preloadAd(  
    context = this@MainActivity,  
    adName = "test_native_ad"  
)
 ```
 
## Load Native Ad
 ```kotlin
AdSdk.loadAd(  
    context = this,  
    parentView = findViewById(R.id.llAdView),  
    adName = "test_native_ad"  
)
```
> **Note:** Optional callback methods can be registered in case you want to listen to them.
```kotlin
nativeAdLoadListener = object : NativeAdLoadListener() {
	override fun onAdClicked() {  
	    super.onAdClicked()  
	}  
	  
	override fun onAdClosed() {  
	    super.onAdClosed()  
	}  
	  
	override fun onAdFailedToLoad(p0: LoadAdError) {  
	    super.onAdFailedToLoad(p0)  
	}  
	  
	override fun onAdImpression() {  
	    super.onAdImpression()  
	}  
	  
	override fun onAdInflated() {  
	    super.onAdInflated()  
	}  
	  
	override fun onAdLoaded(nativeAd: NativeAd?) {  
	    super.onAdLoaded(nativeAd)  
	}  
	  
	override fun onAdOpened() {  
	    super.onAdOpened()  
	}  
	  
	override fun onAdSwipeGestureClicked() {  
	    super.onAdSwipeGestureClicked()  
	}  
	  
	override fun onMultipleAdsLoaded(nativeAds: ArrayList<NativeAd?>) {  
	    super.onMultipleAdsLoaded(nativeAds)  
	}
}
 ```

## Fetch a Native Ad object for later use OR Loading Custom Layout
```kotlin
/**  
 * @param context -> Pass the context here  
 * @param parentView -> Pass the parent view here in which you want to load the ad  
 * @param adName -> Pass the adName here which should match exactly with dashboard name  
 * @param nativeAdLoadListener -> Register NativeAdLoadListener to listen to related callbacks  
 */
 fun fetchNativeAd(  
    context: Context,  
    parentView: ViewGroup? = null,  
    adName: String,  
    nativeAdLoadListener: NativeAdLoadListener  
)
```
### Example Implementation
```kotlin
AdSdk.fetchNativeAd(  
    context = this,  
    parentView = findViewById(R.id.llAdView),  
    adName = "test_native_ad",  
    nativeAdLoadListener = object : NativeAdLoadListener() {  
       override fun onAdClicked() {  
          super.onAdClicked()  
      }  

      override fun onAdClosed() {  
          super.onAdClosed()  
      }  

      override fun onAdFailedToLoad(p0: LoadAdError) {  
          super.onAdFailedToLoad(p0)  
      }  

      override fun onAdImpression() {  
          super.onAdImpression()  
      }  

      override fun onAdInflated() {  
          super.onAdInflated()  
      }  

      override fun onAdLoaded(nativeAd: NativeAd?) {  
          super.onAdLoaded(nativeAd)  
      }  

      override fun onAdOpened() {  
          super.onAdOpened()  
      }  

      override fun onAdSwipeGestureClicked() {  
          super.onAdSwipeGestureClicked()  
      }  

      override fun onMultipleAdsLoaded(nativeAds: ArrayList<NativeAd?>) {  
          super.onMultipleAdsLoaded(nativeAds)  
      }
    }  
)
```
## Fetch Multiple Native Ad objects for later use
> **Note:** adsRequested should be a number between 1 to 5.
```kotlin
/**  
 * @param context -> Pass the context here  
 * @param parentView -> Pass the parent view here in which you want to load the ad  
 * @param adName -> Pass the adName here which should match exactly with dashboard name  
 * @param adsRequested -> Pass the number of ad objects[Between 1 to 5] needed from a single ad unit.  
 * @param nativeAdLoadListener -> Register NativeAdLoadListener to listen to related callbacks  
 **/
 fun fetchNativeAds(  
    context: Context,  
    parentView: ViewGroup? = null,  
    adName: String,
    adsRequested: Int,  
    nativeAdLoadListener: NativeAdLoadListener  
)
```
### Example Implementation
```kotlin
AdSdk.fetchNativeAds( 
      context = this,  
      parentView = findViewById(R.id.llAdView),  
      adName = "test_native_ad",  
      adsRequested = 5,
      nativeAdLoadListener = object : NativeAdLoadListener() {  
         override fun onAdClicked() {  
            super.onAdClicked()  
        }  

        override fun onAdClosed() {  
            super.onAdClosed()  
        }  

        override fun onAdFailedToLoad(p0: LoadAdError) {  
            super.onAdFailedToLoad(p0)  
        }  

        override fun onAdImpression() {  
            super.onAdImpression()  
        }  

        override fun onAdInflated() {  
            super.onAdInflated()  
        }  

        override fun onAdLoaded(nativeAd: NativeAd?) {  
            super.onAdLoaded(nativeAd)  
        }  

        override fun onAdOpened() {  
            super.onAdOpened()  
        }  

        override fun onAdSwipeGestureClicked() {  
            super.onAdSwipeGestureClicked()  
        }  

        override fun onMultipleAdsLoaded(nativeAds: ArrayList<NativeAd?>) {  
            super.onMultipleAdsLoaded(nativeAds)  
        }
      }  
)
```
## Load Interstitial Ad
```kotlin
AdSdk.loadAd(  
    context = this,  
    adName = "test_interstitial",  
      interstitialAdLoadListener = object : InterstitialAdLoadListener() {  
        override fun onAdFailedToLoad(adErrors: List<String>) {  
          //This is fired only if primary, secondary and fallback all 3 Ids fail.  
        }  

        override fun onAdLoaded(interstitialAd: InterstitialAd) {
          //This is fired when ad is loaded.    
        }  
      }  
)
```
### Show Interstitial Ad
```kotlin
interstitialAd.show(this@MainActivity)
```
### Set the FullScreenContentCallback for Interstitial Ad
```kotlin
interstitialAd.fullScreenContentCallback = object: FullScreenContentCallback() {  
      override fun onAdClicked() {
        // Called when a click is recorded for an ad.   
      }  

      override fun onAdDismissedFullScreenContent() {
        // Called when ad is dismissed.  
      }  

      override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
        // Called when ad fails to show.   
      }  

      override fun onAdImpression() {
        // Called when an impression is recorded for an ad. 
      }  

      override fun onAdShowedFullScreenContent() {
        // Called when ad is shown.  
      }  
}
```
## Load Rewarded Ad
```kotlin
AdSdk.loadAd(  
    context = this,  
    adName = "test_rewarded",  
    rewardedAdLoadListener = object :RewardedAdLoadListener(){  
        override fun onAdFailedToLoad(adErrors: List<String>) {
	        //This is fired only if primary, secondary and fallback all 3 Ids fail.
        }  
  
        override fun onAdLoaded(rewardedAd: RewardedAd) {
	        //This is fired when ad is loaded. 
        }  
    }  
)
```
### Show Rewarded Ad
```kotlin
rewardedAd.show(this@MainActivity) {  
    fun onUserEarnedReward(rewardItem: RewardItem) {  
        var rewardAmount = rewardItem.amount  
        var rewardType = rewardItem.type  
    }  
} 
```
### Set the FullScreenContentCallback for Rewarded Ad
```kotlin
rewardedAd.fullScreenContentCallback = object: FullScreenContentCallback() {  
    override fun onAdClicked() {  
        // Called when a click is recorded for an ad.  
    }  
  
    override fun onAdDismissedFullScreenContent() {  
        // Called when ad is dismissed.  
		// Set the ad reference to null so you don't show the ad a second time.
    }  
  
    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {  
        // Called when ad fails to show.
    }  
  
    override fun onAdImpression() {  
        // Called when an impression is recorded for an ad.  
    }  
  
    override fun onAdShowedFullScreenContent() {  
        // Called when ad is shown.
    }  
}
```
## Load Rewarded Interstitial Ad
```kotlin
AdSdk.loadAd(  
    context = this,  
  adName = "test_rewarded_interstitial",  
  rewardedInterstitialAdLoadListener = object : RewardedInterstitialAdLoadListener() {  
        override fun onAdFailedToLoad(adErrors: List<String>) {
	        //This is fired only if primary, secondary and fallback all 3 Ids fail.  
        }  
  
        override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
	        //This is fired when ad is loaded.   
        }  
    }  
)
```
### Show Rewarded Interstitial Ad
```kotlin
rewardedInterstitialAd.show(this@MainActivity) {  
  fun onUserEarnedReward(rewardItem: RewardItem) {  
        var rewardAmount = rewardItem.amount  
        var rewardType = rewardItem.type    
    }  
}
```

### Set the FullScreenContentCallback for Rewarded Interstitial Ad
```kotlin
rewardedInterstitialAd.fullScreenContentCallback = object: FullScreenContentCallback() {  
    override fun onAdClicked() {  
        // Called when a click is recorded for an ad.  
    }  
  
    override fun onAdDismissedFullScreenContent() {  
        // Called when ad is dismissed.  
		// Set the ad reference to null so you don't show the ad a second time.
    }  
  
    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {  
        // Called when ad fails to show.
    }  
  
    override fun onAdImpression() {  
        // Called when an impression is recorded for an ad.  
    }  
  
    override fun onAdShowedFullScreenContent() {  
        // Called when ad is shown.
    }  
}
```
## Load AppOpen Ad (Single Time)
```kotlin
AdSdk.loadAd(  
    application = application,  
    context = this,  
    adName = "test_app_open",  
    appOpenLoadType = AppOpenLoadType.SINGLE_LOAD,  
      appOpenAdLoadListener = object : AppOpenAdLoadListener() {  
          override fun onInitSuccess(manager: AppOpenAdManager?) {  
          super.onInitSuccess(manager)  
      }  
      override fun onAdLoaded(ad: AppOpenAd) {  
          super.onAdLoaded(ad)  
          ad.show(this@MainActivity)  
      }  

      override fun onAdClosed() {  
          super.onAdClosed()  
      }  

      override fun onAdFailedToLoad(loadAdError: List<String>) {  
          super.onAdFailedToLoad(loadAdError)  
      }  

      override fun onAdFailedToShow(adError: AdError) {  
          super.onAdFailedToShow(adError)  
      }  

      override fun onContextFailed() {  
          super.onContextFailed()  
      }  
    }  
)
```

## Load AppOpen Ad (Background to Foreground)
```kotlin
AdSdk.loadAd(  
    application = application,  
    context = this,  
    adName = "test_app_open",  
    appOpenLoadType = AppOpenLoadType.BACKGROUND_TO_FOREGROUND  
)
```
