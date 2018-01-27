package com.hientp.hcmus.masterhelper;


import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hientp.hcmus.masterhelper.app.AppLifeCycle;
import com.hientp.hcmus.masterhelper.internal.di.ApplicationPlugin;
import com.hientp.hcmus.masterhelper.internal.di.components.ApplicationComponent;
import com.hientp.hcmus.uiwidget.util.FontLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import timber.log.Timber;

public class AndroidApplication extends Application {

    private static AndroidApplication _instance;
    private ApplicationComponent appComponent;
    @Inject
    Set<ApplicationPlugin> mApplicationPlugins;


    public static AndroidApplication instance() {
        return _instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        _instance = this;
        initApplication();
    }

    private void initApplication() {
      /*  if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }*/
        FontLoader.initialize(this);
        registerActivityLifecycleCallbacks(new AppLifeCycle());
        initAppComponent();

        //Inject plugins
        ApplicationPlugin[] plugins = mApplicationPlugins.toArray(new ApplicationPlugin[0]);
        List<ApplicationPlugin> pluginList = new ArrayList<>();
        Collections.addAll(pluginList, plugins);
        Collections.sort(pluginList, (o1, o2) -> o1.sortOrder() > o2.sortOrder() ? 1 : -1);
        for (ApplicationPlugin plugin: pluginList) {
            plugin.apply(this);
        }
    }

    private void initAppComponent() {
        appComponent = ApplicationComponent.create(this);

        Timber.d("onCreate %s", appComponent);
        appComponent.inject(this);
    }

    public ApplicationComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Fresco.getImagePipeline().clearMemoryCaches();
    }
}
