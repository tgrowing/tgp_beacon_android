package com.tencent.beacon.beaconmodules.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BeaconReportUtilsTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void initSDK() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        System.out.println("Test end!");
    }
}