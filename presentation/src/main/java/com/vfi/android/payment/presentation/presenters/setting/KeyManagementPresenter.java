package com.vfi.android.payment.presentation.presenters.setting;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.businessbeans.TLEConfig;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.tle.RKIDownloadStatus;
import com.vfi.android.domain.interactor.repository.UseCaseGetAllHostInfo;
import com.vfi.android.domain.interactor.repository.UseCaseGetAllMerchantInfo;
import com.vfi.android.domain.interactor.repository.UseCaseGetAllTleConfigs;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.repository.UseCaseSaveTleConfig;
import com.vfi.android.domain.interactor.transaction.tle.UseCaseInputCardPin;
import com.vfi.android.domain.interactor.transaction.tle.UseCaseStartRKIKeyDownload;
import com.vfi.android.domain.interactor.transaction.tle.UseCaseStopRKIKeyDownload;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.models.SettingItemViewModel;
import com.vfi.android.payment.presentation.presenters.base.BaseSettingItemPresenter;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.KeyManagementUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.vfi.android.payment.presentation.utils.ResUtil.getString;

public class KeyManagementPresenter extends BaseSettingItemPresenter<KeyManagementUI> {
    private final UseCaseSaveTleConfig useCaseSaveTleConfig;
    private final UseCaseStartRKIKeyDownload useCaseStartRKIKeyDownload;
    private final UseCaseStopRKIKeyDownload useCaseStopRKIKeyDownload;
    private final UseCaseGetCurTranData useCaseGetCurTranData;
    private final UseCaseInputCardPin useCaseInputCardPin;
    private List<HostInfo> hostInfoList;
    private List<MerchantInfo> merchantInfoList;
    private List<TLEConfig> tleConfigList;
    private Map<Integer,List<SettingItemViewModel>> listMap;

    private int firstIntentionId;
    private HostInfo selectedHostInfo;
    private MerchantInfo selectedMerchantInfo;

    private static final int ID_ROOT = 0;
    private static final int ID_1_EDIT_APP_ID       = 1;
    private static final int ID_2_SMART_RKI_DOWNLOAD= 2;
    private static final int ID_3_SMART_TLE_DOWNLOAD= 3;
    private static final int ID_4_CHANGE_PIN        = 4;
    private static final int ID_5_MANUAL_KEY_ENTRY  = 5;
    private static final int ID_6_DESTROY_KEYS      = 6;
    private static final int ID_SELECT_HOST         = 7;
    private static final int ID_SELECT_MERCHANT     = 8;
    private static final int ID_1_1_APP_ID          = 101;
    private static final int ID_2_1_RKI_DOWNLOAD    = 201;
    private static final int ID_3_1_TLE_DOWNLOAD    = 301;

    private int tViewtype = SettingItemViewModel.ViewType.LABEL_AND_TEXT;
    private int eViewtype = SettingItemViewModel.ViewType.LABEL_AND_EDIT;

    @Inject
    public KeyManagementPresenter(UseCaseGetAllHostInfo useCaseGetAllHostInfo,
                                  UseCaseGetAllMerchantInfo useCaseGetAllMerchantInfo,
                                  UseCaseGetAllTleConfigs useCaseGetAllTleConfigs,
                                  UseCaseStartRKIKeyDownload useCaseStartRKIKeyDownload,
                                  UseCaseStopRKIKeyDownload useCaseStopRKIKeyDownload,
                                  UseCaseGetCurTranData useCaseGetCurTranData,
                                  UseCaseInputCardPin useCaseInputCardPin,
                                  UseCaseSaveTleConfig useCaseSaveTleConfig){
        hostInfoList = useCaseGetAllHostInfo.execute(null);
        merchantInfoList = useCaseGetAllMerchantInfo.execute(null);
        tleConfigList = useCaseGetAllTleConfigs.execute(null);
        this.useCaseSaveTleConfig = useCaseSaveTleConfig;
        this.useCaseStartRKIKeyDownload = useCaseStartRKIKeyDownload;
        this.useCaseGetCurTranData = useCaseGetCurTranData;
        this.useCaseStopRKIKeyDownload = useCaseStopRKIKeyDownload;
        this.useCaseInputCardPin = useCaseInputCardPin;
        initData();
    }

    @Override
    protected void onFirstUIAttachment() {
        super.onFirstUIAttachment();
        doUICmd_showTitle(ResUtil.getString(R.string.setting_title_key_management));
    }

    private void initData() {
        listMap = new HashMap<>();
        List<SettingItemViewModel> list = new ArrayList<>();
        list.add(new SettingItemViewModel(ID_1_EDIT_APP_ID, tViewtype, getString(R.string.setting_key_manage_edit_app_id), ""));
        list.add(new SettingItemViewModel(ID_2_SMART_RKI_DOWNLOAD, tViewtype, getString(R.string.setting_manage_smart_rki_download), ""));
        list.add(new SettingItemViewModel(ID_3_SMART_TLE_DOWNLOAD, tViewtype, getString(R.string.setting_key_manage_smart_tle_download), ""));
        list.add(new SettingItemViewModel(ID_4_CHANGE_PIN, tViewtype, getString(R.string.setting_key_manage_change_pin), ""));
        list.add(new SettingItemViewModel(ID_5_MANUAL_KEY_ENTRY, tViewtype, getString(R.string.setting_key_manage_manual_key_entry), ""));
        list.add(new SettingItemViewModel(ID_6_DESTROY_KEYS, tViewtype, getString(R.string.setting_key_manage_destroy_keys), ""));
        listMap.put(ID_ROOT,list);
    }

    @Override
    public List<SettingItemViewModel> initSettingItems() {
        return listMap.get(ID_ROOT);
    }

    @Override
    public void onItemSelected(SettingItemViewModel settingItemViewModel) {
        switch (settingItemViewModel.getId()) {
            case ID_1_EDIT_APP_ID:
            case ID_2_SMART_RKI_DOWNLOAD:
            case ID_3_SMART_TLE_DOWNLOAD:
                showEnabledTleHost(settingItemViewModel);
                firstIntentionId = settingItemViewModel.getId();
                break;
            case ID_4_CHANGE_PIN:
                break;
            case ID_5_MANUAL_KEY_ENTRY:
                break;
            case ID_6_DESTROY_KEYS:
                break;
            case ID_SELECT_HOST:
                doSelectedHostProcessing(settingItemViewModel);
                break;
            case ID_SELECT_MERCHANT:
                doSelectedMerchantProcessing(settingItemViewModel);
                break;
            case ID_2_1_RKI_DOWNLOAD:
                break;
            case ID_3_1_TLE_DOWNLOAD:
                break;
        }
    }

    @Override
    public void onItemValueChanged(SettingItemViewModel settingItemViewModel) {
        switch (settingItemViewModel.getId()) {
            case ID_1_1_APP_ID:
                TLEConfig tleConfig = getTleConfig(settingItemViewModel.getArrayIndex());
                LogUtil.d(TAG, "set AppId=[" + settingItemViewModel.getText() + "]");
                tleConfig.setAppId(settingItemViewModel.getText());
                useCaseSaveTleConfig.execute(tleConfig);
                break;
        }
    }

    @Override
    public void processUnSavedItems(List<SettingItemViewModel> unSavedItemList) {

    }

    private void showEnabledTleHost(SettingItemViewModel settingItemViewModel) {
        List<SettingItemViewModel> modelList = new ArrayList<>();
        for (int i = 0; i < hostInfoList.size(); i++) {
            HostInfo hostInfo = hostInfoList.get(i);
            if (hostInfo.getMerchantIndexs() == null) {
                continue;
            }

            String[] merchantIndexs = hostInfo.getMerchantIndexs().split(",");
            if (merchantIndexs.length == 0) {
                continue;
            }

            boolean haveMerchantEnableTle = false;
            for (int j = 0; j < merchantIndexs.length; j++) {
                int merchantIndex = StringUtil.parseInt(merchantIndexs[j], -1);
                if (getMerchantInfo(merchantIndex).isEnableTle()) {
                    haveMerchantEnableTle = true;
                }
            }

            if (haveMerchantEnableTle) {
                modelList.add(new SettingItemViewModel(ID_SELECT_HOST, tViewtype, hostInfo.getHostName(), "", i));
            }
        }

        if (modelList.size() == 0) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_no_data_found));
        } else {
            doUICmd_showSettingItem(modelList);
        }
    }

    private void doSelectedHostProcessing(SettingItemViewModel settingItemViewModel) {
        HostInfo hostInfo = hostInfoList.get(settingItemViewModel.getArrayIndex());
        this.selectedHostInfo = hostInfo;
        String[] merchantIndexs = hostInfo.getMerchantIndexs().split(",");

        List<SettingItemViewModel> itemViewModels = new ArrayList<>();
        int enableTleMerchantCount = 0;
        for (int i = 0; i < merchantIndexs.length; i++) {
            int merchantIndex = StringUtil.parseInt(merchantIndexs[i], -1);
            MerchantInfo merchantInfo = getMerchantInfo(merchantIndex);
            if (merchantInfo.isEnableTle()) {
                LogUtil.d(TAG, "Merchant[" + i + "] Tle enabled.");
                enableTleMerchantCount ++;
                itemViewModels.add(new SettingItemViewModel(ID_SELECT_MERCHANT, tViewtype, merchantInfo.getMerchantName(), "", i));
            }
        }

        LogUtil.d(TAG, "Host[" + hostInfo.getHostName() + "] enalbeTleMerchantCount=" + enableTleMerchantCount);
        if (enableTleMerchantCount > 1) {
            doUICmd_showSettingItem(itemViewModels);
        } else {
            SettingItemViewModel model = itemViewModels.get(0);
            doFirstIntentionProcess(merchantInfoList.get(model.getArrayIndex()));
        }
    }

    private void doSelectedMerchantProcessing(SettingItemViewModel settingItemViewModel) {
        MerchantInfo merchantInfo = merchantInfoList.get(settingItemViewModel.getArrayIndex());
        doFirstIntentionProcess(merchantInfo);
    }

    private void doFirstIntentionProcess(MerchantInfo merchantInfo) {
        this.selectedMerchantInfo = merchantInfo;
        TLEConfig tleConfig = getTleConfig(merchantInfo.getTleIndex());
        if (tleConfig == null) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_no_data_found));
            return;
        }
        switch (firstIntentionId) {
            case ID_1_EDIT_APP_ID:
                List<SettingItemViewModel> itemViewModels = new ArrayList<>();
                itemViewModels.add(new SettingItemViewModel(ID_1_1_APP_ID, eViewtype, "APP ID", "" + tleConfig.getAppId(), tleConfig.getIndex()));
                doUICmd_showSettingItem(itemViewModels);
                break;
            case ID_2_SMART_RKI_DOWNLOAD:
                startDoRKIKeyDownload();
                break;
            case ID_3_SMART_TLE_DOWNLOAD:
                break;
            default:
                break;
        }
    }

    private void startDoRKIKeyDownload() {
        CurrentTranData currentTranData = useCaseGetCurTranData.execute(null);
        currentTranData.setHostInfo(selectedHostInfo);
        currentTranData.setMerchantInfo(selectedMerchantInfo);

        doUICmd_cancelSettingUITimer();
        useCaseStartRKIKeyDownload.asyncExecute(null).doOnNext(status-> {
            LogUtil.d(TAG, "RKIDownloadStatus=[" + RKIDownloadStatus.toDebugString(status) + "]");
            switch (status) {
                case RKIDownloadStatus.REQ_INSERT_CARD:
                    doUICmd_showHintCountDownDialog(getString(R.string.setting_please_insert_smart_card), 30, ()-> {
                        // timeout
                        doUICmd_showToastMessage(getString(R.string.setting_terminal_operation_timeout));
                        useCaseStopRKIKeyDownload.asyncExecuteWithoutResult(null);
                    });
                    break;
                case RKIDownloadStatus.REQ_INPUT_PIN:
                    doUICmd_dismissCurrentDialog();
                    startInputPin();
                    break;
                case RKIDownloadStatus.START_DOWNLOADING:
                    doUICmd_showHintCountDownDialog("Downloading", 60, ()-> {
                        // timeout
                        doUICmd_showToastMessage(getString(R.string.setting_download_failed));
                        useCaseStopRKIKeyDownload.asyncExecuteWithoutResult(null);
                    });
                    break;
                case RKIDownloadStatus.DOWNLOAD_SUCCESS:
                    doUICmd_showToastMessage(getString(R.string.setting_download_success));
                    doUICmd_startSettingUITimer();
                    break;
                case RKIDownloadStatus.DOWNLOAD_FAILED:
                    doUICmd_dismissCurrentDialog();
                    doUICmd_showToastMessage(getString(R.string.setting_download_failed));
                    doUICmd_startSettingUITimer();
                    break;
            }
        }).doOnError(throwable -> {
            throwable.printStackTrace();
            doUICmd_showToastMessage(getString(R.string.setting_download_failed));
            doUICmd_dismissCurrentDialog();
            doUICmd_startSettingUITimer();
        }).subscribe();
    }

    private void startInputPin() {
        doUICmd_showInputPinDialog((pin, isConfirm) -> {
            if (isConfirm) {
                useCaseInputCardPin.asyncExecute(pin).doOnNext(inputTLEPinResult -> {
                    if (!inputTLEPinResult.isSuccess()) {
                        int retryTimes = inputTLEPinResult.getRemainedRetryTimes();
                        if (retryTimes > 0) {
                            if (retryTimes == 1) {
                                doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_last_attempt_enter_pin) + ", " + ResUtil.getString(R.string.toast_hint_retry_times) + "[" + retryTimes + "]");
                            } else {
                                doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_wrong_pin) + ", " + ResUtil.getString(R.string.toast_hint_retry_times) + "[" + retryTimes + "]");
                            }
                            startInputPin();
                        } else {
                            useCaseStopRKIKeyDownload.asyncExecuteWithoutResult(null);
                        }
                    }
                }).doOnError(throwable -> {
                    useCaseStopRKIKeyDownload.asyncExecuteWithoutResult(null);
                }).subscribe();
            } else {
                useCaseStopRKIKeyDownload.asyncExecuteWithoutResult(null);
            }
        });
    }

    private TLEConfig getTleConfig(int tleConfigIndex) {
        for (TLEConfig tleConfig : tleConfigList) {
            if (tleConfig.getIndex() == tleConfigIndex) {
                return tleConfig;
            }
        }

        LogUtil.d(TAG, "getTleConfig tleConfigIndex=" + tleConfigIndex + " not found.");
        return null;
    }

    private MerchantInfo getMerchantInfo(int merchantIndex) {
        for (MerchantInfo merchantInfo : merchantInfoList) {
            if (merchantInfo.getMerchantIndex() == merchantIndex) {
                return merchantInfo;
            }
        }

        LogUtil.d(TAG, "merchantIndex[" + merchantIndex + "] not found.");
        return null;
    }

    private void doUICmd_showInputPinDialog(DialogUtil.InputDialogListener listener) {
        execute(ui -> ui.showInputPinDialog(listener));
    }
}
