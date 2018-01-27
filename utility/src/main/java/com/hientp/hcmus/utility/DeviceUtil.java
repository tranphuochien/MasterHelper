package com.hientp.hcmus.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;

import java.util.UUID;

import timber.log.Timber;

public class DeviceUtil {
    private final static String STR_EMPTY = "";

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    public static String getDeviceManufacturer() {
        try {
            String model = android.os.Build.MODEL;
            String manufacturer = android.os.Build.MANUFACTURER;
            return (manufacturer + "/" + model);
        } catch (Exception e) {
            Timber.d(e, "getDeviceManufacturer() exception [%s]", e.getMessage());
        }
        return STR_EMPTY;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }
        return phrase.toString();
    }

    public static String getAndroidVersion() {
        try {
            String release = Build.VERSION.RELEASE;
            int sdkVersion = Build.VERSION.SDK_INT;
            return "Android " + sdkVersion + " (" + release + ")";
        } catch (Exception e) {
            Timber.d(e, "getAndroidVersion() exception [%s]", e.getMessage());
        }
        return STR_EMPTY;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /*public static boolean isTablet(Context context) {
        try {
            return context.getResources().getConfiguration().smallestScreenWidthDp >= 600;
        } catch (Exception e) {
            Timber.d(e, "isTablet exception [%s]", e.getMessage());
        }
        return false;
    }*/


}
