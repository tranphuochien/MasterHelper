package com.hientp.hcmus.masterhelper.ui.activity;

import com.hientp.hcmus.masterhelper.ui.fragment.BaseFragment;
import com.hientp.hcmus.masterhelper.ui.fragment.SplashScreenFragment;

/**
 * Created by hientp on 28/01/2018.
 */

public class SplashScreenActivity extends BaseActivity {

    @Override
    public BaseFragment getFragmentToHost() {
        return SplashScreenFragment.newInstance();
    }


    @Override
    public void onBackPressed() {
        // empty
    }
}
