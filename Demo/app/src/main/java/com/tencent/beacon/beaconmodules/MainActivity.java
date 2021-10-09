package com.tencent.beacon.beaconmodules;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.beacon.BeaconAdapter;
//import com.tencent.beacon.base.info.QimeiWrapper;
//import com.tencent.beacon.base.util.ELog;
import com.tencent.beacon.beaconmodules.util.TestUtils;
import com.tencent.beacon.event.UserAction;
import com.tencent.beacon.event.open.BeaconEvent;
import com.tencent.beacon.event.open.BeaconReport;
import com.tencent.beacon.event.open.EventResult;
import com.tencent.beacon.event.open.EventType;

import java.util.List;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = App.TAG;
    private ScheduledExecutorService executorService;
    private boolean isStartLookThread;
    private Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executorService = Executors.newScheduledThreadPool(6);
//        initView();
        //syncGetQimei();
        serviceIntent = new Intent(this, BeaconService.class);
        startService(serviceIntent);
//        pressTest();
        reportManualEvent();
        methodRequiresTwoPermission();
    }

    private void reportManualEvent() {
        EditText eventCodeEditText = findViewById(R.id.eventEditText);
        EditText eventParamsEditText = findViewById(R.id.manualParams);
        findViewById(R.id.manualEvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = eventCodeEditText.getText().toString();
                String eventParams = eventParamsEditText.getText().toString();
                if (TextUtils.isEmpty(eventName)) {
                    Toast.makeText(MainActivity.this, "事件名称不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                HashMap<String, String> params = new HashMap<>();
                if (!TextUtils.isEmpty(eventParams)) {
                    try {
                        JSONObject jsonObject = new JSONObject(eventParams);
                        Iterator<String> keyIter = jsonObject.keys();
                        String key;
                        String value;
                        while (keyIter.hasNext())
                        {
                            key = (String) keyIter.next();
                            value = jsonObject.getString(key);
                            params.put(key, value);
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "err:" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                String subAppKey = ((EditText) findViewById(R.id.subAppKeyEdit)).getText().toString();
                BeaconEvent.Builder builder = BeaconEvent.builder()
                        .withCode(eventName)
                        .withParams(params)
                        .withType(EventType.REALTIME);
                if (!TextUtils.isEmpty(subAppKey)) {
                    builder.withAppKey(subAppKey);
                }
                BeaconEvent normal = builder.build();
                EventResult result = BeaconReport.getInstance().report(normal);
                toastResult(result.eventID, result.errorCode, result.errMsg);
            }
        });
    }

    private void pressTest() {
        TestUtils.startPressTest(SDKTest.MAIN_APP_KEY, "main_press", SDKTest.productRandomParams(),
                true, 300, 10, 60 * 1000);
        TestUtils.startPressTest(SDKTest.MAIN_APP_KEY, "main_normal_press", SDKTest.productRandomParams(),
                false, 300, 50, 15 * 1000);
        TestUtils.startPressTest(SDKTest.SUB_APP_KEY, "sub_press", SDKTest.productRandomParams(),
                true, 500, 10, 34 * 1000);
        TestUtils.startPressTest(SDKTest.SUB_APP_KEY, "sub_normal_press", SDKTest.productRandomParams(),
                true, 500, 10, 40 * 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serviceIntent);
    }

    private void initView() {
        TextView sdkVersion = findViewById(R.id.show_sdk_version);
        sdkVersion.setText(BeaconReport.getInstance().getSDKVersion());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(1)
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "有读写和PHONE_STATE权限", Toast.LENGTH_LONG).show();
        } else {
            EasyPermissions.requestPermissions(this, "需要读写权限", 1, perms);
        }
    }

    private void toastResult(long eventID, int errorCode, String msg) {
        String text;
        if (errorCode == EventResult.ERROR_CODE_SUCCESS) {
            text = "入库成功,eventID:" + eventID;
        } else {
            text = "入库失败,原因:" + msg;
        }
        Log.i(App.TAG, "report result: " + text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void toastCostTime(long cost, String msg) {
        String text = "msg: " + msg + ",耗时: " + cost;
        Log.i(TAG, text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // 不加AppKey默认是主通道App
    public void reportMainNormal(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withCode("main_normal")
                .withParams(SDKTest.productRandomParams())
                .withType(EventType.NORMAL)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportMainNormal_nopara(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withCode("main_normal")
                //.withParams(SDKTest.productRandomParams())
                .withType(EventType.NORMAL)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportMainDTNormal(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withCode("main_normal_DT")
                .withParams(SDKTest.productRandomParams())
                .withType(EventType.DT_NORMAL)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportMainRealtime(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withCode("main_realtime")
                .withParams(SDKTest.productRandomParams())
                .withType(EventType.REALTIME)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportMainRealtime_nopara(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withCode("main_realtime")
                //.withParams(SDKTest.productRandomParams())
                .withType(EventType.REALTIME)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportMainDTRealTime(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withCode("main_realtime_DT")
                .withParams(SDKTest.productRandomParams())
                .withType(EventType.DT_REALTIME)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportMainNormalWithoutCode(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withCode("")
                .withParams(SDKTest.productRandomParams())
                .withType(EventType.NORMAL)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportMainRealtimeWithoutCode(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withCode("")
                .withParams(SDKTest.productRandomParams())
                .withType(EventType.REALTIME)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportMainNormalLarge(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withCode("main_normal")
                .withParams(SDKTest.productRandomParamsLong())
                .withType(EventType.NORMAL)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportMainRealtimeLarge(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withCode("main_realtime")
                .withParams(SDKTest.productRandomParamsLong())
                .withType(EventType.REALTIME)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }


    public void reportMainNormalGreater(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withCode("main_normal")
                .withParams(SDKTest.productRandomParams_10kb())
                .withType(EventType.NORMAL)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportMainRealtimeGreater(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withCode("main_realtime")
                .withParams(SDKTest.productRandomParams_10kb())
                .withType(EventType.REALTIME)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportMainNormalNone(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withAppKey("")
                .withCode("main_normal")
                .withParams(SDKTest.productRandomParams())
                .withType(EventType.NORMAL)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportMainRealtimeNone(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withAppKey("")
                .withCode("main_realtime")
                .withParams(SDKTest.productRandomParams())
                .withType(EventType.REALTIME)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportSubRealtime(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withAppKey(SDKTest.SUB_APP_KEY)
                .withCode("sub_realtime")
                .withParams(SDKTest.productRandomParams())
                .withType(EventType.REALTIME)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportSubRealtime_nopara(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withAppKey(SDKTest.SUB_APP_KEY)
                .withCode("sub_realtime")
                //.withParams(SDKTest.productRandomParams())
                .withType(EventType.REALTIME)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void agreement(View view) {
        BeaconReport instance = BeaconReport.getInstance();
        instance.setCollectProcessInfo(true);
    }

    public void reportSubDTRealTime(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withAppKey(SDKTest.SUB_APP_KEY)
                .withCode("sub_realtime_DT")
                .withParams(SDKTest.productRandomParams())
                .withType(EventType.DT_REALTIME)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportSubRealtimeWithoutCode(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withAppKey(SDKTest.SUB_APP_KEY)
                .withCode("")
                .withParams(SDKTest.productRandomParams())
                .withType(EventType.REALTIME)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }


    public void reportSubRealtimeLarge(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withAppKey(SDKTest.SUB_APP_KEY)
                .withCode("sub_realtime")
                .withParams(SDKTest.productRandomParamsLong())
                .withType(EventType.REALTIME)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportSubNormal(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withAppKey(SDKTest.SUB_APP_KEY)
                .withCode("sub_normal")
                .withParams(SDKTest.productRandomParams())
                .withType(EventType.NORMAL)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportSubNormal_nopara(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withAppKey(SDKTest.SUB_APP_KEY)
                .withCode("sub_normal")
                //.withParams("k1", "v1")
                .withType(EventType.NORMAL)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportSubDTNormal(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withAppKey(SDKTest.SUB_APP_KEY)
                .withCode("sub_normal_DT")
                .withParams(SDKTest.productRandomParams())
                .withType(EventType.DT_NORMAL)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportSubNormalWithoutCode(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withAppKey(SDKTest.SUB_APP_KEY)
                .withCode("")
                .withParams(SDKTest.productRandomParams())
                .withType(EventType.NORMAL)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportSubNormalLarge(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withAppKey(SDKTest.SUB_APP_KEY)
                .withCode("sub_normal")
                .withParams(SDKTest.productRandomParamsLong())
                .withType(EventType.NORMAL)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportSubNormalGreater(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withAppKey(SDKTest.SUB_APP_KEY)
                .withCode("sub_normal")
                .withParams(SDKTest.productRandomParams_10kb())
                .withType(EventType.NORMAL)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    public void reportSubRealtimeGreater(View view) {
        BeaconEvent normal = BeaconEvent.builder()
                .withAppKey(SDKTest.SUB_APP_KEY)
                .withCode("sub_realtime")
                .withParams(SDKTest.productRandomParams_10kb())
                .withType(EventType.REALTIME)
                .build();
        EventResult result = BeaconReport.getInstance().report(normal);
        toastResult(result.eventID, result.errorCode, result.errMsg);
    }

    /**
     * Javadoc待补充
     */
    public void reportMainManyNormal(View view) {
        //初始化edittext
        EditText mEditText = (EditText) findViewById(R.id.app_task_number);
        executorService.execute(new Runnable() {

            public void run() {
                final String code = "main_normal_many";
                //获取上报条数
                int num = Integer.parseInt(mEditText.getText().toString());
                final long cost = SDKTest.manyEventReport(SDKTest.MAIN_APP_KEY, code, EventType.NORMAL, num);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastCostTime(cost, num + code + " 入库结束");
                    }
                });
            }
        });
    }


    public void reportMainManyRealtime(View view) {
        EditText mEditText = (EditText) findViewById(R.id.app_task_number);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final String code = "main_realtime_many";
                int num = Integer.parseInt(mEditText.getText().toString());
                final long cost = SDKTest.manyEventReport(SDKTest.MAIN_APP_KEY, code, EventType.REALTIME, num);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastCostTime(cost, num + code + " 入库结束");
                    }
                });
            }
        });
    }

    public void reportMainManyDTRealtime(View view) {
        EditText mEditText = (EditText) findViewById(R.id.app_task_number);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final String code = "main_realtime_many_DT";
                int num = Integer.parseInt(mEditText.getText().toString());
                final long cost = SDKTest.manyEventReport(SDKTest.MAIN_APP_KEY, code, EventType.DT_REALTIME, num);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastCostTime(cost, num + code + " 入库结束");
                    }
                });
            }
        });
    }

    public void reportMainManyDTNormal(View view) {
        EditText mEditText = (EditText) findViewById(R.id.app_task_number);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final String code = "main_normal_many_DT";
                int num = Integer.parseInt(mEditText.getText().toString());
                final long cost = SDKTest.manyEventReport(SDKTest.MAIN_APP_KEY, code, EventType.DT_NORMAL, num);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastCostTime(cost, num + code + " 入库结束");
                    }
                });
            }
        });
    }


    public void reportSubManyNormal(View view) {
        EditText mEditTextAisle = (EditText) findViewById(R.id.aisle_task_number);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final String code = "sub_normal_many";
                int num = Integer.parseInt(mEditTextAisle.getText().toString());
                final long cost = SDKTest.manyEventReport(SDKTest.SUB_APP_KEY, code, EventType.NORMAL, num);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastCostTime(cost, num + code + " 入库结束");
                    }
                });
            }
        });
    }

    public void reportSubManyRealtime(View view) {
        EditText mEditTextAisle = (EditText) findViewById(R.id.aisle_task_number);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final String code = "sub_realtime_many";
                int num = Integer.parseInt(mEditTextAisle.getText().toString());
                final long cost = SDKTest.manyEventReport(SDKTest.SUB_APP_KEY, code, EventType.REALTIME, num);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastCostTime(cost, num + "条" + code + " 入库结束");
                    }
                });
            }
        });
    }

    public void reportSubManyDTRealtime(View view) {
        EditText mEditTextAisle = (EditText) findViewById(R.id.aisle_task_number);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final String code = "sub_realtime_many_DT";
                int num = Integer.parseInt(mEditTextAisle.getText().toString());
                final long cost = SDKTest.manyEventReport(SDKTest.SUB_APP_KEY, code, EventType.DT_REALTIME, num);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastCostTime(cost, num + "条" + code + " 入库结束");
                    }
                });
            }
        });
    }

    public void reportSubManyDTNormal(View view) {
        EditText mEditTextAisle = (EditText) findViewById(R.id.aisle_task_number);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final String code = "sub_normal_many_DT";
                int num = Integer.parseInt(mEditTextAisle.getText().toString());
                final long cost = SDKTest.manyEventReport(SDKTest.SUB_APP_KEY, code, EventType.DT_NORMAL, num);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastCostTime(cost, num + "条" + code + " 入库结束");
                    }
                });
            }
        });
    }

//    public void refreshQimei(View view) {
//        getQimei();
//    }

    // 获取Qimei
//    private void getQimei() {
//        TextView qimeiTv = findViewById(R.id.show_qimei);
//        String msg = String.format("getQimei: qimei16 = %s, qimei36 = %s", QimeiWrapper.getQimei().getQimei16(),
//                QimeiWrapper.getQimei().getQimei36());
//        ELog.debug2(TAG, msg);
//        qimeiTv.setText(msg);
//        Toast.makeText(this, "获取qimei", Toast.LENGTH_SHORT).show();
//    }

    public void setBeaconParams(View view) {
        BeaconReport beaconReport = BeaconReport.getInstance();
        beaconReport.setQQ("202005212200");
        beaconReport.setUserID("0000001");
        beaconReport.setOmgID("19990624");
    }

    /**
     * 性能测试时可以去掉这个轮询,避免影响真实结果
     */
    public synchronized void showBeaconThread(View view) {
        if (isStartLookThread) {
            return;
        }
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                StringBuilder builder = new StringBuilder();
                Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
                for (Map.Entry<Thread, StackTraceElement[]> threadEntry : allStackTraces.entrySet()) {
                    Thread key = threadEntry.getKey();
                    if (key.getName().contains("beacon")) {
                        builder.append(key.getName()).append(":");
                        StackTraceElement[] value = threadEntry.getValue();
                        StackTraceElement firstTrace = value[0];
                        String fileName = firstTrace.getFileName();
                        if (fileName == null) {
                            fileName = "";
                        }
                        String methodName = firstTrace.getMethodName();
                        if (methodName.contains("$")) {
                            methodName = methodName.substring(methodName.indexOf("$")
                                    + 1, methodName.lastIndexOf("$") - 2);
                        }
                        builder.append("(").append(fileName).append(":").append(firstTrace.getLineNumber())
                                .append(")").append(methodName).append(" ");
                        builder.append("\n");
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = findViewById(R.id.beacon_thread_tv);
                        textView.setText(builder.toString());
                    }
                });
            }
        }, 0, 20, TimeUnit.MILLISECONDS);
        isStartLookThread = true;
    }

    public void reportOldNormal(View view) {
        boolean isSuccess = UserAction.onUserAction("old_normal", SDKTest.productRandomParams(), false, false);
        Toast.makeText(this, isSuccess ? "上报成功" : "上报失败", Toast.LENGTH_SHORT).show();
    }

    public void reportOldRealtime(View view) {
        boolean isSuccess = UserAction.onUserAction("old_realtime", SDKTest.productRandomParams(), true, true);
        Toast.makeText(this, isSuccess ? "上报成功" : "上报失败", Toast.LENGTH_SHORT).show();
    }

    public void reportOldSubNormal(View view) {
        boolean isSuccess = BeaconAdapter.onUserAction("old_sub_normal", true,
                -1, -1, SDKTest.productRandomParams(), false);
        Toast.makeText(this, isSuccess ? "上报成功" : "上报失败", Toast.LENGTH_SHORT).show();
    }

    public void reportOldSubRealtime(View view) {
        boolean isSuccess = BeaconAdapter.onUserAction("old_sub_realtime", true,
                -1, -1, SDKTest.productRandomParams(), true, true);
        Toast.makeText(this, isSuccess ? "上报成功" : "上报失败", Toast.LENGTH_SHORT).show();
    }

    public void refreshOaid(View view) {

        Toast.makeText(this, "OAID：" + BeaconReport.getInstance().getOAID(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.i(TAG, "onPermissionsGranted perms: " + perms);

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.i(TAG, "onPermissionsDenied perms: " + perms);

    }
}