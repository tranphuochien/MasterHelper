package com.hientp.hcmus.masterhelper.internal.di;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.hientp.hcmus.masterhelper.recceivers.NetworkReceiver;

import timber.log.Timber;

public class SystemRegistryPlugin implements ApplicationPlugin {
    @Override
    public int sortOrder() {
        return 50;
    }

    @Override
    public void apply(Application application) {
        Timber.d("Register network receiver");
        NetworkReceiver networkChangeReceiver = new NetworkReceiver();
        application.registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
}
