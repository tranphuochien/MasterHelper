package com.hientp.hcmus.masterhelper.internal.di.modules;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.application;
    }

    @Provides
    @Singleton
    EventBus providesEventBus() {
        return EventBus.getDefault();
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Context application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

}
