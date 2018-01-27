package com.hientp.hcmus.masterhelper.internal.di;

import android.app.Application;

/**
 * Interface for declaring application plugin
 */

public interface ApplicationPlugin {
    int sortOrder();
    void apply(Application application);
}
