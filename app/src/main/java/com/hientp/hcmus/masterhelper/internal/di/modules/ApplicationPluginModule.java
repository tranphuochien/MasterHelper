package com.hientp.hcmus.masterhelper.internal.di.modules;


import com.hientp.hcmus.masterhelper.internal.di.ApplicationPlugin;
import com.hientp.hcmus.masterhelper.internal.di.SystemRegistryPlugin;
import com.hientp.hcmus.masterhelper.internal.di.TimberPlugin;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

/**
 * Provider all application plugins
 */

@Module
public class ApplicationPluginModule {

    @Provides
    @IntoSet
    ApplicationPlugin providerTimberPlugin() {
        return new TimberPlugin();
    }

    @Provides
    @IntoSet
    ApplicationPlugin providerSystemRegistryPlugin() {
        return new SystemRegistryPlugin();
    }
}
