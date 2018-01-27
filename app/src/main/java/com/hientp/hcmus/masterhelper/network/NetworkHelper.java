package com.hientp.hcmus.masterhelper.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import timber.log.Timber;

/**
 * Provide static helper methods which are network related
 */
public class NetworkHelper {
    public static boolean isNetworkAvailable(Context context) {
        if(context == null){
            Timber.d("check network status with context null");
            return true;
        }
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            return false;
        }

        Timber.d("Current network type [%s] and state [%s]", networkInfo.getTypeName(), networkInfo.getState());
        return networkInfo.isConnected();
    }
}
