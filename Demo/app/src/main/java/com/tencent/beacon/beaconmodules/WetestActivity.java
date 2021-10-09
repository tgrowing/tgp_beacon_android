package com.tencent.beacon.beaconmodules;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tencent.beacon.beaconmodules.util.TestUtils;
import java.util.concurrent.ScheduledExecutorService;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Javadoc 待补充
 *
 * @author tyrsong
 */
public class WetestActivity extends Activity {

    private static final String TAG = App.TAG;
    private ScheduledExecutorService executorService;
    private boolean isStartLookThread;
    private Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);
        pressTest();
    }

    private void pressTest() {
        TestUtils.startPressTest(SDKTest.MAIN_APP_KEY, "main_press", SDKTest.productRandomParams(), true, 300, 20,
                30 * 1000);
        TestUtils.startPressTest(SDKTest.MAIN_APP_KEY, "main_normal_press", SDKTest.productRandomParams(), false, 300,
                50, 30 * 1000);
        TestUtils.startPressTest(SDKTest.SUB_APP_KEY, "sub_press", SDKTest.productRandomParams(), true, 500, 10,
                34 * 1000);
        TestUtils.startPressTest(SDKTest.SUB_APP_KEY, "sub_normal_press", SDKTest.productRandomParams(), true, 500,
                10, 30 * 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
