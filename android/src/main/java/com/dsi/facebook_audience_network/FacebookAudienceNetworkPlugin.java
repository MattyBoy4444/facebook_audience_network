package com.dsi.facebook_audience_network;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.facebook.ads.*;

import java.util.HashMap;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

/**
 * FacebookAudienceNetworkPlugin
 */
public class FacebookAudienceNetworkPlugin implements MethodCallHandler, FlutterPlugin, ActivityAware {

    private Activity mActivity = null;

    /**
     * Plugin registration.
     */
    public static void registerWith(PluginRegistry.Registrar registrar) {
        //register(registrar.messenger(), registrar.context(), registrar.platformViewRegistry());
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

        if (mActivity != null)
            AudienceNetworkAds.initialize(mActivity.getApplicationContext());

        if (testingId != null) {
            AdSettings.addTestDevice(testingId);
        }
        return true;
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {

        // Main channel for initialization
        final MethodChannel channel = new MethodChannel(binding.getBinaryMessenger(),
                FacebookConstants.MAIN_CHANNEL);
        channel.setMethodCallHandler(this);

        // Interstitial Ad channel
        final MethodChannel interstitialAdChannel = new MethodChannel(binding.getBinaryMessenger(),
                FacebookConstants.INTERSTITIAL_AD_CHANNEL);
        interstitialAdChannel
                .setMethodCallHandler(new FacebookInterstitialAdPlugin(binding.getApplicationContext(),
                        interstitialAdChannel));

        // Rewarded video Ad channel
        final MethodChannel rewardedAdChannel = new MethodChannel(binding.getBinaryMessenger(),
                FacebookConstants.REWARDED_VIDEO_CHANNEL);
        rewardedAdChannel
                .setMethodCallHandler(new FacebookRewardedVideoAdPlugin(binding.getApplicationContext(),
                        rewardedAdChannel));

        // Banner Ad PlatformView channel
        binding.getPlatformViewRegistry().
                registerViewFactory(FacebookConstants.BANNER_AD_CHANNEL,
                        new FacebookBannerAdPlugin(binding.getBinaryMessenger()));

        // Native Ad PlatformView channel
        binding.getPlatformViewRegistry().
                registerViewFactory(FacebookConstants.NATIVE_AD_CHANNEL,
                        new FacebookNativeAdPlugin(binding.getBinaryMessenger()));
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {

    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        mActivity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        mActivity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        mActivity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        mActivity = null;
    }
}

