package com.hientp.hcmus.masterhelper.recceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hientp.hcmus.masterhelper.AndroidApplication;
import com.hientp.hcmus.masterhelper.event.NetworkChangeEvent;
import com.hientp.hcmus.masterhelper.network.BaseNetworkInterceptor;
import com.hientp.hcmus.masterhelper.network.NetworkHelper;
import com.hientp.hcmus.masterhelper.util.AndroidUtils;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;


public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isOnline = NetworkHelper.isNetworkAvailable(context);
        Timber.d("Network State Change : isOnline %s", isOnline);
        EventBus eventBus = AndroidApplication.instance().getAppComponent().eventBus();
        eventBus.post(new NetworkChangeEvent(isOnline));
        if (isOnline) {
            BaseNetworkInterceptor.CONNECTION_TYPE = AndroidUtils.getNetworkClass();
        }
    }
}
