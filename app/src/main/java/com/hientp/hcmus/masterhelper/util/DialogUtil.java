package com.hientp.hcmus.masterhelper.util;


import android.app.Activity;
import android.app.Dialog;
import android.view.KeyEvent;

import com.hientp.hcmus.masterhelper.R;
import com.hientp.hcmus.uiwidget.dialog.DialogManager;
import com.hientp.hcmus.uiwidget.dialog.SweetAlertDialog;
import com.hientp.hcmus.uiwidget.dialog.listener.OnEventConfirmDialogListener;
import com.hientp.hcmus.uiwidget.dialog.listener.OnEventDialogListener;
import com.hientp.hcmus.uiwidget.dialog.listener.OnEventUpdateListener;
import com.hientp.hcmus.uiwidget.dialog.listener.OnSweetDialogListener;

import timber.log.Timber;

/**
 * Created by root on 26/01/2018.
 */

public class DialogUtil {
    private DialogUtil() {
        // private constructor for utils class
    }

    private static Dialog mProgressDialog;


    public static void showNetworkErrorDialog(Activity activity,
                                              OnSweetDialogListener listener) {
        if (activity == null) {
            return;
        }
        DialogManager.showDrawableDialog(activity,
                activity.getString(R.string.exception_no_connection_try_again),
                R.drawable.ic_no_internet,
                listener,
                activity.getString(R.string.txt_close));
    }

    public static void showErrorDialog(Activity activity,
                                       String message,
                                       String cancelText,
                                       OnEventDialogListener cancelListener) {
        DialogManager.showSweetDialogCustom(activity,
                message,
                cancelText,
                SweetAlertDialog.ERROR_TYPE,
                cancelListener);

    }

    public static void showWarningDialog(Activity activity,
                                         String message,
                                         OnEventDialogListener cancelListener) {
        showWarningDialog(activity,
                message,
                activity.getString(R.string.txt_close),
                cancelListener);
    }

    public static void showWarningDialog(Activity activity,
                                         String message,
                                         String cancelBtnText,
                                         OnEventDialogListener cancelListener) {
        DialogManager.showSweetDialogCustom(activity,
                message,
                cancelBtnText,
                SweetAlertDialog.WARNING_TYPE,
                cancelListener);
    }

    public static void showNotificationDialog(Activity activity,
                                              String message) {
        showNotificationDialog(activity,
                message,
                null);
    }

    public static void showNotificationDialog(Activity activity,
                                              String message,
                                              OnEventDialogListener cancelListener) {
        showNotificationDialog(activity,
                message,
                activity.getString(R.string.txt_close),
                cancelListener);
    }

    public static void showNotificationDialog(Activity activity,
                                              String message,
                                              String btnCancel,
                                              OnEventDialogListener cancelListener) {
        if (activity == null) {
            return;
        }
        DialogManager.showSweetDialogCustom(activity,
                message,
                btnCancel,
                SweetAlertDialog.NORMAL_TYPE,
                cancelListener);
    }

    public static void showRetryDialog(Activity activity,
                                       String retryMessage,
                                       OnEventConfirmDialogListener retryListener) {
        DialogManager.showRetryDialog(activity,
                retryMessage,
                retryListener);
    }

    public static void showConfirmDialog(Activity activity,
                                         String pMessage,
                                         String pOKButton,
                                         String pCancelButton,
                                         OnEventConfirmDialogListener callback) {
        DialogManager.showConfirmDialog(activity,
                pMessage,
                pOKButton,
                pCancelButton,
                callback);
    }

    public static void showSuccessDialog(Activity activity,
                                         String message,
                                         OnEventDialogListener listener) {
        if (activity == null) {
            return;
        }
        DialogManager.showSweetDialogCustom(activity,
                message,
                activity.getString(R.string.txt_close),
                SweetAlertDialog.SUCCESS_TYPE,
                listener);
    }

    public static void showCustomDialog(Activity activity,
                                        String message,
                                        String cancelBtnText,
                                        int dialogType,
                                        OnEventDialogListener listener) {
        DialogManager.showSweetDialogCustom(activity, message, cancelBtnText, dialogType, listener);
    }

    static void showSweetDialogUpdate(Activity activity,
                                      String contentText,
                                      String newVersion,
                                      OnEventUpdateListener listener,
                                      boolean forceUpdate) {
        if (activity == null) {
            return;
        }
        if (forceUpdate) {
            DialogManager.showVersionUpdateDialog(activity,
                    contentText,
                    newVersion,
                    activity.getString(R.string.btn_update),
                    null,
                    listener);
        } else {
            DialogManager.showVersionUpdateDialog(activity,
                    contentText,
                    newVersion,
                    activity.getString(R.string.btn_update),
                    activity.getString(R.string.btn_cancel),
                    listener);
        }
    }

    public static void showNoticeDialog(Activity activity,
                                        String message,
                                        String btnConfirm,
                                        String btnCancel,
                                        OnEventConfirmDialogListener listener) {
        DialogManager.showConfirmDialog(activity, message, btnConfirm, btnCancel, listener);
    }

    public static void showCustomDialog(Activity activity,
                                        int dialogType,
                                        String message,
                                        OnSweetDialogListener listener,
                                        String[] btnNames) {
        DialogManager.showMultiButtonDialog(activity,
                dialogType,
                -1,
                message,
                listener,
                btnNames);
    }

    private static boolean isShowingLoading() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    public static void hideLoading() {
        Timber.d("hideLoading");
        try {
            if (isShowingLoading()
                    && mProgressDialog.getOwnerActivity() != null
                    && !mProgressDialog.getOwnerActivity().isFinishing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
                Timber.d("hide loading success.");
            } else {
                Timber.d("hide loading fail because dialog null or not showing.");
            }
        } catch (Exception e) {
            Timber.w(e, "hideLoading throw exception [%s]", e.getMessage());
        }
    }

    public static void showLoading(final Activity activity) {
        Timber.d("showLoading activity[%s]", activity);
        try {
            if (activity == null) {
                return;
            }
            if (isShowingLoading()) {
                if (mProgressDialog.getOwnerActivity() == activity) {
                    Timber.d("Loading is showing.");
                    return;
                } else {
                    hideLoading();
                }
            }
            mProgressDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE, R.style.alert_dialog_transparent);
            mProgressDialog.setOwnerActivity(activity);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setOnKeyListener((dialogInterface, keyCode, keyEvent) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (!activity.isFinishing()) {
                        hideLoading();
                        activity.onBackPressed();
                    }
                    return true;
                } else {
                    return false;
                }
            });
            if (!activity.isFinishing()) {
                Timber.d("show loading success.");
                mProgressDialog.show();
            } else {
                Timber.d("show loading fail because activity finishing.");
            }
        } catch (Exception e) {
            Timber.w(e, "showLoading throw exception [%s]", e.getMessage());
        }
    }


    public static SweetAlertDialog yesNoDialog(Activity pActivity, String pMessage, String pOKButton, String pCancelButton, final OnEventConfirmDialogListener callback) {
        SweetAlertDialog mDialog = new SweetAlertDialog(pActivity);
        mDialog.setContentHtmlText(pMessage);
        mDialog.setCancelText(pCancelButton);
        mDialog.setConfirmText(pOKButton);
        mDialog.setTitleText(pActivity.getString(R.string.dialog_title_confirm));
        mDialog.showCancelButton(true);
        mDialog.setCancelClickListener(sDialog -> {
            if (sDialog != null) {
                sDialog.dismiss();
            }

            if (callback != null) {
                callback.onCancelEvent();
            }

        }).setConfirmClickListener(sDialog -> {
            if (sDialog != null) {
                sDialog.dismiss();
            }

            if (callback != null) {
                callback.onOKEvent();
            }

        });
        return mDialog;
    }

    public static void closeAllDialog() {
        DialogManager.closeAllDialog();
    }
}