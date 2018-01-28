package com.hientp.hcmus.masterhelper.ui.presenter;


import com.hientp.hcmus.masterhelper.app.ApplicationState;
import com.hientp.hcmus.masterhelper.ui.view.ISplashScreenView;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * Created by hientp on 1/28/18.
 * Splash startup
 */
@Singleton
public class SplashScreenPresenter extends AbstractPresenter<ISplashScreenView> {

    private final ApplicationState mApplicationState;

    @Inject
    SplashScreenPresenter(ApplicationState applicationState) {
        this.mApplicationState = applicationState;
    }

    public void verifyUser() {
        Timber.d("ApplicationState object [%s]", mApplicationState);

        /*if (!mUserConfig.hasCurrentUser()) {
            Timber.i("goto Login screen");
            mView.gotoLoginScreen();
            return;
        }

        User user = mUserConfig.getCurrentUser();
        if (user.profilelevel < User.MIN_PROFILE_LEVEL) {
            Timber.i("should update level");
            AndroidApplication.instance().getAppComponent().applicationSession().clearUserSession();
            return;
        }*/

        Timber.i("go to home screen");

        mView.gotoHomeScreen();
    }
}
