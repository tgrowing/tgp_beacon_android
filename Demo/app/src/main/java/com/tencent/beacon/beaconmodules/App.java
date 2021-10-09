package com.tencent.beacon.beaconmodules;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.tencent.beacon.BeaconAdapter;
//import com.tencent.beacon.base.info.QimeiWrapper;
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
        beforeInit();
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
        BeaconConfig config = BeaconConfig.builder()
                .maxDBCount(20000)
                .setNormalPollingTime(3000)
                .setRealtimePollingTime(1000)
                .setForceEnableAtta(true)
                .pagePathEnable(false)
//                .setHttpAdapter(OkHttpAdapter.create(new OkHttpClient()))
//                .setImei("a2")
//                .setImsi("a4")
//                .setMac("a6")
//                .setModel("a10")
//                .setWifiMacAddress("a20")
//                .setWifiSSID("a69")
//                .setOaid("a144")
                .build();
        BeaconReport beaconReport = BeaconReport.getInstance();
        beaconReport.setCollectProcessInfo(false);
        beaconReport.setStrictMode(true);
        beaconReport.setAppVersion("1.1.1.1");
        beaconReport.setLogAble(true);
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
        beaconReport.start(this, SDKTest.MAIN_APP_KEY, config);
        Log.i(TAG, "init cost time: " + (System.currentTimeMillis() - l));
//        Log.i(TAG, "init getQimei16: " + QimeiWrapper.getQimei().getQimei16() + "getQimei36: "
//                + QimeiWrapper.getQimei().getQimei36());

        BeaconAdapter.registerTunnel(SDKTest.SUB_APP_KEY, "bb", "1001");
    }
}
