package com.xabeng.myweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by sev_user on 8/30/2018.
 */

public class RefreshReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.changeCity(Utils.mCityPreference.getCity());
    }
}
