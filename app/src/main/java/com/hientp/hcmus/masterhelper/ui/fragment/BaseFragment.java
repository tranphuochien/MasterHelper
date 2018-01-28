package com.hientp.hcmus.masterhelper.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.hientp.hcmus.masterhelper.AndroidApplication;
import com.hientp.hcmus.masterhelper.R;
import com.hientp.hcmus.masterhelper.internal.di.components.ApplicationComponent;
import com.hientp.hcmus.masterhelper.navigation.Navigator;
import com.hientp.hcmus.masterhelper.util.DialogUtil;
import com.hientp.hcmus.masterhelper.util.ToastUtil;
import com.hientp.hcmus.uiwidget.dialog.SweetAlertDialog;
import com.hientp.hcmus.uiwidget.dialog.listener.OnEventConfirmDialogListener;
import com.hientp.hcmus.uiwidget.dialog.listener.OnEventDialogListener;
import com.hientp.hcmus.uiwidget.dialog.listener.OnSweetDialogListener;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by hientp on 26/01/2018.
 * *
 */
public abstract class BaseFragment extends Fragment {

    public final String TAG = getClass().getSimpleName();
    protected final Navigator navigator = AndroidApplication.instance().getAppComponent().navigator();
    private Snackbar mSnackBar;
    private SweetAlertDialog mProgressDialog;
    private Unbinder unbinder;
    private Handler mShowLoadingTimeoutHandler;
    private Runnable mShowLoadingTimeoutRunnable;

    protected abstract void setupFragmentComponent();

    protected abstract int getResLayoutId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getResLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        setupFragmentComponent();
        return view;
    }

    @Override
    public void onDestroyView() {
        hideKeyboard();
        cancelShowLoadingTimeoutRunnable();
        hideProgressDialog();
        super.onDestroyView();
        mShowLoadingTimeoutHandler = null;
        mShowLoadingTimeoutRunnable = null;
        mProgressDialog = null;
        unbinder.unbind();
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void showSnackbar(int message, View.OnClickListener listener) {
        hideSnackbar();
        if (getActivity() == null) {
            return;
        }
        mSnackBar = Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        if (listener != null) mSnackBar.setAction(R.string.retry, listener);
        mSnackBar.show();
    }

    public void showNetworkError() {
        showSnackbar(R.string.exception_no_connection, null);
    }

    public void hideSnackbar() {
        if (mSnackBar != null) mSnackBar.dismiss();
    }

    public boolean isShowingLoading() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    public void hideProgressDialog() {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog.dismiss();
        cancelShowLoadingTimeoutRunnable();
    }

    public void showProgressDialog() {
        cancelShowLoadingTimeoutRunnable();
        if (mProgressDialog == null) {
            mProgressDialog = new SweetAlertDialog(getContext(),
                    SweetAlertDialog.PROGRESS_TYPE, R.style.alert_dialog_transparent);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setOnKeyListener((dialogInterface, keyCode, keyEvent) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing()) {
                        activity.onBackPressed();
                    }
                    return true;
                } else {
                    return false;
                }
            });
        }
        if (!isShowingLoading()) {
            mProgressDialog.show();
        }
    }

    public void showProgressDialog(final long timeout) {
        showProgressDialog();
        startShowLoadingTimeoutRunnable(timeout);
    }

    public void showProgressDialogWithTimeout() {
        showProgressDialog(35000);
    }

    private void startShowLoadingTimeoutRunnable(final long timeout) {
        if (timeout <= 0) {
            return;
        }
        if (mShowLoadingTimeoutHandler == null) {
            mShowLoadingTimeoutHandler = new Handler();
        }
        if (mShowLoadingTimeoutRunnable == null) {
            mShowLoadingTimeoutRunnable = new Runnable() {
                @Override
                public void run() {
                    onTimeoutLoading(timeout);
                }
            };
        }
        mShowLoadingTimeoutHandler.postDelayed(mShowLoadingTimeoutRunnable, timeout);
    }

    private void cancelShowLoadingTimeoutRunnable() {
        if (mShowLoadingTimeoutHandler != null && mShowLoadingTimeoutRunnable != null) {
            mShowLoadingTimeoutHandler.removeCallbacks(mShowLoadingTimeoutRunnable);
        }
    }

    protected void onTimeoutLoading(long timeout) {
        Timber.d("time out show loading");
        if (isShowingLoading()) {
            hideProgressDialog();
        }
    }

    public void showNetworkErrorDialog() {
        showNetworkErrorDialog(null);
    }

    public void showNetworkErrorDialog(OnSweetDialogListener listener) {
        DialogUtil.showNetworkErrorDialog(getActivity(), listener);
    }

    public void showWarningDialog(String message,
                                  OnEventDialogListener cancelListener) {
        DialogUtil.showWarningDialog(getActivity(), message, cancelListener);
    }

    public void showWarningDialog(String message,
                                  String cancelBtnText,
                                  final OnEventDialogListener cancelListener) {
        DialogUtil.showWarningDialog(getActivity(),
                message,
                cancelBtnText,
                cancelListener);
    }

    public void showNotificationDialog(String message) {
        showErrorDialog(message,
                getString(R.string.txt_close),
                null);
    }

    public void showErrorDialog(String message) {
        showErrorDialog(message,
                getString(R.string.txt_close),
                null);
    }

    public void showErrorDialog(String message,
                                final OnEventDialogListener cancelListener) {
        DialogUtil.showNotificationDialog(getActivity(),
                message,
                getString(R.string.txt_close),
                cancelListener);
    }

    public void showErrorDialog(String message,
                                String cancelText,
                                final OnEventDialogListener cancelListener) {
        DialogUtil.showNotificationDialog(getActivity(),
                message,
                cancelText,
                cancelListener);
    }

    public void showConfirmDialog(String pMessage,
                                  String pOKButton,
                                  String pCancelButton,
                                  final OnEventConfirmDialogListener callback) {
        DialogUtil.showConfirmDialog(getActivity(),
                pMessage,
                pOKButton,
                pCancelButton,
                callback);
    }

    public void showSuccessDialog(String message, OnEventDialogListener listener) {
        DialogUtil.showSuccessDialog(getActivity(), message, listener);
    }


    public ApplicationComponent getAppComponent() {
        return AndroidApplication.instance().getAppComponent();
    }

    public void showToast(String message) {
        ToastUtil.showToast(getActivity(), message);
    }

    public void showToast(int message) {
        ToastUtil.showToast(getActivity(), message);
    }

    public void hideKeyboard() {
        if (getView() == null) {
            return;
        }
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }
}
