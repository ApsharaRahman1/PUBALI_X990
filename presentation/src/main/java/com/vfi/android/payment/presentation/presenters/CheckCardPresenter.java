package com.vfi.android.payment.presentation.presenters;


import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.SwitchParameter;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.consts.CTLSLedStatus;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.CheckCardErrorConst;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.CheckCardResult;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.deviceservice.UseCaseCheckCardStart;
import com.vfi.android.domain.interactor.deviceservice.UseCaseCheckCardStop;
import com.vfi.android.domain.interactor.deviceservice.UseCaseSetCTLSLedStatus;
import com.vfi.android.domain.interactor.deviceservice.UseCaseStopWaitingCardRemoved;
import com.vfi.android.domain.interactor.deviceservice.UseCaseWaitingCardRemoved;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalCfg;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.CheckCardErrorMapper;
import com.vfi.android.payment.presentation.mappers.ExceptionErrMsgMapper;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TvShowUtil;
import com.vfi.android.payment.presentation.view.contracts.CheckCardUI;


import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * Created by yunlongg1 on 03/11/2017.
 */

public class CheckCardPresenter extends BasePresenter<CheckCardUI> {
    private final String TAG = TAGS.CHECK_CARD;
    private final UseCaseGetCurTranData useCaseGetCurTranData;
    private CurrentTranData currentTranData;
    private UseCaseCheckCardStart useCaseCheckCardStart;
    private UseCaseCheckCardStop useCaseCheckCardStop;
    private UseCaseSetCTLSLedStatus useCaseSetCTLSLedStatus;
    private UseCaseWaitingCardRemoved useCaseWaitingCardRemoved;
    private UseCaseStopWaitingCardRemoved useCaseStopWaitingCardRemoved;
    private UINavigator uiNavigator;
    private TerminalCfg terminalCfg;

    @Inject
    CheckCardPresenter(UseCaseGetCurTranData useCaseGetCurTranData,
                       UseCaseCheckCardStart useCaseCheckCardStart,
                       UseCaseSetCTLSLedStatus useCaseSetCTLSLedStatus,
                       UINavigator uiNavigator,
                       UseCaseCheckCardStop useCaseCheckCardStop,
                       UseCaseWaitingCardRemoved useCaseWaitingCardRemoved,
                       UseCaseStopWaitingCardRemoved useCaseStopWaitingCardRemoved,
                       UseCaseGetTerminalCfg useCaseGetTerminalCfg) {
        this.currentTranData = useCaseGetCurTranData.execute(null);
        this.useCaseCheckCardStart = useCaseCheckCardStart;
        this.useCaseCheckCardStop = useCaseCheckCardStop;
        this.useCaseGetCurTranData = useCaseGetCurTranData;
        this.uiNavigator = uiNavigator;
        this.terminalCfg = useCaseGetTerminalCfg.execute(null);
        this.useCaseSetCTLSLedStatus = useCaseSetCTLSLedStatus;
        this.useCaseWaitingCardRemoved = useCaseWaitingCardRemoved;
        this.useCaseStopWaitingCardRemoved = useCaseStopWaitingCardRemoved;
    }

    @Override
    protected void onFirstUIAttachment() {
        SwitchParameter switchParameter = currentTranData.getSwitchParameter();
        doUICmd_enableManualInputBtn(switchParameter.isEnableManual());
        boolean isSupportTap = switchParameter.isEnableTapCard();
        boolean isSupportInsert = switchParameter.isEnableInsertCard();
        boolean isSupportSwipe = switchParameter.isEnableSwipeCard();
        displayCheckCardIcon(isSupportTap, isSupportInsert, isSupportSwipe);
        loadAmount();
        doUICmd_showTitle(currentTranData.getTitle(), "");
    }

    /**
     * If amount is zero, hide amount area.
     */
    private void loadAmount() {
        long amountLong = 0;
        amountLong = StringUtil.parseLong(currentTranData.getTransAmount(), 0);
        LogUtil.d(TAG, "getTransAmount=" + currentTranData.getTransAmount());
        if (amountLong <= 0) {
            doUICmd_hideAmount();
        } else {
            String amount = String.format("%012d", amountLong);
            amount = TvShowUtil.formatAmount(amount);
            doUICmd_showAmount(amount);
        }
    }

    public void doCancelCheckCard() {
        uiNavigator.getUiFlowControlData().setNeedUIBack(true);
        doUICmd_navigatorToNext();
//        doUICmd_showToastMessage(ResUtil.getString(R.string.tv_hint_operation_cancelled));
    }

    public void doManualInputCardNum() {
        useCaseSetCTLSLedStatus.asyncExecuteWithoutResult(CTLSLedStatus.CLEAR_ALL_LEDS);
        uiNavigator.getUiFlowControlData().setManualInput(true);
        currentTranData.getCardInfo().setCardEntryMode(CardEntryMode.MANUAL);
        doUICmd_navigatorToNext();
    }

    public void stopCheckCard() {
        useCaseCheckCardStop.asyncExecuteWithoutResult(null);
    }

    private void showRetryHintInfo(CheckCardResult checkCardResult) {
        if (checkCardResult.isFallBack()) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_fallback));
        } else if (checkCardResult.isSupportALLCardType() || checkCardResult.isOnlySupportMagCard()) {
            String hintMsg;
            if (currentTranData.getCardInfo().getCardEntryMode() == CardEntryMode.MAG) {
                hintMsg = ResUtil.getString(R.string.toast_hint_stripe_reading_error);
            } else {
                hintMsg = ResUtil.getString(R.string.toast_hint_reading_error);
            }

            hintMsg += "\n";
            hintMsg += ResUtil.getString(R.string.toast_hint_try_again);
            doUICmd_showHintCountDownDialog(hintMsg, 2, null);
        } else {
            boolean isNeedAddSplitChar = false;
            String hintMsg = ResUtil.getString(R.string.tv_hint_please);
            if (checkCardResult.isSupportICCard()) {
                hintMsg += " " + ResUtil.getString(R.string.tv_hint_insert);
                isNeedAddSplitChar = true;
            }

            if (checkCardResult.isSupportRFCard()) {
                if (isNeedAddSplitChar) {
                    hintMsg += "/";
                }
                hintMsg += ResUtil.getString(R.string.tv_hint_tap);
                isNeedAddSplitChar = true;
            }

            if (checkCardResult.isSupportMagCard()) {
                if (isNeedAddSplitChar) {
                    hintMsg += "/";
                }
                hintMsg += ResUtil.getString(R.string.tv_hint_swipe);
            }

            hintMsg += " " + ResUtil.getString(R.string.tv_hint_card);

            doUICmd_showHintCountDownDialog(hintMsg, 2, null);
        }
    }


    public void startCheckCard() {
        currentTranData = useCaseGetCurTranData.execute(null);
        if (currentTranData.isEMVNeedFallback()
                && currentTranData.getIcCardfallbackRemainTimes() == 0) {
            displayCheckCardIcon(false, false, true);
        } else if (currentTranData.isEMVNeedTapToInsert() && currentTranData.getRfCardFallbackRemainTimes() == 0) {
            displayCheckCardIcon(false, true, true);
        }

        loadAmount();

        useCaseCheckCardStart.asyncExecute(null).doOnNext(checkCardResult -> {
            LogUtil.d(TAG, "UI isNeedRetry=" + checkCardResult.isNeedRetry());
            if (checkCardResult.isNeedRetry()) {
                doRetryCheckCard(checkCardResult, currentTranData);
                showRetryHintInfo(checkCardResult);
            } else {
                if (currentTranData.getCardInfo().getCardEntryMode() == CardEntryMode.MAG) {
                    uiNavigator.getUiFlowControlData().setSwipeCard(true);

                    doUICmd_showInputLast4AccountDialog();

                } else {
                    uiNavigator.getUiFlowControlData().setSwipeCard(false);
                    doUICmd_navigatorToNext();
                }
            }
        }).doOnError(throwable -> {
            throwable.printStackTrace();

            if (throwable instanceof CommonException) {
                CommonException exception = (CommonException) throwable;
                int exceptionType = exception.getExceptionType();
                int subErrorType = exception.getSubErrType();
                LogUtil.d(TAG, "CommonException type=" + exceptionType + " subErrorType=" + subErrorType);
                switch (exceptionType) {
                    case ExceptionType.CHECK_CARD_TIMEOUT:
                    case ExceptionType.TRACK_KEY_NOT_FOUND:
                        currentTranData.setErrorMsg(ExceptionErrMsgMapper.toErrorMsg(exceptionType));
                        uiNavigator.getUiFlowControlData().setTransFailed(true);
                        doUICmd_navigatorToNext();
                        break;
                    case ExceptionType.FALL_BACK:
                        if (subErrorType == CheckCardErrorConst.CARD_CONFLICT) {
                            showWaitingRemoveCardDialog(ResUtil.getString(R.string.tv_hint_multi_card_present_one));
                        } else {
                            doICPowerUpFailedFallbackProcess();
                        }
                        break;
                    case ExceptionType.CHECK_CARD_FAILED:
                        currentTranData.setErrorMsg(CheckCardErrorMapper.toErrorString(subErrorType));
                        uiNavigator.getUiFlowControlData().setTransFailed(true);
                        doUICmd_navigatorToNext();
                        break;
                    case ExceptionType.GET_CARD_BIN_FAILED:
                        doUICmd_showTransNotAllowDialog();
                        break;
                    default:
                        break;
                }
            } else {
                doUICmd_showToastMessage(throwable.getMessage());
            }
        }).subscribe();
    }

    public void userConfirmFallback() {
        useCaseStopWaitingCardRemoved.asyncExecuteWithoutResult(null);
    }

    public void checkLast4DigitAccount(String last4Account) {
        if (last4Account != null
                && last4Account.length() == 4
                && currentTranData.getCardInfo().getPan().endsWith(last4Account)) {
            uiNavigator.getUiFlowControlData().setNeedSelectHostMerchant(true);
            doUICmd_navigatorToNext();
        } else {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_invalid_entry));
            doUICmd_showInputLast4AccountDialog();
        }
    }

    private void doICPowerUpFailedFallbackProcess() {
        CurrentTranData currentTranData = useCaseGetCurTranData.execute(null);

        int cardEntryMode = currentTranData.getCardInfo().getCardEntryMode();
        if (cardEntryMode == CardEntryMode.IC) {
            currentTranData.setEMVNeedFallback(true);
            if (currentTranData.getIcCardfallbackRemainTimes() <= 0) {
                showWaitingRemoveCardDialog(ResUtil.getString(R.string.tv_hint_error_reading) + "\n" + ResUtil.getString(R.string.tv_hint_swipe_magnetic_card));
            } else {
                showWaitingRemoveCardDialog(ResUtil.getString(R.string.tv_hint_error_reading) + "\n" + ResUtil.getString(R.string.tv_hint_remove_card_try_another_card));
            }
        } else {
            currentTranData.setEMVNeedTapToInsert(true);
            if (currentTranData.getRfCardFallbackRemainTimes() <= 0) {
                showWaitingRemoveCardDialog(ResUtil.getString(R.string.tv_hint_swipe_inset_type_card));
            } else {
                showWaitingRemoveCardDialog(ResUtil.getString(R.string.tv_hint_card_not_support) + "\n" + ResUtil.getString(R.string.tv_hint_try_another_card));
            }
        }
    }

    private void checkAndSyncWaitingCardRemove() {
        boolean needDelay = false;
        if (currentTranData.getCardInfo().getCardEntryMode() == CardEntryMode.RF) {
            needDelay = true;
        }
        Disposable disposable = useCaseWaitingCardRemoved.asyncExecute(needDelay).subscribe(isCardRemoved -> {
            if (isCardRemoved) {
                doUICmd_dismissFallbackRemoveCardDialog();
                startCheckCard();
            }
        }, throwable -> {
            doUICmd_dismissFallbackRemoveCardDialog();
            startCheckCard();
        });
    }

    private void showWaitingRemoveCardDialog(String hintStr) {
        checkAndSyncWaitingCardRemove();
        doUICmd_showFallbackRemoveCardDialog(hintStr);
    }

    private void displayCheckCardIcon(boolean isSupportTap, boolean isSupportInsert, boolean isSupportSwipe) {
        doUICmd_displayCheckCardIcon(isSupportTap, isSupportInsert, isSupportSwipe);
    }

    private void doRetryCheckCard(CheckCardResult checkCardResult, CurrentTranData currentTranDataIn) {
        displayCheckCardIcon(checkCardResult.isSupportRFCard(),
                checkCardResult.isSupportICCard(), checkCardResult.isSupportMagCard());
    }

    private void doUICmd_showAmount(String amount) {
        execute(ui -> ui.showAmount(amount));
    }

    private void doUICmd_displayCheckCardIcon(boolean isSupportTap, boolean isSupportInsert, boolean isSupportSwipe) {
        execute(ui -> ui.displayCheckCardIcon(isSupportTap, isSupportInsert, isSupportSwipe));
    }

    private void doUICmd_showTransNotAllowDialog() {
        execute(ui -> ui.showTransNotAllowDialog());
    }

    private void doUICmd_showCardNotSupportDialog() {
        execute(ui -> ui.showCardNotSupportDialog());
    }

    private void doUICmd_showFallbackRemoveCardDialog(String msg) {
        execute(ui -> ui.showFallbackRemoveCardDialog(msg));
    }

    private void doUICmd_enableManualInputBtn(boolean isEnable) {
        execute(ui -> ui.enableManualInputBtn(isEnable));
    }

    private void doUICmd_dismissFallbackRemoveCardDialog() {
        execute(ui -> ui.dismissFallbackRemoveCardDialog());
    }

    private void doUICmd_hideAmount() {
        execute(ui -> ui.hideAmount());
    }

    private void doUICmd_showInputLast4AccountDialog() {
        execute(ui -> ui.showInputLast4CardNumDialog());
    }


}
