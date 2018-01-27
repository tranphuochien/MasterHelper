package com.hientp.hcmus.masterhelper.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hientp.hcmus.masterhelper.R;

import java.lang.ref.WeakReference;

/**
 * Created by hientp on 26/01/2018.
 */


public class ToastUtil {
    private static WeakReference<Toast> mToastReference;

    private ToastUtil() {
        // private constructor for utils class
    }

    public static void showToast(Context context, int message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, int message, int duration, int gravity) {
        showToast(context, context.getString(message, duration, gravity));
    }

    public static void showToast(Context context, int message, int duration) {
        showToast(context, message, duration, Gravity.CENTER);
    }

    public static void showToast(final Activity activity, final CharSequence message) {
        if (activity == null)
            return;
        activity.runOnUiThread(() -> showToast(activity.getApplicationContext(), message, Toast.LENGTH_SHORT));
    }

    public static void showToast(Context context, CharSequence message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, CharSequence message, int duration) {
        showToast(context, message, duration, Gravity.CENTER);
    }

    public static void showToast(Context context, CharSequence message, int duration, int gravity) {
        if (TextUtils.isEmpty(message)) {
            return;
        }

        Toast toast = Toast.makeText(context, message, duration);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }

    private static void showCustomToast(Context context, CharSequence message, int duration, int gravity, boolean isAllCaps) {
        if (mToastReference == null || mToastReference.get() == null) {
            mToastReference = new WeakReference<>(new Toast(context));
        }

        mToastReference.get().setGravity(gravity, 0, 0);
        mToastReference.get().setDuration(duration);
        View customView = View.inflate(context, R.layout.custom_toast, null);
        TextView mMessage = (TextView) customView.findViewById(R.id.tvMessage);
        mMessage.setText(message);
        mMessage.setAllCaps(isAllCaps);
        mToastReference.get().setView(customView);
        mToastReference.get().show();
    }

    public static void showCustomToast(Context context, CharSequence message) {
        showCustomToast(context, message, Toast.LENGTH_SHORT, Gravity.CENTER, true);
    }

    public static void showCustomToast(Context context, CharSequence message, boolean isAllCaps) {
        showCustomToast(context, message, Toast.LENGTH_SHORT, Gravity.CENTER, isAllCaps);
    }
}