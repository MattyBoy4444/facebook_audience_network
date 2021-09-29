package com.dsi.facebook_audience_network;

import android.content.Context;
import androidx.annotation.NonNull;

import com.facebook.ads.*;

import java.util.HashMap;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.platform.PlatformViewRegistry;

/**
 * FacebookAudienceNetworkPlugin
 */
public class FacebookAudienceNetworkPlugin implements MethodCallHandler, FlutterPlugin {

    private Context mContext = null;

    private FacebookAudienceNetworkPlugin() {
    }

    private FacebookAudienceNetworkPlugin(Context context) {
        this.mContext = context;
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(PluginRegistry.Registrar registrar) {
        register(registrar.messenger(), registrar.context(), registrar.platformViewRegistry());
    }

    @Override
    public void onMethodCall(MethodCall call, @NonNull Result result) {

        if (call.method.equals(FacebookConstants.INIT_METHOD))
            result.success(init((HashMap) call.arguments));
        else
            result.notImplemented();
    }

    private boolean init(HashMap initValues) {
        final String testingId = (String) initValues.get("testingId");

        if (mContext != null)
            AudienceNetworkAds.initialize(mContext);

        if (testingId != null) {
            AdSettings.addTestDevice(testingId);
        }
        return true;
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {
        register(binding.getBinaryMessenger(), binding.getApplicationContext(), binding.getPlatformViewRegistry());
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {

    }

    private static void register(BinaryMessenger messenger, Context context, PlatformViewRegistry platformViewRegistry)
    {
        // Main channel for initialization
        final MethodChannel channel = new MethodChannel(messenger,
                FacebookConstants.MAIN_CHANNEL);
        channel.setMethodCallHandler(new FacebookAudienceNetworkPlugin(context));

        // Interstitial Ad channel
        final MethodChannel interstitialAdChannel = new MethodChannel(messenger,
                FacebookConstants.INTERSTITIAL_AD_CHANNEL);
        interstitialAdChannel
                .setMethodCallHandler(new FacebookInterstitialAdPlugin(context,
                        interstitialAdChannel));

        // Rewarded video Ad channel
        final MethodChannel rewardedAdChannel = new MethodChannel(messenger,
                FacebookConstants.REWARDED_VIDEO_CHANNEL);
        rewardedAdChannel
                .setMethodCallHandler(new FacebookRewardedVideoAdPlugin(context,
                        rewardedAdChannel));

        // Banner Ad PlatformView channel
        platformViewRegistry.
                registerViewFactory(FacebookConstants.BANNER_AD_CHANNEL,
                        new FacebookBannerAdPlugin(messenger));
        
        // Native Ad PlatformView channel
        platformViewRegistry.
                registerViewFactory(FacebookConstants.NATIVE_AD_CHANNEL,
                        new FacebookNativeAdPlugin(messenger));
    }
}

