package com.vfi.android.payment.presentation.navigation;

import android.content.Context;


import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.interactor.deviceservice.UseCaseImportCardConfirmResult;
import com.vfi.android.domain.interactor.deviceservice.UseCaseIsNeedRemoveCard;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.view.activities.MainMenuActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UINavigator {
    private final String TAG = TAGS.Navigator;
    private UIFlowControlData uiFlowControlData;
    private TransUiFlow transUiFlow;
    private String lastUIName = null;

    @Inject
    UseCaseIsNeedRemoveCard useCaseIsNeedRemoveCard;
    private int exceptionCount = 0;

    @Inject
    public UINavigator() {
    }

    public synchronized void navigatorToNextUI(Context context) {
        LogUtil.d("dataxx", "transUiFlow=" + (transUiFlow == null ? "null" : "not null"));
        if (transUiFlow != null) {
            LogUtil.d(TAG, "transUiFlow = " + transUiFlow.getClass().getSimpleName());
            exceptionCount = 0;
        } else {
            LogUtil.d(TAG, "Something is wrong, exceptionCount=[" + exceptionCount + "]");
            exceptionCount ++;
            if (exceptionCount > 5) {
                LogUtil.d(TAG, "Something is wrong, back to mainMenu");
                AndroidUtil.startActivity(context, MainMenuActivity.class);
            }
        }

        if (transUiFlow != null) {
            if (uiFlowControlData != null) {
                uiFlowControlData.setCurrentUIContext(context);
            }

            if (isTwiceDoNextUIOnSameUI(context)) {
                return;
            }

            transUiFlow.navigatorToNext(context, this);
        }
    }

    private boolean isTwiceDoNextUIOnSameUI(Context context) {
        String currentUIName = context.getClass().getName();

        if (uiFlowControlData.isNeedUIBack() || uiFlowControlData.isGoBackToMainMenu()
                || uiFlowControlData.isUIStateStackEmpty()) {
            lastUIName = null;
            return false;
        }

        LogUtil.d(TAG, "LastUIname=" + lastUIName);
        LogUtil.d(TAG, "CurrentUIName=" + currentUIName);

        if (lastUIName != null && lastUIName.equals(currentUIName)) {
            LogUtil.d(TAG, "Same activity [" +  lastUIName + "] not allow do twice NextUI");
            return true;
        }

        lastUIName = currentUIName;

        return false;
    }

    public void resetUIFlowControlData() {
        uiFlowControlData = new UIFlowControlData();
    }

    public UIFlowControlData getUiFlowControlData() {
        return uiFlowControlData;
    }

    public void setTransUiFlow(TransUiFlow transUiFlow) {
        LogUtil.d(TAG, "setTransUiFlow = " + transUiFlow);
        this.transUiFlow = transUiFlow;
    }

    public boolean isNeedRemoveCard() {
        return useCaseIsNeedRemoveCard.execute(null);
    }
}
