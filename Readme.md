
# Ad SDK

## Introduction
An Ad Util library to facilitate easy and standardised implementation of AdMob/AdManager/AppLovin SDK.

## Dashboard Setup
`Step 1:` Open https://admobdash-v2.apyhi.com/

`Step 2:` Login using your google account.

`Step 3:` Register a new App, If already registered skip this step.

- AppName : Your App Name (Eg: Adutils)
- PackageID: Your package ID (Eg: example.appyhigh.adutils)
- Platform : Only Android Supported yet
- Latest Version : Your latest app version here (Eg: 101)
-  Critical Version : Your critical app version here below which you don't want your users to use the app. (Eg: 100)
<img src="https://github.com/Appyhigh/ad-sdk/blob/main/screenshots/register.png" style ="width:400px; height:auto;" alt="register"/>

`Step 4:` Open the App from the list and add your ad placements.

- ad_name -> This name should `match exactly` with the Ad placement name in your code.
- primary_ids -> List of `;` seperated ad placement ids.
- ad_type -> Type of ad you want your ad placement to be (Eg: Native, Banner, etc). This cannot be changed once its set.
- secondary_ids -> List of `;` seperated ad placement ids. This is used in case primary id fails to load.
- refresh_rate_ms -> This parameter has to be a value in `milliseconds` and is valid just for `Banner` and `Native` Ad Placements. This will refresh you Ad after the set time to fetch a new Ad.
- color_hex -> This parameter is only valid for Native Ad. This parameter is responsible for the CTA color of the Ad in Light Mode.
- color_hex_dark -> This parameter is only valid for Native Ad. This parameter is responsible for the CTA color of the Ad in Dark Mode.
- text_color -> This parameter is only valid for Native Ad. This parameter is responsible for the Text color of the Ad in Light Mode.
- text_color_dark -> This parameter is only valid for Native Ad. This parameter is responsible for the Text color of the Ad in Dark Mode.
- bg_color -> This parameter is only valid for Native Ad. This parameter is responsible for the Background color of the Ad in Light Mode.
- bg_color_dark -> This parameter is only valid for Native Ad. This parameter is responsible for the Background color of the Ad in Dark Mode.
- size -> This parameter is only valid for Banner and Native to set the desired Ad Size.
- primary_adload_timeout_ms -> This parameter has to be a value in `milliseconds` and Ad Loading will wait for this amount of time until it tries next ad unit in line.
- background_threshold -> This parameter has to be a value in `milliseconds` and this will be responsible for Background to Foreground timer for app open ad.
- mediaHeight -> This parameter is only valid for Native Ad to control media height for native ad.


## Export JSON file for AD SDK Implementation
`Step 1:` Open https://admobdash-v2.apyhi.com/

`Step 2:` Login using your google account.

`Step 3:` Open the App from the list and click on the export button at top right corner below New Ad Placement Button.

## Available Ad Sizes For Banner and Native
<p float="left">
<img src="https://github.com/Appyhigh/ad-sdk/blob/main/screenshots/banner_ads.png" style ="width:175px; height:auto; " alt="banner ads"/>
<img src="https://github.com/Appyhigh/ad-sdk/blob/main/screenshots/native_one.png" style ="width:175px; height:auto;" alt="native ads"/>
<img src="https://github.com/Appyhigh/ad-sdk/blob/main/screenshots/native_big_v1.png" style ="width:175px; height:auto;" alt="native ads"/>
<img src="https://github.com/Appyhigh/ad-sdk/blob/main/screenshots/native_big_v2.png" style ="width:175px; height:auto;" alt="native ads"/>
<img src="https://github.com/Appyhigh/ad-sdk/blob/main/screenshots/grid_native.png" style ="width:175px; height:auto;" alt="native ads"/>
</p>

## Proguard Rules
```
-keep class io.jsonwebtoken.*.* { *; }
-keepnames class io.jsonwebtoken.* { *; }
-keepnames interface io.jsonwebtoken.* { *; }
-keep class com.appyhigh.adsdk.data.model.adresponse.** { *; }
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

plugins {
  id 'applovin-quality-service'
}

//root level only
applovin {
    apiKey "APP_LOVIN_API_KEY"
}

dependencies {  
 implementation 'com.github.Appyhigh:ad-sdk.x.x'
 implementation("com.applovin:applovin-sdk:+")
}
 ```

In your project level `build.gradle` file add :
```groovy

buildscript {
    dependencies {
        classpath "com.applovin.quality:AppLovinQualityServiceGradlePlugin:4.9.1"
    }
    repositories {
        maven { url 'https://artifacts.applovin.com/android' }
        google()
    }
}
```

> **Note:** Add mediations for AppLovin whichever are needed from [(Ref)](https://dash.applovin.com/documentation/mediation/android/mediation-adapters)


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

	    <meta-data
            android:name="applovin.sdk.key"
            android:value="APP_LOVIN_API_KEY" />

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

## Fetch Advertising ID
```kotlin
var advertId: String? = null
CoroutineScope(Dispatchers.IO).launch {  
  var idInfo: AdvertisingIdClient.Info? = null  
    try {  
       idInfo = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext)  
    } catch (e: GooglePlayServicesNotAvailableException) {  
	e.printStackTrace()  
    } catch (e: GooglePlayServicesRepairableException) {  
	e.printStackTrace()  
    } catch (e: IOException) {  
	e.printStackTrace()  
    }  
    try {  
	advertId = idInfo!!.id  
	} catch (e: NullPointerException) {  
	e.printStackTrace()  
    }  
}
```

## SDK Initialization

```kotlin
/**  
 * @param application -> Pass your application context here  
 * @param testDevice -> Pass the test device id here. Eg: 59106BA0F480E2EC4CD8CC7AA2C49B81  
 * @param fileId -> Pass the local response file reference her. (R.raw.ad_utils_response)  
 * @param adInitializeListener -> register the adInitializeLister here to listen to success/failure of initialization.  
 */
fun initialize(  
    application: Application,  
    testDevice: String?,
    advertisingId: String?,  
    fileId: Int,  
    adInitializeListener: AdInitializeListener  
)
```

### Example Implementation:
```kotlin
AdSdk.initialize(  
  application = application,  
  testDevice = "59106BA0F480E2EC4CD8CC7AA2C49B81", 
  advertisingId = advertId,
  fileId = R.raw.ad_utils_response,  
  adInitializeListener = object : AdInitializeListener() {  
      override fun onSdkInitialized(isHardStopEnabled: Boolean) {  
               if (isHardStopEnabled) {
                        AdSdk.showCustomHardStopPopup(this@SplashActivity)
		}
	      // Ads Ready to be loaded
      }  

      override fun onInitializationFailed(adSdkError: AdSdkError) {
	      //SDK Initialization Failed  
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
    lifecycle = lifecycle,  
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
    lifecycle = lifecycle, 
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
	    // This will return native ad object only if using AdSdk.fetchNativeAd() and not AdSdk.loadAd()
	}  
	  
	override fun onAdOpened() {  
	    super.onAdOpened()  
	}  
	  
	override fun onAdSwipeGestureClicked() {  
	    super.onAdSwipeGestureClicked()  
	}  
	  
	override fun onMultipleAdsLoaded(nativeAds: ArrayList<NativeAd?>) {  
	    super.onMultipleAdsLoaded(nativeAds)
	    // This will return multiple native ad object only if using AdSdk.fetchNativeAds() and not AdSdk.loadAd()  
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
          // This will return native ad object only if using AdSdk.fetchNativeAd() and not AdSdk.loadAd()  
      }  

      override fun onAdOpened() {  
          super.onAdOpened()  
      }  

      override fun onAdSwipeGestureClicked() {  
          super.onAdSwipeGestureClicked()  
      }  

      override fun onMultipleAdsLoaded(nativeAds: ArrayList<NativeAd?>) {  
          super.onMultipleAdsLoaded(nativeAds)
          // This will return multiple native ad object only if using AdSdk.fetchNativeAds() and not AdSdk.loadAd()   
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
private var mInterstitialAd: InterstitialAd? = null
private var mMaxInterstitialAd: MaxInterstitialAd? = null
AdSdk.loadAd(  
    context = this,
    lifecycle = lifecycle,  
    adName = "test_interstitial",  
    interstitialAdLoadListener = object : InterstitialAdLoadListener() {  
      override fun onAdFailedToLoad(adErrors: List<String>) {  
        //This is fired only if primary, secondary and fallback all 3 Ids fail.  
      }  
      override fun onAdLoaded(interstitialAd: InterstitialAd) {
	     mInterstitialAd = interstitialAd
        //This is fired when ad is loaded for admob/admanager.    
      }
      override fun onApplovinAdLoaded(interstitialAd: MaxInterstitialAd) {
	     mMaxInterstitialAd = interstitialAd
	    //This is fired when ad is loaded for applovin
      }  
    }  
)
```
### Show Interstitial Ad (Admob/Admanager)
```kotlin
if(mInterstitialAd != null) {
	mInterstitialAd.show(this@MainActivity)
}
```

### Show Interstitial Ad (Applovin)
```kotlin
if(mMaxInterstitialAd != null && mMaxInterstitialAd?.isReady!!) {
	mMaxInterstitialAd?.showAd()
}
```

### Set the FullScreenContentCallback for Interstitial Ad (Admob/Admanager)
```kotlin
mInterstitialAd.fullScreenContentCallback = object: FullScreenContentCallback() {  
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
### Set the FullScreenContentCallback for Interstitial Ad (Applovin)
```kotlin
mMaxInterstitialAd?.setListener(object : MaxAdListener {  
    override fun onAdLoaded(p0: MaxAd?) {
	    // This wont be called again, its already consumed within SDK.
    }  
  
    override fun onAdDisplayed(p0: MaxAd?) { 
	    // Called when an impression is recorded for an ad. 
    }  
  
    override fun onAdHidden(p0: MaxAd?) {  
	    // This will be called when ad is closed.  
    }  
  
    override fun onAdClicked(p0: MaxAd?) {
	    // Called when a click is recorded for an ad.     
    }  
  
    override fun onAdLoadFailed(p0: String?, p1: MaxError?) {
	   // This wont be called again, its already consumed within SDK.  
    }  
  
    override fun onAdDisplayFailed(p0: MaxAd?, p1: MaxError?) {
	    // This will be called if ad display failed.   
    }  
})
```


## Load Rewarded Ad
```kotlin
private var mRewardedAd: RewardedAd? = null  
private var mMaxRewardedAd: MaxRewardedAd? = null
AdSdk.loadAd(  
    context = this,
    lifecycle = lifecycle,  
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
### Show Rewarded Ad (Admob/Appmanager)
```kotlin
mRewardedAd?.show(this@MainActivity) {  
  println("${AdSdkConstants.TAG} ${it.type} ${it.amount}")  
}
```

### Show Rewarded Ad (Applovin)
```kotlin
mMaxRewardedAd?.showAd() 
```

### Set the FullScreenContentCallback for Rewarded Ad (Admob/Admanager)
```kotlin
mRewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {  
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

### Set the FullScreenContentCallback for Rewarded Ad (Applovin)
```kotlin
mMaxRewardedAd?.setListener(object : MaxRewardedAdListener {  
    override fun onAdLoaded(p0: MaxAd?) { 
	    // This will not be called, it has been already consumed by SDK. 
    }  
  
    override fun onAdDisplayed(p0: MaxAd?) {
	    // This will be called when ad is shown.  
    }  
  
    override fun onAdHidden(p0: MaxAd?) {  
        // This will be caled when ad is closed.
    }  
  
    override fun onAdClicked(p0: MaxAd?) { 
	    // This is called when ad is clicked. 
    }  
  
    override fun onAdLoadFailed(p0: String?, p1: MaxError?) { 
	    // This will not be called, it has been already consumed by SDK.   
    }  
  
    override fun onAdDisplayFailed(p0: MaxAd?, p1: MaxError?) {  
        // This will be called when ad fails to display.
    }  
  
    override fun onUserRewarded(p0: MaxAd?, p1: MaxReward?) { 
	    //On User Reward Success 
        println("${AdSdkConstants.TAG} ${p1?.label} ${p1?.amount}")  
    }  
  
    override fun onRewardedVideoStarted(p0: MaxAd?) { 
	    // This will be called when rewarded video starts playing. 
    }  
  
    override fun onRewardedVideoCompleted(p0: MaxAd?) {
	    // This will be called when rewarded video completes.  
    }  
})
```


## Load Rewarded Interstitial Ad
> **Note:** This is available only for Admob and Admanager.
```kotlin
AdSdk.loadAd(  
  context = this,
  lifecycle = lifecycle,  
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
          appOpenAd?.show(this@MainActivity)  
      }

	  override fun onApplovinAdLoaded(ad: MaxAppOpenAd) {  
	      super.onApplovinAdLoaded(ad)  
	      maxAppOpenAd?.showAd()
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
