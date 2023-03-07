package com.vfi.android.payment.presentation.view.fragments.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.AndroidApplication;
import com.vfi.android.payment.presentation.internal.di.components.DaggerFragmentComponent;
import com.vfi.android.payment.presentation.internal.di.components.FragmentComponent;
import com.vfi.android.payment.presentation.presenters.base.Presenter;
import com.vfi.android.payment.presentation.view.contracts.base.UI;

public abstract class BaseMvpFragment<T extends UI> extends BaseFragment {
    private final String TAG = "BaseFragment";
    private T ui;
    private Presenter<T> presenter;
    private boolean isUIAttached = false;

    private boolean isDebugLifeCircle = true;

    public BaseMvpFragment() {
        FragmentComponent fragmentComponent;
        fragmentComponent = DaggerFragmentComponent.builder()
                .applicationComponent(AndroidApplication.getInstance().getApplicationComponent())
                .build();

        injectComponent(fragmentComponent);
        callSuperSetupPresenter();
    }

    public void setupPresenter(Presenter<T> presenter, T ui) {
        this.presenter = presenter;
        this.ui = ui;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isDebugLifeCircle) {
            LogUtil.d(TAG, "----onViewCreated=" + this.getClass().getSimpleName());
        }

        if (presenter != null) {
            presenter.attachUI(ui);
            isUIAttached = true;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (isDebugLifeCircle) {
            LogUtil.d(TAG, "---- onSaveInstanceState=" + this.getClass().getSimpleName());
        }

        if (presenter != null) {
            presenter.detachUI();
            isUIAttached = false;
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isDebugLifeCircle) {
            LogUtil.d(TAG, "---- onResume=" + this.getClass().getSimpleName());
        }

        if (!isUIAttached) {
            if (presenter != null) {
                presenter.attachUI(ui);
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (isDebugLifeCircle) {
            LogUtil.d(TAG, "---- onDestroyView=" + this.getClass().getSimpleName());
        }

        if (presenter != null) {
            presenter.detachUI();
            isUIAttached = false;
        }
        super.onDestroyView();
    }

    protected abstract void callSuperSetupPresenter();
    protected abstract void injectComponent(FragmentComponent fragmentComponent);
}
