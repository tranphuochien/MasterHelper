package com.hientp.hcmus.masterhelper;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.hientp.hcmus.masterhelper.ui.activity.BaseActivity;
import com.hientp.hcmus.masterhelper.ui.fragment.BaseFragment;
import com.hientp.hcmus.masterhelper.util.ToastUtil;
import com.hientp.hcmus.uiwidget.dialog.listener.OnEventDialogListener;

import timber.log.Timber;

public class MainActivity extends BaseActivity {

    @Nullable
    @Override
    protected BaseFragment getFragmentToHost() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClick(View view) {
        ToastUtil.showCustomToast(getBaseContext(), "demo");
    }
}
