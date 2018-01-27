package com.hientp.hcmus.masterhelper.internal.di;

import android.app.Application;

import timber.log.Timber;

/**
 * Initialize Timber for debug build
 */

public class TimberPlugin implements ApplicationPlugin {
    @Override
    public int sortOrder() {
        return 1;
    }

    @Override
    public void apply(Application application) {
        Timber.plant(new Timber.DebugTree());
    }
}
