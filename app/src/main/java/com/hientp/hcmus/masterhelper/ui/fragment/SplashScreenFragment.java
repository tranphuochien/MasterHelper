package com.hientp.hcmus.masterhelper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.hientp.hcmus.masterhelper.R;
import com.hientp.hcmus.masterhelper.ui.presenter.SplashScreenPresenter;
import com.hientp.hcmus.masterhelper.ui.view.ISplashScreenView;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by hientp on 28/01/2018.
 */

public class SplashScreenFragment extends BaseFragment implements ISplashScreenView {

    @Inject
    SplashScreenPresenter presenter;
    private boolean interstitialCanceled = false;

    public static SplashScreenFragment newInstance() {

        Bundle args = new Bundle();

        SplashScreenFragment fragment = new SplashScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupFragmentComponent() {
        getAppComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.activity_splashscreen;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() == null) {
            return;
        }
        boolean isTaskRoot = getActivity().isTaskRoot();

        if (!isTaskRoot) {
            getActivity().finish();
            return;
        }

        presenter.verifyUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (interstitialCanceled) {
            gotoHomeScreen();
        }
    }

    @Override
    public void onDestroyView() {
        presenter.detachView();
        super.onDestroyView();
    }

    @Override
    public void gotoHomeScreen() {
        interstitialCanceled = true;
        navigator.startHomeActivity(getContext());
        finishActivity();
    }

    @Override
    public void gotoLoginScreen() {
        //navigator.startLoginActivity(getContext());
        navigator.startHomeActivity(getContext());
        finishActivity();
    }

    private void finishActivity() {
        if (getActivity() == null) {
            return;
        }
        getActivity().finish();
    }
}

