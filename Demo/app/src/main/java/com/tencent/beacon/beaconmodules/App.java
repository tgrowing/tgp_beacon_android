package com.tencent.beacon.beaconmodules;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.tencent.beacon.BeaconAdapter;
import com.tencent.beacon.event.immediate.BeaconImmediateReportCallback;
import com.tencent.beacon.event.immediate.BeaconTransferArgs;
import com.tencent.beacon.event.immediate.IBeaconImmediateReport;
import com.tencent.beacon.beaconmodules.util.BeaconPrefs;
import com.tencent.beacon.event.open.BeaconConfig;
import com.tencent.beacon.event.open.BeaconEvent;
import com.tencent.beacon.event.open.BeaconReport;
import com.tencent.beacon.event.open.EventType;

//import com.didichuxing.doraemonkit. DoraemonKit;

public class App extends Application {

    public static final String TAG = "Beacon-Test";
    private static Context context;

    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        DoraemonKit.install(this, null, "pId");
//        beforeInit();
        initBeacon();
        context = this;
    }

    private void beforeInit() {
        BeaconEvent event = BeaconEvent.builder()
                .withCode("beforeInit1")
                .withType(EventType.REALTIME)
                .withAppKey(SDKTest.SUB_APP_KEY)
                .build();
        BeaconReport.getInstance().report(event).isSuccess();

        BeaconEvent event2 = BeaconEvent.builder()
                .withCode("beforeInit2")
                .withType(EventType.REALTIME)
                .build();
        BeaconReport.getInstance().report(event2);
    }

    private void initBeacon() {
        Log.i(TAG, "==============Welcome to Beacon===============");
        BeaconPrefs prefs = new BeaconPrefs(this);
        String appKey = prefs.getString(BeaconPrefs.PREFS_KEY_APPKEY, null);
        String eventHost = prefs.getString(BeaconPrefs.PREFS_KEY_UPLOAD_HOST, null);
        if (TextUtils.isEmpty(appKey)) {
            appKey = SDKTest.MAIN_APP_KEY;
        }
        Log.i(App.TAG, "setUploadHost(event): " + eventHost + ", appkey:" + appKey);

        BeaconConfig config = BeaconConfig.builder()
                .maxDBCount(20000)
                .setNormalPollingTime(3500)
                .setRealtimePollingTime(1500)
                .setNormalUploadNum(65)
                .setRealtimeUploadNum(65)
                .setForceEnableAtta(true)
                .pagePathEnable(false)
                .setIsSocketMode(false)
                .setUploadHost(eventHost)
                .setModel(Build.MODEL)
//                .setHttpAdapter(OkHttpAdapter.create(new OkHttpClient()))
//                .setImei("a2")
//                .setImsi("a4")
//                .setMac("a6")
//                .setWifiMacAddress("a20")
//                .setWifiSSID("a69")
//                .setOaid("a144")
                .build();
        BeaconReport beaconReport = BeaconReport.getInstance();
        beaconReport.setCollectProcessInfo(false);
        beaconReport.setStrictMode(true);
        beaconReport.setAppVersion("1.2.4");
        beaconReport.setChannelID("demo-10001");
        beaconReport.setLogAble(true);
        beaconReport.setAndroidID("androidid-github-demo");
//        beaconReport.setImei("A2");
//        beaconReport.setImsi("A4");
//        beaconReport.setMac("A6");
//        beaconReport.setModel("A10");
//        beaconReport.setWifiMacAddress("A20");
//        beaconReport.setWifiSSID("A69");
//        beaconReport.setOaid("A144");
        long l = System.currentTimeMillis();
//        beaconReport.setCollectAndroidID(false);
//        beaconReport.setStrictMode(false);
//        beaconReport.start(this, new TunnelInfo(SDKTest.MAIN_APP_KEY, "123", "45"), config);

        beaconReport.start(this, appKey, config);
        Log.i(TAG, "init cost time: " + (System.currentTimeMillis() - l));
        BeaconAdapter.registerTunnel(SDKTest.SUB_APP_KEY, "bb", "1001");
    }
}
