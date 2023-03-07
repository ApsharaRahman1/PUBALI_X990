package com.vfi.android.payment.presentation.presenters;


import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.databeans.PinKeyCoordinate;
import com.vfi.android.domain.entities.databeans.PinPadInitPinInputCustomViewParamIn;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.deviceservice.UseCaseInitPinInputCustomView;
import com.vfi.android.domain.interactor.deviceservice.UseCaseSetPowerKeyStatus;
import com.vfi.android.domain.interactor.deviceservice.UseCaseStartPinInputCustomView;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.ExceptionErrMsgMapper;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TvShowUtil;
import com.vfi.android.payment.presentation.view.contracts.InputPinUI;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;


/**
 * Created by yunlongg1 on 03/11/2017.
 */

public class InputPinPresenter extends BasePresenter<InputPinUI> {
    private final String TAG = this.getClass().getSimpleName();

    private final UseCaseInitPinInputCustomView useCaseInitPinInputCustomView;
    private final UseCaseStartPinInputCustomView useCaseStartPinInputCustomView;
    private final UseCaseSetPowerKeyStatus useCaseSetPowerKeyStatus;
    private final CurrentTranData currentTranData;
    private final UINavigator uiNavigator;

    @Inject
    InputPinPresenter(
            UINavigator uiNavigator,
            UseCaseInitPinInputCustomView useCaseInitPinInputCustomView,
            UseCaseStartPinInputCustomView useCaseStartPinInputCustomView,
            UseCaseSetPowerKeyStatus useCaseSetPowerKeyStatus,
            UseCaseGetCurTranData useCaseGetCurTranData) {
        this.useCaseInitPinInputCustomView = useCaseInitPinInputCustomView;
        this.useCaseStartPinInputCustomView = useCaseStartPinInputCustomView;
        this.useCaseSetPowerKeyStatus = useCaseSetPowerKeyStatus;
        this.currentTranData = useCaseGetCurTranData.execute(null);
        this.uiNavigator = uiNavigator;
    }

    @Override
    protected void onFirstUIAttachment() {
        String title;
        String subTitle =  "";
        doUICmd_showTitle(currentTranData.getTitle(), subTitle);
        if (currentTranData.isEmvRequireOnlinePin()) {
            doUICmd_showPinHint(ResUtil.getString(R.string.tv_hint_enter_online_pin));
        } else {
            doUICmd_showPinHint(ResUtil.getString(R.string.tv_hint_enter_offline_pin));
        }
        loadCardInfo();
    }

    private void loadCardInfo() {
        String acount = currentTranData.getCardInfo().getPan();
        acount = TvShowUtil.formatAcount(acount);

        String amount = StringUtil.formatAmount(currentTranData.getTotalAmount());

        doUICmd_showTransInfo(acount, amount);
    }

    public int[] initkeyboardAndPinpad(List<PinKeyCoordinate> pinKeyCoordinates) {
        PinPadInitPinInputCustomViewParamIn paramIn = new PinPadInitPinInputCustomViewParamIn();
        if (currentTranData.isEmvRequireOnlinePin()) {
            paramIn.setMaxPinLen(6);
        } else {
            paramIn.setMaxPinLen(12);
        }
        paramIn.setPinKeyCoordinates(pinKeyCoordinates);
        paramIn.setPinpadListener(new PinPadInitPinInputCustomViewParamIn.PinpadListener() {
            @Override
            public void onInput(int len, int key) {
                doUICmd_showPasswd(len);
            }

            @Override
            public void onConfirm(byte[] data, boolean isNonePin) {
                LogUtil.d(TAG, "Set inputPinFinished=true");
                LogUtil.d(TAG,"Is By pass : " + isNonePin);
                uiNavigator.getUiFlowControlData().setInputPinFinished(true);
                doUICmd_navigatorToNext();
            }

            @Override
            public void onCancel() {
                LogUtil.d("TAG", "onCancel, Set transFailed=true");
                currentTranData.setErrorMsg(ResUtil.getString(R.string.tv_hint_input_pin_cancelled));
                uiNavigator.getUiFlowControlData().setTransFailed(true);
                doUICmd_navigatorToNext();
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                if (errorCode == -2) {
                    currentTranData.setErrorMsg(ResUtil.getString(R.string.tv_hint_input_pin_timeout));
                } else {
                    currentTranData.setErrorMsg(ResUtil.getString(R.string.tv_hint_input_pin_exception));
                }
                uiNavigator.getUiFlowControlData().setTransFailed(true);
                doUICmd_navigatorToNext();
            }
        });

        try {
            Map<String, String> map = useCaseInitPinInputCustomView.execute(paramIn);
            LogUtil.d("Map.size=" + map.size());
            LogUtil.d("btn_0=" + map.get("btn_0"));
            LogUtil.d("btn_1=" + map.get("btn_1"));
            LogUtil.d("btn_2=" + map.get("btn_2"));
            LogUtil.d("btn_3=" + map.get("btn_3"));
            LogUtil.d("btn_4=" + map.get("btn_4"));
            LogUtil.d("btn_5=" + map.get("btn_5"));
            LogUtil.d("btn_6=" + map.get("btn_6"));
            LogUtil.d("btn_7=" + map.get("btn_7"));
            LogUtil.d("btn_8=" + map.get("btn_8"));
            LogUtil.d("btn_9=" + map.get("btn_9"));
            LogUtil.d("btn_10=" + map.get("btn_10"));
            LogUtil.d("btn_11=" + map.get("btn_11"));
            LogUtil.d("btn_12=" + map.get("btn_12"));
            int btn_0 = Integer.parseInt(map.get("btn_0"));
            int btn_1 = Integer.parseInt(map.get("btn_1"));
            int btn_2 = Integer.parseInt(map.get("btn_2"));
            int btn_3 = Integer.parseInt(map.get("btn_3"));
            int btn_4 = Integer.parseInt(map.get("btn_4"));
            int btn_5 = Integer.parseInt(map.get("btn_5"));
            int btn_6 = Integer.parseInt(map.get("btn_6"));
            int btn_7 = Integer.parseInt(map.get("btn_7"));
            int btn_8 = Integer.parseInt(map.get("btn_8"));
            int btn_9 = Integer.parseInt(map.get("btn_9"));

            int[] randomNumbers = new int[]{btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9};
            return randomNumbers;
        } catch (Exception e) {
            Throwable throwable = e.getCause();
            LogUtil.d("Start pin input exception happened.");
            if (throwable != null && throwable instanceof CommonException) {
                CommonException commonException = (CommonException) throwable;
                String errMsg = ExceptionErrMsgMapper.toErrorMsg(commonException.getExceptionType());
                LogUtil.d("Start pin input exception happened. error:" + errMsg);
                currentTranData.setErrorMsg(errMsg);
            } else {
                e.printStackTrace();
                currentTranData.setErrorMsg(ResUtil.getString(R.string.tv_hint_input_pin_exception));
                doUICmd_showToastMessage("Start pin input error:" + e.getMessage());
            }
            uiNavigator.getUiFlowControlData().setTransFailed(true);
            doUICmd_navigatorToNext();

            // Exception happened. example: request online pin but online pin key not found.
            return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        }
    }

    public void startPinInput() {
        useCaseStartPinInputCustomView.execute(null);
        LogUtil.d("TAG", "After === startPinInput");
    }

    public void setPowerKeyStatus(boolean isPowerKeyEnable) {
        useCaseSetPowerKeyStatus.execute(isPowerKeyEnable);
    }

    private void doUICmd_showPasswd(int len) {
        execute(ui -> ui.showPassword(len));
    }

    private void doUICmd_showTransInfo(String acount, String amount) {
        execute(ui -> ui.showTransInfo(acount, amount));
    }

    private void doUICmd_showPinHint(String pinHint) {
        execute(ui -> ui.showPinHint(pinHint));
    }
}
