package com.hientp.hcmus.masterhelper.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.hientp.hcmus.masterhelper.AndroidApplication;
import com.hientp.hcmus.masterhelper.R;
import com.hientp.hcmus.masterhelper.internal.di.components.ApplicationComponent;
import com.hientp.hcmus.masterhelper.navigation.Navigator;
import com.hientp.hcmus.masterhelper.ui.fragment.BaseFragment;
import com.hientp.hcmus.masterhelper.util.DialogUtil;
import com.hientp.hcmus.masterhelper.util.ToastUtil;
import com.hientp.hcmus.uiwidget.dialog.listener.OnEventDialogListener;
import com.hientp.hcmus.uiwidget.dialog.listener.OnSweetDialogListener;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();
    protected final EventBus mEventBus = getAppComponent().eventBus();
    protected final Navigator mNavigator = getAppComponent().navigator();
    private Unbinder unbinder;
    private boolean mResumed;

    @Nullable
    protected abstract BaseFragment getFragmentToHost();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResLayoutId());
        if (savedInstanceState == null) {
            hostFragment(getFragmentToHost());
        }

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected int getResLayoutId() {
        return R.layout.activity_common;
    }

    protected void hostFragment(BaseFragment fragment, int id) {
        if (fragment != null && getFragmentManager().findFragmentByTag(fragment.getTag()) == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(id, fragment, fragment.TAG);
            ft.commit();
        }
    }

    protected void hostFragment(BaseFragment fragment) {
        hostFragment(fragment, R.id.fragment_container);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mResumed = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mResumed = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        if (!mResumed) {
            return;
        }

        Fragment activeFragment = getActiveFragment();
        if (activeFragment instanceof BaseFragment) {
            if (((BaseFragment) activeFragment).onBackPressed()) {
                return;
            }
        }

        super.onBackPressed();
    }

    protected Fragment getActiveFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    public void showToast(String message) {
        ToastUtil.showToast(this, message);
    }

    public void showToast(int message) {
        ToastUtil.showToast(this, message);
    }

    protected ApplicationComponent getAppComponent() {
        return AndroidApplication.instance().getAppComponent();
    }

    public void showNetworkErrorDialog() {
        showNetworkErrorDialog(null);
    }

    public void showNetworkErrorDialog(OnSweetDialogListener listener) {
        DialogUtil.showNetworkErrorDialog(this, listener);
    }

    public void showCustomDialog(String message,
                                 String cancelBtnText,
                                 int dialogType,
                                 final OnEventDialogListener listener) {
        DialogUtil.showCustomDialog(this,
                message,
                cancelBtnText,
                dialogType,
                listener);
    }

    public void showNotificationDialog(String message) {
        DialogUtil.showNotificationDialog(this, message);
    }

    public void showWarningDialog(String message,
                                  final OnEventDialogListener cancelListener) {
        DialogUtil.showWarningDialog(this,
                message,
                cancelListener);
    }

    public void showErrorDialog(String message) {
        DialogUtil.showNotificationDialog(this, message, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Fragment activeFragment = getActiveFragment();
        if (activeFragment instanceof BaseFragment) {
            ((BaseFragment) activeFragment).onNewIntent(intent);
        }
    }
}
