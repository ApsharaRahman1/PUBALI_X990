package com.vfi.android.payment.presentation.transflows;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;


import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.interactor.deviceservice.UseCaseImportCardConfirmResult;
import com.vfi.android.domain.interactor.deviceservice.UseCaseWaitingCardRemoved;
import com.vfi.android.domain.interactor.repository.UseCaseSetDoingTransactionFlag;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.AndroidApplication;
import com.vfi.android.payment.presentation.internal.di.components.CommonComponent;
import com.vfi.android.payment.presentation.internal.di.components.DaggerCommonComponent;
import com.vfi.android.payment.presentation.navigation.UIFlowControlData;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.navigation.UIState;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.ToastUtil;
import com.vfi.android.payment.presentation.view.activities.TransSuccessActivity;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_TRANS_END;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_TRANS_ENTRY;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_TRANS_SUCCESS;

public class BaseTransFlow {
    protected final String TAG = TAGS.Navigator;
    private UIWatchDog uiWatchDog;
    private Dialog customDialog = null;
    private Dialog confirmCancelDialog = null;

    @Inject
    UseCaseImportCardConfirmResult useCaseImportCardConfirmResult;
    @Inject
    UseCaseWaitingCardRemoved useCaseWaitingCardRemoved;
    @Inject
    UseCaseSetDoingTransactionFlag useCaseSetDoingTransactionFlag;
    @Inject
    UINavigator uiNavigator;

    public BaseTransFlow() {
        CommonComponent commonComponent;
        commonComponent = DaggerCommonComponent.builder()
                .applicationComponent(AndroidApplication.getInstance().getApplicationComponent())
                .build();
        commonComponent.inject(this);
    }


    public boolean isCorrectUIEntry(Context context, UIFlowControlData controlData, Class entryActivityClass) {
        if (controlData.isUIStateStackEmpty()) {
            controlData.pushUIState(UI_STATE_TRANS_ENTRY);

            useCaseSetDoingTransactionFlag.asyncExecuteWithoutResult(true);
            if (uiWatchDog != null) {
                uiWatchDog.stopUIWatch();
            }
            uiWatchDog = new UIWatchDog(controlData, entryActivityClass);
            uiWatchDog.startUIWatch();
        } else {
            if (uiWatchDog == null) {
                uiWatchDog = new UIWatchDog(controlData, entryActivityClass);
                uiWatchDog.startUIWatch();
            }else {
                uiWatchDog.updateWatchTime();
            }
        }

        LogUtil.d(TAG, "uiState=" + UIState.toString(controlData.getStackTopUIState()));
        LogUtil.d(TAG, "context class name=" + context.getClass().getSimpleName());

        return true;
    }

    public boolean isNeedUIBack(Context context, UIFlowControlData controlData, int[] supportUiBackStateList) {
        if (supportUiBackStateList == null) {
            return false;
        }

        int stackTopUIState = controlData.getStackTopUIState();
        if (controlData.isNeedUIBack()) {
            controlData.setNeedUIBack(false);
            for (int i = 0; i < supportUiBackStateList.length; i++) {
                if (stackTopUIState == supportUiBackStateList[i]) {
                    AndroidUtil.finishActivity(context);
                    controlData.popUIState();
                    LogUtil.d(TAG, "Back to previous UI");
                    if (controlData.getStackTopUIState() == UI_STATE_TRANS_ENTRY) {
                        if (uiWatchDog != null) {
                            uiWatchDog.stopUIWatch();
                        }
                        controlData.pushUIState(UI_STATE_TRANS_END);
                    }
                    return true;
                }
            }
            LogUtil.d(TAG, "Back to Main menu");
            controlData.pushUIState(UI_STATE_TRANS_END);
        }

        if (controlData.isGoBackToMainMenu()) {
            controlData.setGoBackToMainMenu(false);
            controlData.pushUIState(UI_STATE_TRANS_END);
        }

        return false;
    }

    public void jumpToState(Context context, int ui_state, Class uiClass) {
        AndroidUtil.startActivity(context, uiClass);
        uiNavigator.getUiFlowControlData().pushUIState(ui_state);
    }

    public boolean isEmvTransaction(UIFlowControlData uiFlowControlData) {
        boolean isEmvTransaction = false;

        if (!uiFlowControlData.isSwipeCard() && !uiFlowControlData.isManualInput()) {
            isEmvTransaction = true;
        }

        LogUtil.d(TAG, "isEmvTransaction=" + isEmvTransaction);
        return isEmvTransaction;
    }

    public void importCardConfirmResult(boolean isConfirm) {
        useCaseImportCardConfirmResult.asyncExecuteWithoutResult(isConfirm);
    }

    private String getUIStateUIBackHintMsg(UINavigator uiNavigator) {
        int uiState = uiNavigator.getUiFlowControlData().getStackTopUIState();
        String hintMsg = "";

        if (uiState == UI_STATE_TRANS_END) {
            uiNavigator.getUiFlowControlData().popUIState();
            uiState = uiNavigator.getUiFlowControlData().getStackTopUIState();
            uiNavigator.getUiFlowControlData().pushUIState(UI_STATE_TRANS_END);
        }

        LogUtil.d(TAG, "getStackTopUIState uistate=" + uiState);
        switch (uiState) {
            case UI_STATE_TRANS_SUCCESS:
                if (uiNavigator.getUiFlowControlData().getTransType() == TransType.SETTLEMENT) {
                    hintMsg = ResUtil.getString(R.string.tv_hint_confirm_to_cancel);
                } else {
                    hintMsg = ResUtil.getString(R.string.tv_hint_do_not_print_customer_slip);
                }
                break;
            default:
                hintMsg = ResUtil.getString(R.string.tv_hint_confirm_to_cancel);
                break;
        }

        return hintMsg;
    }

    public void doBackToEntry(Context context, UINavigator uiNavigator, Class entryActivityClass, boolean isNeedDialogConfirmUIBack) {
        boolean notNeedDialogConfirmUIBack = uiNavigator.getUiFlowControlData().isNotNeedDialogConfirmUIBack();
        if (isNeedDialogConfirmUIBack && !notNeedDialogConfirmUIBack) {
            confirmCancelDialog = DialogUtil.showAskDialog(context, getUIStateUIBackHintMsg(uiNavigator), new DialogUtil.AskDialogListener() {
                @Override
                public void onClick(boolean isSure) {
                    if (isSure) {
                        doCheckCardRemoveAndUIBack(context, uiNavigator, entryActivityClass);
                    } else {
                        uiNavigator.getUiFlowControlData().popUIState();
                    }
                }
            });
        } else {
            doCheckCardRemoveAndUIBack(context, uiNavigator, entryActivityClass);
        }
    }

    private void doCheckCardRemoveAndUIBack(Context context, UINavigator uiNavigator, Class entryActivityClass) {
        LogUtil.d(TAG, "doCheckCardRemoveAndUIBack");
        if (uiWatchDog != null) {
            uiWatchDog.stopUIWatch();
            uiNavigator.setTransUiFlow(null);
            uiNavigator.getUiFlowControlData().clearUIStack();
            uiWatchDog = null;
        }

        useCaseSetDoingTransactionFlag.asyncExecuteWithoutResult(false);

        Disposable disposable = useCaseWaitingCardRemoved.asyncExecute(false).subscribe(isCardRemoved -> {
            if (!isCardRemoved) {
                if (customDialog == null) {
                    LogUtil.d(TAG, "customDialog is null");
                    customDialog = DialogUtil.showRemoveCardDialog(context, null);
                }

                if (!customDialog.isShowing()) {
                    LogUtil.d(TAG, "customDialog not showing");
                    customDialog.show();
                }
            } else {
                LogUtil.d(TAG, "cardRemoved");
                if (customDialog != null && customDialog.isShowing()) {
                    customDialog.dismiss();
                }

                customDialog = null;
                AndroidUtil.startActivity(context, entryActivityClass);
            }
        }, throwable -> {
            throwable.printStackTrace();
            if (customDialog != null && customDialog.isShowing()) {
                customDialog.dismiss();
            }
            customDialog = null;
            AndroidUtil.startActivity(context, entryActivityClass);
        });
    }

    public void stopUIWatch(){
        if (uiWatchDog != null) {
            uiWatchDog.stopUIWatch();
            uiWatchDog=null;
        }
    }

    private class UIWatchDog {
        private long lastWatchTime;
        private UIFlowControlData uiFlowControlData;
        private Thread timerThread;
        private Class backToEntryActivityClass;
        private volatile boolean isRun;
        private volatile boolean isESign = false;

        public UIWatchDog(UIFlowControlData uiFlowControlData, Class backToEntryActivityClass) {
            this.uiFlowControlData = uiFlowControlData;
            this.backToEntryActivityClass = backToEntryActivityClass;
        }

        public void startUIWatch() {
            lastWatchTime = System.currentTimeMillis();
            stopUIWatch();
            isRun = true;
            timerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean isTransEnd = (uiFlowControlData.getStackTopUIState() == UIState.UI_STATE_TRANS_END);
                    while (isRun && !isTransEnd) {
                        long currentTime = System.currentTimeMillis();
                        long timoutSecond = uiFlowControlData.getSysParamOperatorTimeout();
                        int topUIState = uiFlowControlData.getStackTopUIState();
                        if (topUIState == UIState.UI_STATE_ESIGN) {
                            // 此处隐藏bug就是电子签名超时自动跳到Success界面打印，如果电子签名后面的界面不应该是Success界面此处逻辑就会有问题
                            // 修改此bug方案：这里跟下面一样不设置电子签名状态超时，自己到界面去设置超时,并灵活做想要的动作
                            timoutSecond = 150;
                            isESign = true;
                        }

                        boolean isUIStateNeedCheckTimeout = false;
                        if (topUIState == UIState.UI_STATE_INPUT_PIN
                                || topUIState == UIState.UI_STATE_TRANS_NETWORK_PROCESS
                                || topUIState == UIState.UI_STATE_OPTIONPAYMENT_CODE_SCAN
                                || topUIState == UI_STATE_TRANS_SUCCESS
                                || topUIState == UIState.UI_STATE_CHECK_CARD
                                || topUIState == UIState.UI_STATE_GET_CARD_ACOUNT
                                || topUIState == UIState.UI_STATE_INPUT_AMEX_APPROVE_CODE) {
                            lastWatchTime = System.currentTimeMillis();
                        } else {
                            isUIStateNeedCheckTimeout = true;
                        }

                        if (((currentTime - lastWatchTime) / 1000) % 10 == 0) {
                            LogUtil.d(TAG, "isUIStateNeedCheckTimeout=" + (currentTime - lastWatchTime));
                        }

                        if (currentTime - lastWatchTime > timoutSecond * 1000
                                && isUIStateNeedCheckTimeout) {
                            LogUtil.d(TAG, "currentTime=" + currentTime);
                            LogUtil.d(TAG, "lastWatchTime=" + lastWatchTime);
                            LogUtil.d(TAG, "sysOperatorTimeout=" + uiFlowControlData.getSysParamOperatorTimeout());
                            LogUtil.d(TAG, "uiState timeout.");
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (isESign) {
                                        jumpToState(uiFlowControlData.getCurrentUIContext(), UI_STATE_TRANS_SUCCESS, TransSuccessActivity.class);
                                        lastWatchTime = System.currentTimeMillis();
                                        isESign = false;
                                    } else {
                                        ToastUtil.showToastLong(ResUtil.getString(R.string.toast_hint_ui_timeout));
                                        uiFlowControlData.pushUIState(UI_STATE_TRANS_END);
                                        if (confirmCancelDialog != null && confirmCancelDialog.isShowing()) {
                                            confirmCancelDialog.dismiss();
                                        }
                                        doCheckCardRemoveAndUIBack(AndroidApplication.getInstance().getCurrentActivityContext(), uiNavigator, backToEntryActivityClass);
                                    }
                                }
                            });

                            if (isESign) {
                                lastWatchTime = System.currentTimeMillis();
                            } else {
                                break;
                            }
                        } else {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    LogUtil.d(TAG, "Watch dog thread stoped. isRun=" + isRun);
                }
            });

            timerThread.start();
        }

        public void updateWatchTime() {
            lastWatchTime = System.currentTimeMillis();
        }

        public void stopUIWatch() {
            if (timerThread != null && timerThread.isAlive()) {
                LogUtil.d(TAG, "call stopUIWatch.");
                isRun = false;
                timerThread = null;
            }
        }
    }
}
