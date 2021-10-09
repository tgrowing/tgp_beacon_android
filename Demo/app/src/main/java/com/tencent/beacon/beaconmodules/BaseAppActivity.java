package com.tencent.beacon.beaconmodules;

import android.support.v7.app.AppCompatActivity;

//import com.tencent.beacon.event.UserAction;

/**
 * Created by mowang on 2017/4/13.
 */

public class BaseAppActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
//        UserAction.onPageIn(this.getClass().getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
//        UserAction.onPageIn(this.getClass().getName());
    }
}
