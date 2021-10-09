package com.tencent.beacon.beaconmodules.util;

import android.os.Handler;
import android.os.Looper;
import com.tencent.beacon.event.open.BeaconEvent;
import com.tencent.beacon.event.open.BeaconReport;
import com.tencent.beacon.event.open.EventType;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestUtils {

    private static ScheduledExecutorService service = Executors.newScheduledThreadPool(3, new ThreadFactory() {
        AtomicInteger num = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "app-test-thread-" + num.getAndIncrement());
        }
    });

    private static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Javadoc待补充
     */
    public static void startPressTest(String appKey, String eventCode, Map<String, String> params,
            boolean isRealtime, long period, long count, long total) {
        BeaconEvent event = BeaconEvent.builder()
                .withCode(eventCode)
                .withType(isRealtime ? EventType.REALTIME : EventType.NORMAL)
                .withParams(params)
                .withAppKey(appKey)
                .withIsSucceed(true)
                .build();
        Future<?> future = service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < count; i++) {
                    BeaconReport.getInstance().report(event);
                }
            }
        }, 0, period, TimeUnit.MILLISECONDS);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                future.cancel(false);
            }
        }, total);
    }
}

