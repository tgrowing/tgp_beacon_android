package com.tencent.beacon.beaconmodules;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
//import com.tencent.beacon.base.info.BeaconQimei;
//import com.tencent.beacon.base.info.QimeiWrapper;
//import com.tencent.beacon.base.util.ELog;
import com.tencent.beacon.event.open.BeaconReport;
import com.tencent.beacon.event.open.EventType;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * beacondemo:service进程
 */
public class BeaconService extends Service {

    private static final String TAG = "BeaconService";
    private ScheduledExecutorService executorService;

    public BeaconService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        getQimei();
        executorService = Executors.newScheduledThreadPool(3);
        //pollingReport();
    }

//    private void getQimei() {
//        BeaconQimei qimei = QimeiWrapper.getQimei();
//        ELog.debug(qimei.toString());
//    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * App上报和多通道上报轮询
     * 这里可以调慢一点, 主要是没有界面点击触发
     */
    private void pollingReport() {
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long cost = SDKTest
                        .manyEventReport(SDKTest.MAIN_APP_KEY, "service_main_report", EventType.REALTIME, 30);
                Log.i(TAG, "30条service_main_report事件入库耗时:" + cost);
            }
        }, 100, 5 * 1000, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long cost = SDKTest.manyEventReport(SDKTest.SUB_APP_KEY, "service_sub_report", EventType.NORMAL, 12);
                Log.i(TAG, "12条service_sub_report事件入库耗时:" + cost);
            }
        }, 100, 6 * 1000, TimeUnit.MILLISECONDS);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
