package com.vfi.android.payment.presentation.view.activities.base;

import android.os.Bundle;
import android.util.Log;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.AndroidApplication;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.internal.di.components.DaggerActivityComponent;
import com.vfi.android.payment.presentation.internal.di.modules.ActivityModule;
import com.vfi.android.payment.presentation.presenters.base.Presenter;
import com.vfi.android.payment.presentation.view.contracts.base.UI;

public abstract class BaseMvpActivity<T extends UI> extends BaseActivity {
    private final String TAG = TAGS.Navigator;
    private T ui;
    private Presenter<T> presenter;
    private boolean isUIAttached = false;

    private boolean isDebugLifeCircle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isDebugLifeCircle) {
            LogUtil.d(TAG, "----onCreate=" + this.getClass().getSimpleName());
        }
        super.onCreate(savedInstanceState);

        ActivityComponent activityComponent = DaggerActivityComponent
                .builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(((AndroidApplication)getApplication()).getApplicationComponent())
                .build();

        injectComponent(activityComponent);

        callSuperSetupPresenter();
    }

    protected void setupPresenter(Presenter<T> presenter, T ui) {
        this.presenter = presenter;
        this.ui = ui;
    }

    private void attachUI() {
        if (presenter != null) {
            presenter.attachUI(ui);
            isUIAttached = true;
        }
    }

    private void detachUI() {
        if (presenter != null) {
            presenter.detachUI();
            isUIAttached = false;
        }
    }

    @Override
    protected void onStart() {
        Log.d("dataxx", "onStart: remon");
        if (isDebugLifeCircle) {
            LogUtil.d(TAG, "----onStart=" + this.getClass().getSimpleName());
        }

        super.onStart();
        attachUI();
    }

    @Override
    protected void onResume() {
        if (isDebugLifeCircle) {
            LogUtil.d(TAG, "----onResume=" + this.getClass().getSimpleName());
        }

        if (!isUIAttached) {
            attachUI();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (isDebugLifeCircle) {
            LogUtil.d(TAG, "----onPause=" + this.getClass().getSimpleName());
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (isDebugLifeCircle) {
            LogUtil.d(TAG, "----onStop=" + this.getClass().getSimpleName());
        }
        detachUI();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (isDebugLifeCircle) {
            LogUtil.d(TAG, "----onSaveInstanceState=" + this.getClass().getSimpleName());
        }

        detachUI();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (isDebugLifeCircle) {
            LogUtil.d(TAG, "----onDestory=" + this.getClass().getSimpleName());
        }

        super.onDestroy();
    }

    public Presenter<T> getPresenter() {
        return presenter;
    }

    /**
     * Need extend subclass inject fields.
     */
    protected abstract void callSuperSetupPresenter();
    protected abstract void injectComponent(ActivityComponent activityComponent);
}
