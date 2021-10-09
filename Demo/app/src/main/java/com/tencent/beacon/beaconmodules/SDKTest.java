package com.tencent.beacon.beaconmodules;

import com.tencent.beacon.event.open.BeaconEvent;
import com.tencent.beacon.event.open.BeaconReport;
import com.tencent.beacon.event.open.EventType;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SDKTest {
    // 宿主AppKey
    public static final String MAIN_APP_KEY = "MAIN_APP_KEY";
    // 子通道AppKey
    public static final String SUB_APP_KEY = "SUB_APP_KEY";

    /**
     * 随机60以内的kv
     * 每个v最多1020K
     * 总体KV最多61K
     */
    public static Map<String, String> productRandomParams() {
        Map<String, String> map = new HashMap<>();
        Random random = new Random();
        int kvCount = random.nextInt(30);
        for (int i = 0; i < kvCount; i++) {
            int valueLen = random.nextInt(30);
            StringBuilder valueBuilder = new StringBuilder();
            for (int j = 0; j < valueLen; j++) {
                valueBuilder.append(RandomStringUtils.randomAlphanumeric(10)); // length=10
            }
            map.put("key_" + i, valueBuilder.toString());
        }
        return map;
    }

    public static Map<String, String> productRandomParamsLong() {
        Map<String, String> map = new HashMap<>();
        Random random = new Random();
        int kvCount = 60;
        for (int i = 0; i < kvCount; i++) {
            int valueLen = 10240;
            StringBuilder valueBuilder = new StringBuilder();
            for (int j = 0; j < valueLen; j++) {
                valueBuilder.append("a1#2c3d}e5"); // length=10
            }
            map.put("key_" + i, valueBuilder.toString());
        }
        return map;
    }

    public static Map<String, String> productRandomParams_10kb() {
        Map<String, String> map = new HashMap<>();
        Random random = new Random();
        int kvCount = 5;
        for (int i = 0; i < kvCount; i++) {
            int valueLen = 1024;
            StringBuilder valueBuilder = new StringBuilder();
            for (int j = 0; j < valueLen; j++) {
                valueBuilder.append("a1#2c3d}e511"); // length=12
            }
            map.put("key_" + i, valueBuilder.toString());
        }
        return map;
    }


    public static long manyEventReport(String appKey, String eventCode, EventType type, int count) {
        long l = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            BeaconEvent normal = BeaconEvent.builder()
                    .withAppKey(appKey)
                    .withCode(eventCode.concat("_" + i))
                    .withParams(productRandomParams())
                    .withType(type)
                    .build();
            BeaconReport.getInstance().report(normal);
        }
        return System.currentTimeMillis() - l;
    }
}
