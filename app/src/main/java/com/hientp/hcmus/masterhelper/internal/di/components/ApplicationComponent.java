package com.hientp.hcmus.masterhelper.internal.di.components;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.hientp.hcmus.masterhelper.AndroidApplication;
import com.hientp.hcmus.masterhelper.internal.di.modules.ApplicationModule;
import com.hientp.hcmus.masterhelper.internal.di.modules.ApplicationPluginModule;
import com.hientp.hcmus.masterhelper.navigation.Navigator;
import com.hientp.hcmus.masterhelper.ui.fragment.SplashScreenFragment;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                ApplicationModule.class,
                ApplicationPluginModule.class,
        }
)
public abstract class ApplicationComponent {
    public static ApplicationComponent create(Application application) {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(application))
                .build();
    }

    //Exposed to sub-graphs.
    public abstract Context context();

    public abstract EventBus eventBus();

    public abstract SharedPreferences sharedPreferences();

    public abstract Navigator navigator();

    /*INJECT*/
    public abstract void inject(AndroidApplication androidApplication);

    public abstract void inject(SplashScreenFragment f);
}
