package com.hientp.hcmus.masterhelper.navigation;

import android.content.Context;
import android.content.Intent;

import com.hientp.hcmus.masterhelper.MainActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by hientp on 26/01/2018.
 */

@Singleton
public class Navigator {

    @Inject
    public Navigator() {
    }

    public void startHomeActivity(Context context) {
        startHomeActivity(context, false);
    }

    public void startHomeActivity(Context context, boolean clearTop) {
        Intent intent = intentHomeActivity(context, clearTop);
        context.startActivity(intent);
    }

    public Intent intentHomeActivity(Context context, boolean clearTop) {
        Intent intent = new Intent(context, MainActivity.class);

        if (clearTop) {
            intent.putExtra("finish", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        }
        return intent;
    }
}
