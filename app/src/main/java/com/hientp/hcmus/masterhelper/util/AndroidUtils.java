package com.hientp.hcmus.masterhelper.util;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hientp.hcmus.masterhelper.AndroidApplication;
import com.hientp.hcmus.masterhelper.R;
import com.hientp.hcmus.utility.DeviceUtil;
import com.hientp.hcmus.utility.DimensionUtil;
import com.hientp.hcmus.utility.StorageUtil;
import com.hientp.hcmus.utility.Strings;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import timber.log.Timber;

public class AndroidUtils {
    public static final String TAG = "AndroidUtils";
    public static final int FLAG_TAG_BR = 1;
    public static final int FLAG_TAG_BOLD = 2;
    public static final int FLAG_TAG_COLOR = 4;
    public static final int FLAG_TAG_ALL = FLAG_TAG_BR | FLAG_TAG_BOLD | FLAG_TAG_COLOR;
    private static final Object smsLock = new Object();
    private static final String PREF_NAME = "PREF_UTILS";
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    private final static Object _lock = new Object();
    private static final String STR_EMPTY = "";
    private static final int INT_EMPTY = 0;
    private static final float FLOAT_EMPTY = 0;
    private static final float WEB_IF_SIZE = 20f;
    private static final long MAX_CACHE_SIZE = 1024 * 1024 * 100;
    public static Integer statusBarHeight;
    public static float density = 1;
    public static Point displaySize = new Point();
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static int leftBaseline;
    public static boolean usingHardwareInput;
    public static volatile Handler applicationHandler;
    private static boolean waitingForSms = false;
    private static Boolean isTablet = null;
    private static int adjustOwnerClassGuid = 0;
    private static String mDeviceId;
    private static String sUserAgent = null;

    static {
        density = AndroidApplication.instance().getResources().getDisplayMetrics().density;
        leftBaseline = DeviceUtil.isTablet(AndroidApplication.instance()) ? 80 : 72;
        checkDisplaySize();
        applicationHandler = new Handler(AndroidApplication.instance().getMainLooper());
    }

    private AndroidUtils() {
        // private constructor for utils class
    }

    public static String getScreenType() {
        try {
            return DimensionUtil.getScreenType(density);
        } catch (Exception e) {
            Timber.d(e, "getScreenType() exception [%s]", e.getMessage());
        }
        return STR_EMPTY;
    }

    public static String getCarrierName() {
        try {
            TelephonyManager manager = (TelephonyManager) AndroidApplication
                    .instance()
                    .getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (manager == null) {
                return STR_EMPTY;
            }
            return manager.getNetworkOperatorName();
        } catch (Exception e) {
            Timber.d(e, "getCarrierName() exception [%s]", e.getMessage());
        }
        return STR_EMPTY;
    }

    public static String getNetworkClass() {
        try {
            ConnectivityManager cm = (ConnectivityManager) AndroidApplication.instance()
                    .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) {
                return "Unknown";
            }
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null || !info.isConnected())
                return "Unknown"; //not connected
            if (info.getType() == ConnectivityManager.TYPE_WIFI)
                return "WIFI";
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                int networkType = info.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        return "GPRS";
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return "EDGE";
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        return "CDMA";
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        return "1xRTT";
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        return "2G";
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        return "UMTS";
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        return "EVDO_0";
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        return "EVDO_A";
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        return "HSDPA";
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        return "HSUPA";
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        return "HSPA";
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                        return "EVDO_B";
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                        return "EHRPD";
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        return "3G";
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        return "4G";
                    default:
                        return "Unknown";
                }
            }
            return "Unknown";
        } catch (Exception e) {
            Timber.d(e, "getNetworkClass() exception [%s]", e.getMessage());
        }
        return STR_EMPTY;
    }

    public static String getDeviceId() {
        try {
            if (mDeviceId == null)
                mDeviceId = getUUID();
            return mDeviceId;
        } catch (Exception e) {
            Timber.d(e, "getDeviceId() exception [%s]", e.getMessage());
        }
        return STR_EMPTY;
    }

    private static String getUUID() {
        try {
            SharedPreferences sharedPrefs = AndroidApplication.instance().getSharedPreferences(
                    PREF_NAME, Context.MODE_PRIVATE);
            String uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (TextUtils.isEmpty(uniqueID)) {
                uniqueID = UUID.randomUUID().toString();
                sharedPrefs.edit().putString(PREF_UNIQUE_ID, uniqueID).apply();
            }
            return uniqueID;
        } catch (Exception e) {
            Timber.d(e, "getUUID() exception [%s]", e.getMessage());
        }
        return STR_EMPTY;
    }

    public static void hideKeyboarInputMethod(Activity activity) {
        try {
            if (activity == null)
                return;
            // Check if no view has focus:
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            Timber.d(e, "hideKeyboarInputMethod exception [%s]", e.getMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static int getScreenWidth(Activity context) {
        try {
            int measuredWidth;
            WindowManager w = context.getWindowManager();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Point size = new Point();
                w.getDefaultDisplay().getSize(size);
                measuredWidth = size.x;
            } else {
                Display d = w.getDefaultDisplay();
                measuredWidth = d.getWidth();
            }
            return measuredWidth;
        } catch (Exception e) {
            Timber.d(e, "getScreenWidth exception [%s]", e.getMessage());
        }
        return INT_EMPTY;
    }

    /**
     * Get color string from color resource.
     *
     * @return color string (format #f0f0f0)
     */
    public static String getColorFromResource(@ColorRes int colorResource) {
        try {
            if (colorResource == 0)
                return null;
            return "#" + Integer.toHexString(ContextCompat.getColor(AndroidApplication.instance(),
                    colorResource));
        } catch (Exception e) {
            Timber.d(e, "getColorFromResource exception [%s]", e.getMessage());
        }
        return STR_EMPTY;
    }

    public static float dpToPixels(Context context, int dp) {
        try {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        } catch (Exception e) {
            Timber.d(e, "dpToPixels exception [%s]", e.getMessage());
        }
        return FLOAT_EMPTY;
    }

    public static int pixelsToDp(Context context, float pixels) {
        try {
            float density = context.getResources().getDisplayMetrics().densityDpi;
            return Math.round(pixels / (density / 160f));
        } catch (Exception e) {
            Timber.d(e, "pixelsToDp exception [%s]", e.getMessage());
        }
        return INT_EMPTY;
    }

    public static void sendMailTo(Context context, String extraSubject, String[] to, String title) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto:"));
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_SUBJECT, extraSubject);
            intent.putExtra(Intent.EXTRA_EMAIL, to);
            Intent mailer = Intent.createChooser(intent, title);
            StringBuilder strContent = new StringBuilder();
            strContent.append("To:");
            strContent.append(Arrays.toString(to));
            strContent.append(", title:");
            strContent.append(title);
            strContent.append(", extraSubject:");
            strContent.append(extraSubject);
//        ModuleCommon.instance().getGoogleAnalytics().sendGoogleAnalyticsHitEvents(AndroidUtils.class.getSimpleName(), "Email", "Send", strContent.toString());
            context.startActivity(mailer);
        } catch (Exception e) {
            Timber.d(e, "sendMailTo exception [%s]", e.getMessage());
        }
    }

    public static String getDensityType(Context context) {
        try {
            int densityDpi = context.getResources().getDisplayMetrics().densityDpi;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (densityDpi == DisplayMetrics.DENSITY_XXHIGH)
                    return "xxhdpi";
            }

            if (densityDpi == DisplayMetrics.DENSITY_HIGH) {
                return "hdpi";
            } else if (densityDpi == DisplayMetrics.DENSITY_MEDIUM) {
                return "mdpi";
            } else if (densityDpi == DisplayMetrics.DENSITY_LOW) {
                return "ldpi";
            } else {
                return "xhdpi";
            }
        } catch (Exception e) {
            Timber.d(e, "getDensityType exception [%s]", e.getMessage());
        }
        return STR_EMPTY;
    }

    public static boolean checkInstalledOrNot(Activity activity, String packageName) {
        try {
            if (activity == null)
                return false;

            PackageManager pm = activity.getPackageManager();
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            Timber.d(e, "checkInstalledOrNot exception [%s]", e.getMessage());
        }
        return false;
    }

    public static void openAppInfo(Context context, String packageName) {
        try {
            String SCHEME = "package";
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e) {
            Timber.d(e, "openAppInfo exception [%s]", e.getMessage());
        }
    }

    public static void openBrowser(Context context, String url) {
        try {
            if (context == null)
                return;

            if (TextUtils.isEmpty(url))
                return;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                ToastUtil.showToast(context, context.getResources().getString(R.string.miss_browser));
            }
        } catch (Exception e) {
            Timber.d(e, "openBrowser exception [%s]", e.getMessage());
        }
    }

    public static void openPlayStore(Context context, String packageName) {
        try {
            Timber.tag("AndroidUtils").d("openPlayStore============packageName:" + packageName);
            if (context == null)
                return;

            String appName = "context.getResources().getString(R.string.miss_browser)";
            Uri uriUrl = Uri
                    .parse("market://details?id="
                            + packageName
                            + "&referrer=utm_source%3D"
                            + appName
                            + "%26utm_medium%3Dandroid-app%26utm_campaign%3Dcross-promote");
            Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                ToastUtil.showToast(context, context.getResources().getString(R.string.miss_playstore));
            }
        } catch (Exception e) {
            Timber.d(e, "openPlayStore exception [%s]", e.getMessage());
            ToastUtil.showToast(context, context.getResources().getString(R.string.miss_playstore));
        }
    }

    public static boolean isMainThread() {
        boolean ret = false;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ret = true;
        }
        Timber.d(ret ? " Current Thread is Main Thread " : " Current Thread is Background Thread ");
        return ret;
    }

    public static void requestAdjustResize(Activity activity, int classGuid) {
        try {
            if (activity == null || DeviceUtil.isTablet(activity)) {
                return;
            }
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            adjustOwnerClassGuid = classGuid;
        } catch (Exception e) {
            Timber.d(e, "requestAdjustResize exception [%s]", e.getMessage());
        }
    }

    public static void removeAdjustResize(Activity activity, int classGuid) {
        try {
            if (activity == null || DeviceUtil.isTablet(activity))
                return;

            if (adjustOwnerClassGuid == classGuid)
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        } catch (Exception e) {
            Timber.d(e, "removeAdjustResize exception [%s]", e.getMessage());
        }
    }

    public static boolean isWaitingForSms() {
        boolean value;
        synchronized (smsLock) {
            value = waitingForSms;
        }
        return value;
    }

    public static void setWaitingForSms(boolean value) {
        synchronized (smsLock) {
            waitingForSms = value;
        }
    }

    public static void showKeyboard(View view) {
        try {
            if (view == null)
                return;

            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            Timber.d(e, "showKeyboard exception [%s]", e.getMessage());
        }
    }

    public static boolean isKeyboardShowed(View rootView) {
        try {
            /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
            final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            //DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
            /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
            int heightDiff = rootView.getBottom() - r.bottom;
            /* Threshold size: dp to pixels, multiply with display density */
            return heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * density;
        } catch (Exception e) {
            Timber.d(e, "isKeyboardShowed exception [%s]", e.getMessage());
        }
        return false;
    }

    public static void hideKeyboard(View view) {
        try {
            if (view == null)
                return;

            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null || !imm.isActive())
                return;

            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            Timber.d(e, "hideKeyboard exception [%s]", e.getMessage());
        }
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null || inputMethodManager == null) return;
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            Timber.d(e, "hideKeyboard exception [%s]", e.getMessage());
        }
    }

    public static File getCacheDir(Context context) {
        try {
            String state = Environment.getExternalStorageState();
            if (state == null || state.startsWith(Environment.MEDIA_MOUNTED)) {
                File file = context.getExternalCacheDir();
                if (file != null)
                    return file;
            }
            File file = context.getCacheDir();
            if (file != null) {
                return file;
            }
        } catch (Exception e) {
            Timber.d(e, "getCacheDir exception [%s]", e.getMessage());
        }
        return new File("");
    }

    public static int dp(float value) {
        try {
            if (value == 0)
                return 0;

            return (int) Math.ceil(density * value);
        } catch (Exception e) {
            Timber.d(e, "dp exception [%s]", e.getMessage());
        }
        return INT_EMPTY;
    }

    public static float dpf2(float value) {
        try {
            if (value == 0)
                return 0;

            return density * value;
        } catch (Exception e) {
            Timber.d(e, "dpf2 exception [%s]", e.getMessage());
        }
        return FLOAT_EMPTY;
    }

    public static void checkDisplaySize() {
        try {
            Configuration configuration = AndroidApplication.instance().getResources().getConfiguration();
            usingHardwareInput = configuration.keyboard != Configuration.KEYBOARD_NOKEYS && configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO;
            WindowManager manager = (WindowManager) AndroidApplication.instance().getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                }
            }
        } catch (Exception e) {
            Timber.d(e, "checkDisplaySize exception [%s]", e.getMessage());
        }
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        // synchronized (_lock) {
        if (delay == 0) {
            applicationHandler.post(runnable);
        } else {
            applicationHandler.postDelayed(runnable, delay);
        }
        // }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        applicationHandler.removeCallbacks(runnable);
    }


    public static boolean isSmallTablet() {
        try {
            float minSide = Math.min(displaySize.x, displaySize.y) / density;
            return minSide <= 700;
        } catch (Exception e) {
            Timber.d(e, "isSmallTablet exception [%s]", e.getMessage());
        }
        return false;
    }

    public static int getMinTabletSide() {
        try {
            if (!isSmallTablet()) {
                int smallSide = Math.min(displaySize.x, displaySize.y);
                int leftSide = smallSide * 35 / 100;
                if (leftSide < dp(320)) {
                    leftSide = dp(320);
                }
                return smallSide - leftSide;
            } else {
                int smallSide = Math.min(displaySize.x, displaySize.y);
                int maxSide = Math.max(displaySize.x, displaySize.y);
                int leftSide = maxSide * 35 / 100;
                if (leftSide < dp(320)) {
                    leftSide = dp(320);
                }
                return Math.min(smallSide, maxSide - leftSide);
            }
        } catch (Exception e) {
            Timber.d(e, "getMinTabletSide exception [%s]", e.getMessage());
        }
        return INT_EMPTY;
    }

    public static void clearCursorDrawable(EditText editText) {
        try {
            if (editText == null)
                return;
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.setInt(editText, 0);
        } catch (Exception e) {
            Timber.d(e, "clearCursorDrawable exception [%s]", e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    public static void clearDrawableAnimation(View view) {
        try {
            if (Build.VERSION.SDK_INT < 21 || view == null)
                return;

            Drawable drawable;
            if (view instanceof ListView) {
                drawable = ((ListView) view).getSelector();
                if (drawable != null) {
                    drawable.setState(StateSet.NOTHING);
                }
            } else {
                drawable = view.getBackground();
                if (drawable != null) {
                    drawable.setState(StateSet.NOTHING);
                    drawable.jumpToCurrentState();
                }
            }
        } catch (Exception e) {
            Timber.d(e, "clearDrawableAnimation exception [%s]", e.getMessage());
        }
    }

    public static void addMediaToGallery(String fromPath) {
        try {
            if (fromPath == null)
                return;

            File f = new File(fromPath);
            Uri contentUri = Uri.fromFile(f);
            addMediaToGallery(contentUri);
        } catch (Exception e) {
            Timber.d(e, "addMediaToGallery exception [%s]", e.getMessage());
        }
    }

    public static void addMediaToGallery(Uri uri) {
        try {
            if (uri == null)
                return;
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(uri);
            AndroidApplication.instance().sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            Timber.d(e, "addMediaToGallery exception [%s]", e.getMessage());
        }
    }

    @SuppressLint("DefaultLocale")
    public static String formatFileSize(long size) {
        try {
            if (size < 1024) {
                return String.format("%d B", size);
            } else if (size < 1024 * 1024) {
                return String.format("%.1f KB", size / 1024.0f);
            } else if (size < 1024 * 1024 * 1024) {
                return String.format("%.1f MB", size / 1024.0f / 1024.0f);
            } else {
                return String.format("%.1f GB", size / 1024.0f / 1024.0f / 1024.0f);
            }
        } catch (Exception e) {
            Timber.d(e, "formatFileSize exception [%s]", e.getMessage());
        }

        return STR_EMPTY;
    }

    public static byte[] decodeQuotedPrintable(final byte[] bytes) {
        try {
            if (bytes == null)
                return null;

            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (int i = 0; i < bytes.length; i++) {
                final int b = bytes[i];
                if (b == '=') {
                    try {
                        final int u = Character.digit((char) bytes[++i], 16);
                        final int l = Character.digit((char) bytes[++i], 16);
                        buffer.write((char) ((u << 4) + l));
                    } catch (Exception e) {

                        return null;
                    }
                } else {
                    buffer.write(b);
                }
            }
            byte[] array = buffer.toByteArray();
            buffer.close();
            return array;
        } catch (Exception e) {
            Timber.d(e, "decodeQuotedPrintable exception [%s]", e.getMessage());
        }
        return null;
    }

    public static boolean copyFile(InputStream sourceFile, File destFile) throws IOException {
        OutputStream out = new FileOutputStream(destFile);
        byte[] buf = new byte[4096];
        int len;
        while ((len = sourceFile.read(buf)) > 0) {
            Thread.yield();
            out.write(buf, 0, len);
        }
        out.close();
        return true;
    }

    public static boolean copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileInputStream source = null;
        FileOutputStream destination = null;
        try {
            source = new FileInputStream(sourceFile);
            destination = new FileOutputStream(destFile);
            destination.getChannel().transferFrom(source.getChannel(), 0, source.getChannel().size());
        } catch (Exception e) {
            Timber.d(e, "copyFile exception [%s]", e.getMessage());
            return false;
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
        return true;
    }

    public static String createTempImageFile(Context context, Uri uri) {
        String filePath;
        InputStream inputStream = null;
        BufferedOutputStream outStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            File extDir = context.getExternalCacheDir();
            filePath = extDir.getAbsolutePath() + "/CachedImg_" + UUID.randomUUID().toString() + ".jpg";
            outStream = new BufferedOutputStream(new FileOutputStream
                    (filePath));

            byte[] buf = new byte[2048];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }

        } catch (IOException e) {
            Timber.d(e, "createTempImageFile exception [%s]", e.getMessage());
            filePath = "";
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Timber.d(e, "createTempImageFile exception [%s]", e.getMessage());
            }
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                Timber.d(e, "createTempImageFile exception [%s]", e.getMessage());
            }
        }
        return filePath;
    }

    public static int getStatusBarHeight(Context context) {
        try {
            if (statusBarHeight != null)
                return statusBarHeight;

            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            } else {
                statusBarHeight = 0;
            }

            return statusBarHeight;
        } catch (Exception e) {
            Timber.d(e, "getStatusBarHeight exception [%s]", e.getMessage());
        }

        return INT_EMPTY;
    }

    public static void animateLike(View v) {
        try {
            ScaleAnimation scal = new ScaleAnimation(1f, 1.8f, 1, 1.8f, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
            scal.setDuration(250);
            scal.setFillAfter(true);
            scal.setRepeatCount(1);
            scal.setRepeatMode(Animation.REVERSE);
            v.startAnimation(scal);
        } catch (Exception e) {
            Timber.d(e, "animateLike exception [%s]", e.getMessage());
        }
    }

    public static void deleteFile(String path) {
        try {
            File oldDir = new File(path);
            if (oldDir.exists()) {
                deleteRecursive(oldDir);
            }
        } catch (Exception e) {
            Timber.d(e, "deleteFile exception [%s]", e.getMessage());
        }
    }

    public static void deleteRecursive(File fileOrDirectory) {
        try {
            if (fileOrDirectory.isDirectory()) {
                for (File child : fileOrDirectory.listFiles()) {
                    deleteRecursive(child);
                }
            }
            fileOrDirectory.delete();
        } catch (Exception e) {
            Timber.d(e, "deleteRecursive exception [%s]", e.getMessage());
        }
    }

    public static void writeToFile(final String fileContents, String path) throws IOException {
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(path), Strings.charsetUTF8());
            out.write(fileContents);
        } catch (Exception e) {
            Timber.d(e, "writeToFile exception [%s]", e.getMessage());
        } finally {
            if (out != null)
                out.close();
        }
    }

    public static String readFromFile(String filePath) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;
        InputStreamReader fr = null;
        try {
            fr = new InputStreamReader(new FileInputStream(filePath), Strings.charsetUTF8());
            in = new BufferedReader(fr);
            while ((line = in.readLine()) != null) stringBuilder.append(line);

        } catch (Exception e) {
            Timber.d(e, "readFromFile exception [%s]", e.getMessage());
        } finally {
            if (in != null) in.close();
            if (fr != null) fr.close();
        }
        return stringBuilder.toString();
    }

    public static boolean unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[1024];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs()) {
                    return false;
                }
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } catch (Exception e) {
            Timber.d(e, "unzip exception [%s]", e.getMessage());
        } finally {
            zis.close();
        }
        return true;
    }

    public static boolean unzip(String source, String destinationDirectory) throws IOException {
        return unzip(new File(source), new File(destinationDirectory));
    }

    public static String unzip2(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        String dirPath = null;
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[1024 * 2];

            boolean isSetPath = false;

            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir;
                if (ze.isDirectory()) {
                    if (!isSetPath) {
                        isSetPath = true;
                        dirPath = targetDirectory.getAbsolutePath() + "/" + ze.getName();
                    }
                    dir = file;
                } else {
                    dir = file.getParentFile();
                }

                if (!dir.isDirectory() && !dir.mkdirs()) {
                    return null;
                }
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zis.close();
        }

        return dirPath;
    }

    public static String unzip2(String source, String destinationDirectory) throws IOException {
        return unzip2(new File(source), new File(destinationDirectory));
    }

    public static String readFileFromAsset(Context context, String fileName) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets().open(fileName);
            isr = new InputStreamReader(fIn, Strings.charsetUTF8());
            input = new BufferedReader(isr);
            String line;
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            Timber.d(e, "readFileFromAsset exception [%s]", e.getMessage());
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e) {
                Timber.d(e, "readFileFromAsset exception [%s]", e.getMessage());
            }
        }
        return returnString.toString();
    }

    public static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName) {
        try {
            final Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);
            replaceFont(staticTypefaceFieldName, regular);
        } catch (Exception e) {
            Timber.d(e, "setDefaultFont exception [%s]", e.getMessage());
        }
    }

    private static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                Map<String, Typeface> newMap = new HashMap<>();
                newMap.put("sans-serif", newTypeface);
                final Field staticField = Typeface.class.getDeclaredField("sSystemFontMap");
                staticField.setAccessible(true);
                staticField.set(null, newMap);

            } else {
                final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
                staticField.setAccessible(true);
                staticField.set(null, newTypeface);
            }
        } catch (Exception e) {
            Timber.d(e, "replaceFont exception [%s]", e.getMessage());
        }
    }

    public static void setSpannedMessageToView(TextView tv,
                                               String message,
                                               String spannedMessage,
                                               boolean isUnderline,
                                               boolean isMessageBold,
                                               @ColorInt int linkColor,
                                               ClickableSpan clickableSpan) {
        try {
            if (tv != null) {
                // set spannable for text view
                int startIndex = message.indexOf("%s");
                int endIndex = startIndex + spannedMessage.length();
                message = String.format(message, spannedMessage);
                Spannable span = Spannable.Factory.getInstance().newSpannable(message);
                // set span underline
                if (isUnderline) {
                    span.setSpan(new UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                // set bold message
                if (isMessageBold) {
                    span.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                span.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // set span color
                span.setSpan(new ForegroundColorSpan(linkColor), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(span);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        } catch (Exception e) {
            Timber.d(e, "setSpannedMessageToView exception [%s]", e.getMessage());
        }
    }

    public static void setSpannedMessageToView(TextView tv,
                                               int message,
                                               int spannedMessage,
                                               boolean isUnderline,
                                               boolean isMessageBold,
                                               @ColorRes int linkColorResId,
                                               ClickableSpan clickableSpan) {
        try {
            Context context = AndroidApplication.instance();
            setSpannedMessageToView(tv, context.getString(message),
                    context.getString(spannedMessage),
                    isUnderline, isMessageBold, context.getResources().getColor(linkColorResId), clickableSpan);
        } catch (Exception e) {
            Timber.d(e, "setSpannedMessageToView exception [%s]", e.getMessage());
        }
    }

    public static int getFrontCameraId(CameraManager cManager) {
        try {
            if (Build.VERSION.SDK_INT < 22) {
                Camera.CameraInfo ci = new Camera.CameraInfo();
                for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                    Camera.getCameraInfo(i, ci);
                    if (ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) return i;
                }
            } else {
                for (int j = 0; j < cManager.getCameraIdList().length; j++) {
                    String[] cameraId = cManager.getCameraIdList();
                    CameraCharacteristics characteristics = cManager.getCameraCharacteristics(cameraId[j]);
                    Integer cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (cOrientation != null && cOrientation == CameraCharacteristics.LENS_FACING_FRONT)
                        return j;
                }
            }
        } catch (Exception e) {
            Timber.d(e, "getFrontCameraId exception [%s]", e.getMessage());
        }

        return -1; // No front-facing camera found
    }

    public static void validateMainThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Must be called from the main thread.");
        }
    }

    public static boolean isHttpRequest(String input) {
        try {
            String URL_REGEX = "^((https?|http)://|www\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
            Pattern pattern = Pattern.compile(URL_REGEX);
            Matcher matcher = pattern.matcher(input);
            return matcher.find();
        } catch (Exception e) {
            Timber.d(e, "isHttpRequest exception [%s]", e.getMessage());
        }
        return false;
    }

    public static boolean checkAndroidMVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isXiaomiDevice() {
        try {
            return DeviceUtil.getDeviceName().toUpperCase().matches("(.*)XIAOMI(.*)");
        } catch (Exception e) {
            Timber.d(e, "isXiaomiDevice() exception [%s]", e.getMessage());
        }
        return false;
    }

    public static boolean isHexColorString(String input) {
        Pattern colorPattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})");
        Matcher m = colorPattern.matcher(input);
        return m.matches();
    }

    private static int getWebIconUrlSize(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int density = metrics.densityDpi;
        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
                return 15;

            case DisplayMetrics.DENSITY_MEDIUM:
                return 20;

            case DisplayMetrics.DENSITY_HIGH:
                return 35;

            case DisplayMetrics.DENSITY_260:
            case DisplayMetrics.DENSITY_280:
            case DisplayMetrics.DENSITY_300:
            case DisplayMetrics.DENSITY_XHIGH:
                return 55;

            case DisplayMetrics.DENSITY_340:
            case DisplayMetrics.DENSITY_360:
            case DisplayMetrics.DENSITY_400:
            case DisplayMetrics.DENSITY_420:
            case DisplayMetrics.DENSITY_XXHIGH:
                return 75;

            case DisplayMetrics.DENSITY_560:
            case DisplayMetrics.DENSITY_XXXHIGH:
                return 90;

            default:
                return 35;
        }
    }

    //
    public static List<Long> convertArrToList(int[] pArrData) {
        List<Long> excludeAppID = new ArrayList<>();
        for (int appId : pArrData) {
            excludeAppID.add((long) appId);
        }

        return excludeAppID;
    }

    //get 10% total storage memory size
    public static long getMemoryCacheSize() {
        try {
            long cacheSize = Long.parseLong(StorageUtil.getTotalExternalMemorySize()) * 10 / 100;
            if (cacheSize <= MAX_CACHE_SIZE) {
                return cacheSize;
            }
        } catch (Exception e) {
            Timber.d("Exception parse memory size to long");
        }
        return MAX_CACHE_SIZE;
    }


    /**
     * Prevent user capture screen of activity
     *
     * @param activity
     */
    public static void disableScreenCapture(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        Window window = activity.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }
}