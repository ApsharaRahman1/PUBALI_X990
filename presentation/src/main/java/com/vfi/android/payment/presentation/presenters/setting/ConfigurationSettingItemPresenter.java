package com.vfi.android.payment.presentation.presenters.setting;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.businessbeans.InstallmentPromo;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.businessbeans.PrintConfig;
import com.vfi.android.domain.entities.businessbeans.SwitchParameter;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.consts.CVV2ControlType;
import com.vfi.android.domain.entities.consts.PrintType;
import com.vfi.android.domain.entities.databeans.OperatorInfo;
import com.vfi.android.domain.entities.databeans.PrintInfo;
import com.vfi.android.domain.interactor.print.UseCaseStartPrintSlip;
import com.vfi.android.domain.interactor.repository.UseCaseGetAllCardInfo;
import com.vfi.android.domain.interactor.repository.UseCaseGetAllInstallmentPromos;
import com.vfi.android.domain.interactor.repository.UseCaseGetAllHostInfo;
import com.vfi.android.domain.interactor.repository.UseCaseGetAllMerchantInfo;
import com.vfi.android.domain.interactor.repository.UseCaseGetAllPrintConfig;
import com.vfi.android.domain.interactor.repository.UseCaseGetAllTransSwitch;
import com.vfi.android.domain.interactor.repository.UseCaseGetClearAllBatches;
import com.vfi.android.domain.interactor.repository.UseCaseGetClearAllReversals;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalCfg;
import com.vfi.android.domain.interactor.repository.UseCaseIsAllHostsEmptyBatch;
import com.vfi.android.domain.interactor.repository.UseCaseIsBatchEmptyByMerchantIndex;
import com.vfi.android.domain.interactor.repository.UseCaseSaveCardBinInfo;
import com.vfi.android.domain.interactor.repository.UseCaseSaveHostInfo;
import com.vfi.android.domain.interactor.repository.UseCaseSaveInstallmentPromo;
import com.vfi.android.domain.interactor.repository.UseCaseSaveMerchantInfo;
import com.vfi.android.domain.interactor.repository.UseCaseSavePrintConfig;
import com.vfi.android.domain.interactor.repository.UseCaseSaveTerminalCfg;
import com.vfi.android.domain.interactor.repository.UseCaseSaveTransactionSwitch;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.domain.utils.logOutPut.LogOutPutUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.MenuTitleMapper;
import com.vfi.android.payment.presentation.models.ParamItemModel;
import com.vfi.android.payment.presentation.presenters.base.BaseSettingItemPresenter;
import com.vfi.android.payment.presentation.receivers.AutoSettlementReceiver;
import com.vfi.android.payment.presentation.receivers.ForceSettlementReceiver;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.ConfigurationUI;
import com.vfi.android.payment.presentation.models.SettingItemViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_AMOUNT;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_BOOLEAN;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_CONST;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_CVV2_MAX;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_CVV2_MIN;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_INDEXS_LIST;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_NUMBER;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_NUMBER_MAX_1;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_NUMBER_MAX_2;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_NUMBER_MAX_5;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_NUMBER_MAX_6;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_NUM_L_PADDING_FIX_6;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_NUMBER_PASSWD_MAX_6;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_PAN_10;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_STRING;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_STR_L_PADDING_FIX_15;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_STR_L_PADDING_FIX_8;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_TIME_HH_MM;
import static com.vfi.android.payment.presentation.utils.ResUtil.*;

public class ConfigurationSettingItemPresenter extends BaseSettingItemPresenter<ConfigurationUI> {

    private final UseCaseSaveMerchantInfo useCaseSaveMerchantInfo;
    private final UseCaseSaveTerminalCfg useCaseSaveTerminalCfg;
    private final UseCaseSaveCardBinInfo useCaseSaveCardBinInfo;
    private final UseCaseSaveTransactionSwitch useCaseSaveTransactionSwitch;
    private final UseCaseGetAllHostInfo useCaseGetAllHostInfo;
    private final UseCaseSaveHostInfo useCaseSaveHostInfo;
    private final UseCaseGetClearAllBatches useCaseGetClearAllBatches;
    private final UseCaseGetClearAllReversals useCaseGetClearAllReversals;
    private final UseCaseStartPrintSlip useCaseStartPrintSlip;
    private final UseCaseIsAllHostsEmptyBatch useCaseIsAllHostsEmptyBatch;
    private final UseCaseIsBatchEmptyByMerchantIndex useCaseIsBatchEmptyByMerchantIndex;
    private final UseCaseSaveInstallmentPromo useCaseSaveInstallmentPromo;
    private final UseCaseSavePrintConfig useCaseSavePrintConfig;
    private final UseCaseGetAllMerchantInfo useCaseGetAllMerchantInfo;

    private static final int ID_ROOT = 0;

    private static final int ID_1_CARDBININFO       = 1;
    private static final int ID_2_TERMINALCFG       = 2;
    private static final int ID_3_MERCHANTCFG       = 3;
    private static final int ID_4_TRANS_SWITCH     =  4;
    private static final int ID_5_TRAINING_MODE     = 5;
    private static final int ID_6_ENALBE_TLE        = 6;
    private static final int ID_7_CLEAR_BATCH       = 7;
    private static final int ID_8_CLEAR_REVERSAL    = 8;
    private static final int ID_9_PRINT_EMV_DEBUG   = 9;
    private static final int ID_10_SYSTEM_LANGUAGE  = 10;
    private static final int ID_11_SYSTEM_SLEEP_TIME= 11;
    private static final int ID_12_EXPORT_LOG       = 12;
    private static final int ID_13_INSTALLMENT_PROMO= 13;
    private static final int ID_14_PRINT_CONFIG     = 14;
    private static final int ID_14_PRINT_ISO_LOG    = 15;

    private static final int ID_1_1_CARDBININFO = 101;
    private static final int ID_2_1_TERMINALCFG = 201;
    private static final int ID_3_1_MERCHANTCFG = 301;
    private static final int ID_4_1_TRANS_TYPE = 401;
    private static final int ID_13_1_INSTALLMENT_PROMO = 1301;
    private static final int ID_14_1_PRINT_CONFIG     = 1401;

    private static final int SAVE_CARDBININFO = 10101;
    private static final int SAVE_TERMINALCFG = 20101;
    private static final int SAVE_MERCHANTCFG = 30101;
    private static final int SAVE_TRANS_SWITCH = 40101;
    private static final int SAVE_INSTALLMENT_PROMO = 130101;
    private static final int SAVE_PRINT_CONFIG = 140101;

    private int tViewtype = SettingItemViewModel.ViewType.LABEL_AND_TEXT;
    private int eViewtype = SettingItemViewModel.ViewType.LABEL_AND_EDIT;
    private int sViewtype = SettingItemViewModel.ViewType.LABEL_AND_SWITCH;
    private int nViewtype = SettingItemViewModel.ViewType.LABEL_AND_NUM;
    private int nPasswdViewType = SettingItemViewModel.ViewType.LABEL_AND_NUM_PASSWD;


    private List<CardBinInfo> cardBinInfoList = new ArrayList<>();
    private List<MerchantInfo> merchantInfoList = new ArrayList<>();
    private List<SwitchParameter> transSwitchList;
    private List<InstallmentPromo> installmentPromoList;
    private List<PrintConfig> printConfigList;
    private TerminalCfg terminalCfg;
    private Map<Integer,List<SettingItemViewModel>> listMap;

    @Inject
    public ConfigurationSettingItemPresenter(UseCaseGetAllCardInfo useCaseGetAllCardInfo,
                                             UseCaseGetAllMerchantInfo useCaseGetAllMerchantInfo,
                                             UseCaseGetTerminalCfg useCaseGetTerminalCfg,
                                             UseCaseSaveTerminalCfg useCaseSaveTerminalCfg,
                                             UseCaseSaveMerchantInfo useCaseSaveMerchantInfo,
                                             UseCaseGetAllTransSwitch useCaseGetAllTransSwitch,
                                             UseCaseSaveTransactionSwitch useCaseSaveTransactionSwitch,
                                             UseCaseGetAllHostInfo useCaseGetAllHostInfo,
                                             UseCaseSaveHostInfo useCaseSaveHostInfo,
                                             UseCaseGetClearAllBatches useCaseGetClearAllBatches,
                                             UseCaseGetClearAllReversals useCaseGetClearAllReversals,
                                             UseCaseStartPrintSlip useCaseStartPrintSlip,
                                             UseCaseIsAllHostsEmptyBatch useCaseIsAllHostsEmptyBatch,
                                             UseCaseIsBatchEmptyByMerchantIndex useCaseIsBatchEmptyByMerchantIndex,
                                             UseCaseGetAllInstallmentPromos useCaseGetAllInstallmentPromos,
                                             UseCaseSaveInstallmentPromo useCaseSaveInstallmentPromo,
                                             UseCaseGetAllPrintConfig useCaseGetAllPrintConfig,
                                             UseCaseSavePrintConfig useCaseSavePrintConfig,
                                             UseCaseSaveCardBinInfo useCaseSaveCardBinInfo){
        this.useCaseSaveMerchantInfo = useCaseSaveMerchantInfo;
        this.useCaseSaveTerminalCfg = useCaseSaveTerminalCfg;
        this.useCaseSaveCardBinInfo = useCaseSaveCardBinInfo;
        this.useCaseSaveTransactionSwitch = useCaseSaveTransactionSwitch;
        this.useCaseGetAllHostInfo = useCaseGetAllHostInfo;
        this.useCaseSaveHostInfo = useCaseSaveHostInfo;
        this.useCaseGetClearAllBatches = useCaseGetClearAllBatches;
        this.useCaseGetClearAllReversals = useCaseGetClearAllReversals;
        this.useCaseStartPrintSlip = useCaseStartPrintSlip;
        this.useCaseIsAllHostsEmptyBatch = useCaseIsAllHostsEmptyBatch;
        this.useCaseIsBatchEmptyByMerchantIndex = useCaseIsBatchEmptyByMerchantIndex;
        this.useCaseSaveInstallmentPromo = useCaseSaveInstallmentPromo;
        this.useCaseSavePrintConfig = useCaseSavePrintConfig;
        this.useCaseGetAllMerchantInfo = useCaseGetAllMerchantInfo;
        cardBinInfoList = useCaseGetAllCardInfo.execute(null);
        merchantInfoList = useCaseGetAllMerchantInfo.execute(null);
        terminalCfg = useCaseGetTerminalCfg.execute(null);
        transSwitchList = useCaseGetAllTransSwitch.execute(null);
        installmentPromoList = useCaseGetAllInstallmentPromos.execute(false);
        printConfigList = useCaseGetAllPrintConfig.execute(null);
        initData();
    }

    @Override
    protected void onFirstUIAttachment() {
        super.onFirstUIAttachment();
        doUICmd_showTitle(ResUtil.getString(R.string.setting_configuration));
    }

    private void initData() {
        listMap = new HashMap<>();
        List<SettingItemViewModel> list = new ArrayList<>();
        list.add(new SettingItemViewModel(ID_1_CARDBININFO,tViewtype, getString(R.string.setting_carbin_info_lable),""));
        list.add(new SettingItemViewModel(ID_2_TERMINALCFG,tViewtype, getString(R.string.setting_terminal_cfg_lable),""));
        list.add(new SettingItemViewModel(ID_3_MERCHANTCFG,tViewtype, getString(R.string.setting_merchant_cfg_lable),""));
        list.add(new SettingItemViewModel(ID_4_TRANS_SWITCH,tViewtype, getString(R.string.setting_config_trans_switch),""));
        list.add(new SettingItemViewModel(ID_13_INSTALLMENT_PROMO, tViewtype, getString(R.string.setting_config_installment_promo), ""));
        list.add(new SettingItemViewModel(ID_14_PRINT_CONFIG, tViewtype, getString(R.string.setting_config_print_config), ""));
        list.add(new SettingItemViewModel(ID_6_ENALBE_TLE,sViewtype, getString(R.string.setting_enable_tle_on_all_hosts),""));
        list.add(new SettingItemViewModel(ID_7_CLEAR_BATCH,tViewtype, getString(R.string.setting_clear_batch),""));
        list.add(new SettingItemViewModel(ID_8_CLEAR_REVERSAL,tViewtype, getString(R.string.setting_clear_reversal),""));
        list.add(new SettingItemViewModel(ID_9_PRINT_EMV_DEBUG,tViewtype, getString(R.string.setting_print_emv_debug_info),""));
        list.add(new SettingItemViewModel(ID_10_SYSTEM_LANGUAGE,tViewtype, getString(R.string.setting_system_language),""));
        list.add(new SettingItemViewModel(ID_11_SYSTEM_SLEEP_TIME,tViewtype, getString(R.string.setting_system_sleep_time),""));
        list.add(new SettingItemViewModel(ID_12_EXPORT_LOG,tViewtype, getString(R.string.setting_config_export_log),""));
        list.add(new SettingItemViewModel(ID_14_PRINT_ISO_LOG, tViewtype, getString(R.string.setting_config_print_iso_log), ""));
        listMap.put(ID_ROOT,list);

        list = new ArrayList<>();
        for(int i = 0;i<cardBinInfoList.size();i++){
            list.add(new SettingItemViewModel(ID_1_1_CARDBININFO,tViewtype,cardBinInfoList.get(i).getCardName(), true,i));
        }
        listMap.put(ID_1_CARDBININFO,list);

        list = new ArrayList<>();
        for(int i = 0;i<merchantInfoList.size();i++){
            list.add(new SettingItemViewModel(ID_3_1_MERCHANTCFG,tViewtype,merchantInfoList.get(i).getMerchantName(), true,i));
        }
        listMap.put(ID_3_MERCHANTCFG,list);

        list = new ArrayList<>();
        for (int i = 0; i < transSwitchList.size(); i++) {
            list.add(new SettingItemViewModel(ID_4_1_TRANS_TYPE, tViewtype, MenuTitleMapper.toString(transSwitchList.get(i).getTransType()), true, i));
        }
        listMap.put(ID_4_TRANS_SWITCH, list);

        list = new ArrayList<>();
        for (int i = 0; i < installmentPromoList.size(); i++) {
            list.add(new SettingItemViewModel(ID_13_1_INSTALLMENT_PROMO, tViewtype, installmentPromoList.get(i).getPromoLabel(), true, i));
        }
        listMap.put(ID_13_INSTALLMENT_PROMO, list);

        list = new ArrayList<>();
        for (int i = 0; i < printConfigList.size(); i++) {
            list.add(new SettingItemViewModel(ID_14_1_PRINT_CONFIG, tViewtype, "" + printConfigList.get(i).getIndex(), true, i));
        }
        listMap.put(ID_14_PRINT_CONFIG, list);
    }

    @Override
    public List<SettingItemViewModel> initSettingItems() {
        return listMap.get(ID_ROOT);
    }

    @Override
    public void onItemSelected(SettingItemViewModel settingItemViewModel) {
        LogUtil.d(TAG, "Id=[" + settingItemViewModel.getId() + "]");
        switch (settingItemViewModel.getId()){
            case ID_1_CARDBININFO:
                doUICmd_showSettingItem(listMap.get(ID_1_CARDBININFO));
                break;
            case ID_1_1_CARDBININFO:
                displayCardBinInfo(settingItemViewModel.getArrayIndex());
                break;
            case ID_2_TERMINALCFG:
                displayTerminalCfg();
                break;
            case ID_3_MERCHANTCFG:
                doUICmd_showSettingItem(listMap.get(ID_3_MERCHANTCFG));
                break;
            case ID_3_1_MERCHANTCFG:
                displayMerchantCfg(settingItemViewModel.getArrayIndex());
                break;
            case ID_4_TRANS_SWITCH:
                doUICmd_showSettingItem(listMap.get(ID_4_TRANS_SWITCH));
                break;
            case ID_4_1_TRANS_TYPE:
                displayTransSwitch(settingItemViewModel.getArrayIndex());
                break;
            case ID_6_ENALBE_TLE:
                setAllHostTleStatus(settingItemViewModel.isChecked());
                break;
            case ID_7_CLEAR_BATCH:
                doClearAllBatches();
                break;
            case ID_8_CLEAR_REVERSAL:
                doClearAllReversals();
                break;
            case ID_9_PRINT_EMV_DEBUG:
                doPrintEmvDebugInfo();
                break;
            case ID_10_SYSTEM_LANGUAGE:
                doUICmd_showSystemLanguageInterface();
                break;
            case ID_11_SYSTEM_SLEEP_TIME:
                doUICmd_showSystemTimeInterface();
                break;
            case ID_12_EXPORT_LOG:
                doExportLogProcess();
                break;
            case ID_13_INSTALLMENT_PROMO:
                doUICmd_showSettingItem(listMap.get(ID_13_INSTALLMENT_PROMO));
                break;
            case ID_13_1_INSTALLMENT_PROMO:
                displayInstallmentPromo(settingItemViewModel.getArrayIndex());
                break;
            case ID_14_PRINT_CONFIG:
                doUICmd_showSettingItem(listMap.get(ID_14_PRINT_CONFIG));
                break;
            case ID_14_1_PRINT_CONFIG:
                displayPrintConfigs(settingItemViewModel.getArrayIndex());
                break;
            case ID_14_PRINT_ISO_LOG:
                printISOLog();
                break;
        }
    }

    @Override
    public void onItemValueChanged(SettingItemViewModel settingItemViewModel) {
        LogUtil.d(TAG, "===onItemValueChanged Id=[" + settingItemViewModel.getId() + "]===");
        switch (settingItemViewModel.getId()){
            case SAVE_CARDBININFO:
                saveChangedCardBinInfoItem(settingItemViewModel);
                break;
            case SAVE_TERMINALCFG:
                saveChangedTerminalCfgItem(settingItemViewModel);
                break;
            case SAVE_MERCHANTCFG:
                saveChangedMerchantCfgItem(settingItemViewModel);
                break;
            case SAVE_TRANS_SWITCH:
                saveChangedTransactionSwitchItem(settingItemViewModel);
                break;
            case SAVE_INSTALLMENT_PROMO:
                saveChangedInstallmentPromo(settingItemViewModel);
                break;
            case SAVE_PRINT_CONFIG:
                saveChangedPrintConfig(settingItemViewModel);
                break;
        }
    }

    @Override
    public void processUnSavedItems(List<SettingItemViewModel> unSavedItemList) {

    }

    private void setAllHostTleStatus(boolean enable) {
        useCaseGetAllMerchantInfo.asyncExecute(null).doOnNext(merchantInfos -> {
            for (MerchantInfo merchantInfo: merchantInfos) {
                merchantInfo.setEnableTle(true);
                useCaseSaveMerchantInfo.execute(merchantInfo);
            }

        }).subscribe();
    }

    private void doClearAllBatches() {
        doUICmd_showAskDialog(getString(R.string.setting_hint_confirm_to_clear_all_batches), isSure -> {
            if (isSure) {
                doUICmd_showCheckPasswordDialog(OperatorInfo.TYPE_SUPER_MANAGER, isCorrectPassword -> {
                    if (isCorrectPassword) {
                        useCaseGetClearAllBatches.asyncExecute(null).doOnComplete(() -> {
                            doUICmd_showToastMessage(getString(R.string.setting_hint_all_batches_cleared));
                        }).subscribe();
                    }
                });
            }
        });
    }

    private void doClearAllReversals() {
        doUICmd_showAskDialog(getString(R.string.setting_hint_confirm_to_clear_all_reversals), isSure -> {
            if (isSure) {
                doUICmd_showCheckPasswordDialog(OperatorInfo.TYPE_SUPER_MANAGER, isCorrectPassword -> {
                    if (isCorrectPassword) {
                        useCaseGetClearAllReversals.asyncExecute(null).doOnComplete(() -> {
                            doUICmd_showToastMessage(getString(R.string.setting_hing_all_reversals_cleared));
                        }).subscribe();
                    }
                });
            }
        });
    }

    private void doPrintEmvDebugInfo() {
        doUICmd_setLoadingDialogStatus(true);
        PrintInfo printInfo = new PrintInfo(PrintType.EMV_DEBUG_INFO, 1);
        printInfo.setPrintLogoData(ResUtil.getByteFromDrawable(R.drawable.print_logo, 384f));
        useCaseStartPrintSlip.asyncExecute(printInfo).doOnNext(unused -> {
            doUICmd_setLoadingDialogStatus(false);
        }).doOnError(throwable -> {
            throwable.printStackTrace();
            doUICmd_setLoadingDialogStatus(false);
        }).subscribe();
    }

    private boolean isAllHostEmptyBatch() {
        return useCaseIsAllHostsEmptyBatch.execute(null);
    }

    private boolean isEmptyBatchByMerchantIndex(int merchantIndex) {
        return useCaseIsBatchEmptyByMerchantIndex.execute(merchantIndex);
    }

    private void displayMerchantCfg(int index) {
        MerchantInfo merchantInfo = merchantInfoList.get(index);
        List<SettingItemViewModel> list = new ArrayList<>();
        boolean isEmptyBatch = isEmptyBatchByMerchantIndex(merchantInfo.getMerchantIndex());
        list.add(create(new ParamItemModel(SAVE_MERCHANTCFG, FMT_CONST, getString(R.string.setting_merchant_merchant_index), merchantInfo.getMerchantIndex(), index )));
        list.add(create(new ParamItemModel(SAVE_MERCHANTCFG, FMT_CONST,getString(R.string.setting_merchant_merchant_name), merchantInfo.getMerchantName() ,index)));
        list.add(create(new ParamItemModel(SAVE_MERCHANTCFG, FMT_STR_L_PADDING_FIX_8,getString(R.string.setting_merchant_terminal_id),merchantInfo.getTerminalId(),index).setNeedCheckBatchEmpty(true, isEmptyBatch)));
        list.add(create(new ParamItemModel(SAVE_MERCHANTCFG, FMT_STR_L_PADDING_FIX_15,getString(R.string.setting_merchant_merchant_id),merchantInfo.getMerchantId(),index).setNeedCheckBatchEmpty(true, isEmptyBatch)));
        list.add(create(new ParamItemModel(SAVE_MERCHANTCFG, FMT_NUM_L_PADDING_FIX_6,getString(R.string.setting_merchant_trace_num),merchantInfo.getTraceNum(),index).setNeedCheckBatchEmpty(true, isEmptyBatch)));
        list.add(create(new ParamItemModel(SAVE_MERCHANTCFG, FMT_NUM_L_PADDING_FIX_6,getString(R.string.setting_merchant_batch_num),merchantInfo.getBatchNum(),index).setNeedCheckBatchEmpty(true, isEmptyBatch)));
        list.add(create(new ParamItemModel(SAVE_MERCHANTCFG, FMT_NUMBER,getString(R.string.setting_merchant_currency_index),merchantInfo.getCurrencyIndex(),index)));
        list.add(create(new ParamItemModel(SAVE_MERCHANTCFG, FMT_BOOLEAN,getString(R.string.setting_merchant_enable_multi_currency),merchantInfo.isEnableMultiCurrency(),index)));
        list.add(create(new ParamItemModel(SAVE_MERCHANTCFG, FMT_NUMBER,getString(R.string.setting_merchant_print_param_index),merchantInfo.getPrintParamIndex(),index)));
        list.add(create(new ParamItemModel(SAVE_MERCHANTCFG, FMT_NUMBER_MAX_2,getString(R.string.setting_merchant_amount_digits),merchantInfo.getAmountDigits(),index)));
        list.add(create(new ParamItemModel(SAVE_MERCHANTCFG, FMT_AMOUNT,getString(R.string.setting_merchant_min_amount),merchantInfo.getMinAmount(),index)));
        list.add(create(new ParamItemModel(SAVE_MERCHANTCFG, FMT_AMOUNT,getString(R.string.setting_merchant_max_amount),merchantInfo.getMaxAmount(),index)));
        list.add(create(new ParamItemModel(SAVE_MERCHANTCFG, FMT_BOOLEAN,getString(R.string.setting_merchant_enable_tle),merchantInfo.getMaxAmount(),index)));
        list.add(create(new ParamItemModel(SAVE_MERCHANTCFG, FMT_NUMBER,getString(R.string.setting_merchant_tle_index),merchantInfo.getMaxAmount(),index)));
        doUICmd_showSettingItem(list);
    }

    private void displayTerminalCfg() {
        List<SettingItemViewModel> list = new ArrayList<>();
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_NUMBER,getString(R.string.setting_terminal_operation_timeout),terminalCfg.getOperationTimeout())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_NUMBER_MAX_2,getString(R.string.setting_terminal_fallback_retry_time),terminalCfg.getFallbackRetryTime())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_NUMBER_MAX_2,getString(R.string.setting_terminal_max_offline_upload_num),terminalCfg.getMaxOfflineUploadNum())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_NUMBER_MAX_2,getString(R.string.setting_terminal_offline_upload_limit),terminalCfg.getOfflineUploadLimit())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_BOOLEAN,getString(R.string.setting_terminal_enable_auto_settlement),terminalCfg.isEnableAutoSettlement())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_BOOLEAN,getString(R.string.setting_terminal_enable_force_settlement),terminalCfg.isEnableForceSettlement())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_BOOLEAN,getString(R.string.setting_terminal_allow_fallback),terminalCfg.isAllowFallback())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_BOOLEAN,getString(R.string.setting_terminal_enable_training_mode),terminalCfg.isEnableTrainingMode()).setNeedCheckSuperPassword(true)));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_NUMBER_PASSWD_MAX_6,getString(R.string.setting_terminal_supper_password),terminalCfg.getSuperPassword()).setNeedCheckSuperPassword(true)));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_NUMBER_PASSWD_MAX_6,getString(R.string.setting_terminal_setting_password),terminalCfg.getTransManagerPassword()).setNeedCheckSuperPassword(true)));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_NUMBER_PASSWD_MAX_6,getString(R.string.setting_terminal_manager_password),terminalCfg.getTransManagerPassword()).setNeedCheckSuperPassword(true)));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_NUM_L_PADDING_FIX_6,getString(R.string.setting_terminal_invoice_num),terminalCfg.getSysInvoiceNum()).setNeedCheckBatchEmpty(true, isAllHostEmptyBatch())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_TIME_HH_MM,getString(R.string.setting_terminal_auto_settle_time),terminalCfg.getAutoSettlementTime())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_TIME_HH_MM,getString(R.string.setting_terminal_force_settle_time),terminalCfg.getForceSettlementTime())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_BOOLEAN,getString(R.string.setting_terminal_electronic_signature),terminalCfg.isEnableESign())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_BOOLEAN, getString(R.string.setting_terminal_enable_check_luhn), terminalCfg.isEnableCheckLuhn())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_BOOLEAN, getString(R.string.setting_terminal_enable_tip), terminalCfg.isEnableTip())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_AMOUNT,getString(R.string.setting_terminal_max_perauth_amount),terminalCfg.getMaxPreAuthAmount())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_NUMBER_MAX_6, getString(R.string.setting_terminal_max_tip_adjust_times), terminalCfg.getMaxAdjustTimes())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_BOOLEAN,getString(R.string.setting_terminal_iso_log),terminalCfg.isEnableISOLog())));
        list.add(create(new ParamItemModel(SAVE_TERMINALCFG, FMT_NUMBER_MAX_6,getString(R.string.setting_terminal_settle_failed_retry_interval),terminalCfg.getSettleFailedRetryIntervalMins())));
        doUICmd_showSettingItem(list);
    }

    private void displayCardBinInfo(int index) {
        CardBinInfo cardBinInfo = cardBinInfoList.get(index);
        List<SettingItemViewModel> list = new ArrayList<>();
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_CONST, getString(R.string.setting_carbin_card_idex),cardBinInfo.getCardIndex(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_CONST, getString(R.string.setting_carbin_card_name),cardBinInfo.getCardName(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_STRING, getString(R.string.setting_cardbin_card_print_label),cardBinInfo.getCardLabel(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_NUMBER, getString(R.string.setting_carbin_card_type),cardBinInfo.getCardType(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_PAN_10, getString(R.string.setting_carbin_pan_low),cardBinInfo.getPanLow(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_PAN_10, getString(R.string.setting_carbin_pan_high),cardBinInfo.getPanHigh(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_INDEXS_LIST, getString(R.string.setting_carbin_host_idex),cardBinInfo.getHostIndexs(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_NUMBER_MAX_1, getString(R.string.setting_carbin_cvv2_control),cardBinInfo.getCvv2Control(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_CVV2_MIN, getString(R.string.setting_cardbin_cvv2_min),cardBinInfo.getCvv2Min(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_CVV2_MAX, getString(R.string.setting_cardbin_cvv2_max),cardBinInfo.getCvv2Max(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_STRING, getString(R.string.setting_carbin_issue_id),cardBinInfo.getIssueId(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_BOOLEAN, getString(R.string.setting_carbin_allow_installment),cardBinInfo.isAllowInstallment(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_BOOLEAN, getString(R.string.setting_carbin_allow_offline),cardBinInfo.isAllowOfflineSale(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_BOOLEAN, getString(R.string.setting_carbin_allow_refund),cardBinInfo.isAllowRefund(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_NUMBER_MAX_5, getString(R.string.setting_carbin_tip_percent),cardBinInfo.getTipPercent(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_STRING, getString(R.string.setting_cardbin_sign_limit_amount),cardBinInfo.getSignLimitAmount(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_STRING, getString(R.string.setting_cardbin_print_limit_amount),cardBinInfo.getPrintLimitAmount(),index)));
        list.add(create(new ParamItemModel(SAVE_CARDBININFO, FMT_BOOLEAN, getString(R.string.setting_cardbin_allow_pin_bypass),cardBinInfo.isAllowPinBypass(),index)));
        doUICmd_showSettingItem(list);
    }

    private void displayTransSwitch(int index) {
        SwitchParameter switchParameter = transSwitchList.get(index);
        List<SettingItemViewModel> list = new ArrayList<>();
        list.add(create(new ParamItemModel(SAVE_TRANS_SWITCH, FMT_BOOLEAN, getString(R.string.setting_trans_switch_enable_transaction), switchParameter.isEnableTrans(), index)));
        list.add(create(new ParamItemModel(SAVE_TRANS_SWITCH, FMT_BOOLEAN, getString(R.string.setting_trans_switch_enable_tip), switchParameter.isEnableEnterTip(), index)));
        list.add(create(new ParamItemModel(SAVE_TRANS_SWITCH, FMT_BOOLEAN, getString(R.string.setting_trans_switch_enable_manual_input), switchParameter.isEnableManual(), index)));
        list.add(create(new ParamItemModel(SAVE_TRANS_SWITCH, FMT_BOOLEAN, getString(R.string.setting_trans_switch_enable_swipe_card), switchParameter.isEnableSwipeCard(), index)));
        list.add(create(new ParamItemModel(SAVE_TRANS_SWITCH, FMT_BOOLEAN, getString(R.string.setting_trans_switch_enable_insert_card), switchParameter.isEnableInsertCard(), index)));
        list.add(create(new ParamItemModel(SAVE_TRANS_SWITCH, FMT_BOOLEAN, getString(R.string.setting_trans_switch_enable_tap_card), switchParameter.isEnableTapCard(), index)));
        list.add(create(new ParamItemModel(SAVE_TRANS_SWITCH, FMT_BOOLEAN, getString(R.string.setting_trans_switch_enable_emv_force_online), switchParameter.isEMVForceOnline(), index)));
        list.add(create(new ParamItemModel(SAVE_TRANS_SWITCH, FMT_BOOLEAN, getString(R.string.setting_trans_switch_enable_check_reversal), switchParameter.isEnableCheckReversal(), index)));
        list.add(create(new ParamItemModel(SAVE_TRANS_SWITCH, FMT_BOOLEAN, getString(R.string.setting_trans_switch_enable_input_manager_pwd), switchParameter.isEnableInputManagerPwd(), index)));
        doUICmd_showSettingItem(list);
    }

    private void displayPrintConfigs(int index) {
        PrintConfig printConfig = printConfigList.get(index);
        List<SettingItemViewModel> list = new ArrayList<>();
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_CONST, getString(R.string.setting_print_config_index), printConfig.getIndex(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_BOOLEAN, getString(R.string.setting_print_config_print_customer_copy), printConfig.isPrintCustomerCopy(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_BOOLEAN, getString(R.string.setting_print_config_print_merchant_copy), printConfig.isPrintMerchantCopy(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_BOOLEAN, getString(R.string.setting_print_config_print_bank_copy), printConfig.isPrintBankCopy(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_STRING, getString(R.string.setting_print_config_header1), printConfig.getHeader1(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_STRING, getString(R.string.setting_print_config_header2), printConfig.getHeader2(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_STRING, getString(R.string.setting_print_config_header3), printConfig.getHeader3(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_STRING, getString(R.string.setting_print_config_header4), printConfig.getHeader4(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_STRING, getString(R.string.setting_print_config_header5), printConfig.getHeader5(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_STRING, getString(R.string.setting_print_config_header6), printConfig.getHeader6(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_STRING, getString(R.string.setting_print_config_footer1), printConfig.getFooter1(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_STRING, getString(R.string.setting_print_config_footer2), printConfig.getFooter2(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_STRING, getString(R.string.setting_print_config_footer3), printConfig.getFooter3(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_STRING, getString(R.string.setting_print_config_footer4), printConfig.getFooter4(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_STRING, getString(R.string.setting_print_config_footer5), printConfig.getFooter5(), index)));
        list.add(create(new ParamItemModel(SAVE_PRINT_CONFIG, FMT_STRING, getString(R.string.setting_print_config_footer6), printConfig.getFooter6(), index)));

        doUICmd_showSettingItem(list);
    }

    private void printISOLog() {
        doUICmd_setLoadingDialogStatus(true);
        PrintInfo printInfo = new PrintInfo(PrintType.ISO_LOG, 1);
        printInfo.setPrintLogoData(ResUtil.getByteFromDrawable(R.drawable.print_logo, 384f));
        useCaseStartPrintSlip.asyncExecute(printInfo).doOnNext(unused -> {
            doUICmd_setLoadingDialogStatus(false);
        }).doOnError(throwable -> {
            throwable.printStackTrace();
            doUICmd_setLoadingDialogStatus(false);
        }).subscribe();
    }

    private void saveChangedPrintConfig(SettingItemViewModel settingItemViewModel) {
        PrintConfig printConfig = printConfigList.get(settingItemViewModel.getArrayIndex());
        String label = settingItemViewModel.getLabel();
        LogUtil.d(TAG, "saveChangedPrintConfig label=" + label);
        if (label.equals(getString(R.string.setting_print_config_print_customer_copy))) {
            printConfig.setPrintCustomerCopy(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_print_config_print_merchant_copy))) {
            printConfig.setPrintMerchantCopy(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_print_config_print_bank_copy))) {
            printConfig.setPrintBankCopy(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_print_config_header1))) {
            printConfig.setHeader1(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_print_config_header2))) {
            printConfig.setHeader2(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_print_config_header3))) {
            printConfig.setHeader3(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_print_config_header4))) {
            printConfig.setHeader4(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_print_config_header5))) {
            printConfig.setHeader5(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_print_config_header6))) {
            printConfig.setHeader6(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_print_config_footer1))) {
            printConfig.setFooter1(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_print_config_footer2))) {
            printConfig.setFooter2(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_print_config_footer3))) {
            printConfig.setFooter3(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_print_config_footer4))) {
            printConfig.setFooter4(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_print_config_footer5))) {
            printConfig.setFooter5(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_print_config_footer6))) {
            printConfig.setFooter6(settingItemViewModel.getText());
        }

        useCaseSavePrintConfig.execute(printConfig);
    }

    private void displayInstallmentPromo(int index) {
        InstallmentPromo installmentPromo = installmentPromoList.get(index);
        List<SettingItemViewModel> list = new ArrayList<>();
        list.add(create(new ParamItemModel(SAVE_INSTALLMENT_PROMO, FMT_CONST, getString(R.string.setting_installment_index), installmentPromo.getIndex(), index)));
        list.add(create(new ParamItemModel(SAVE_INSTALLMENT_PROMO, FMT_BOOLEAN, getString(R.string.setting_installment_enable_promo), installmentPromo.isEnablePromo(), index)));
        list.add(create(new ParamItemModel(SAVE_INSTALLMENT_PROMO, FMT_STRING, getString(R.string.setting_installment_promo_label), installmentPromo.getPromoLabel(), index)));
        list.add(create(new ParamItemModel(SAVE_INSTALLMENT_PROMO, FMT_STRING, getString(R.string.setting_installment_term_array), installmentPromo.getTermList(), index)));
        list.add(create(new ParamItemModel(SAVE_INSTALLMENT_PROMO, FMT_STRING, getString(R.string.setting_installment_promo_code), installmentPromo.getPromoCode(), index)));
        doUICmd_showSettingItem(list);
    }

    private void saveChangedInstallmentPromo(SettingItemViewModel settingItemViewModel) {
        InstallmentPromo installmentPromo = installmentPromoList.get(settingItemViewModel.getArrayIndex());
        String label =settingItemViewModel.getLabel();
        if (label.equals(getString(R.string.setting_installment_index))) {
        } else if (label.equals(getString(R.string.setting_installment_enable_promo))) {
            installmentPromo.setEnablePromo(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_installment_promo_label))) {
            installmentPromo.setPromoLabel(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_installment_term_array))) {
            installmentPromo.setTermList(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_installment_promo_code))) {
            installmentPromo.setPromoCode(settingItemViewModel.getText());
        }
        useCaseSaveInstallmentPromo.execute(installmentPromo);
    }

    private void saveChangedTransactionSwitchItem(SettingItemViewModel settingItemViewModel) {
        SwitchParameter switchParameter = transSwitchList.get(settingItemViewModel.getArrayIndex());
        String label =settingItemViewModel.getLabel();
        if (label.equals(getString(R.string.setting_trans_switch_enable_transaction))) {
            switchParameter.setEnableTrans(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_trans_switch_enable_tip))) {
            switchParameter.setEnableEnterTip(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_trans_switch_enable_manual_input))) {
            switchParameter.setEnableManual(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_trans_switch_enable_swipe_card))) {
            switchParameter.setEnableSwipeCard(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_trans_switch_enable_insert_card))) {
            switchParameter.setEnableInsertCard(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_trans_switch_enable_insert_card))) {
            switchParameter.setEnableInsertCard(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_trans_switch_enable_tap_card))) {
            switchParameter.setEnableTapCard(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_trans_switch_enable_emv_force_online))) {
            switchParameter.setEMVForceOnline(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_trans_switch_enable_check_reversal))) {
            switchParameter.setEnableCheckReversal(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_trans_switch_enable_input_manager_pwd))) {
            switchParameter.setEnableInputManagerPwd(settingItemViewModel.isChecked());
        }

        useCaseSaveTransactionSwitch.execute(switchParameter);
    }

    private void saveChangedMerchantCfgItem(SettingItemViewModel settingItemViewModel) {
        MerchantInfo merchantInfo = merchantInfoList.get(settingItemViewModel.getArrayIndex());
        String label =settingItemViewModel.getLabel();
        if(label.equals(getString(R.string.setting_merchant_merchant_name))){
            merchantInfo.setMerchantName(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_merchant_terminal_id))){
            merchantInfo.setTerminalId(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_merchant_merchant_id))){
            merchantInfo.setMerchantId(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_merchant_trace_num))){
            merchantInfo.setTraceNum(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_merchant_batch_num))){
            merchantInfo.setBatchNum(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_merchant_currency_index))){
            merchantInfo.setCurrencyIndex(Integer.parseInt(settingItemViewModel.getText()));
        } else if(label.equals(getString(R.string.setting_merchant_enable_multi_currency))){
            merchantInfo.setEnableMultiCurrency(settingItemViewModel.isChecked());
        } else if(label.equals(getString(R.string.setting_merchant_print_param_index))){
            merchantInfo.setPrintParamIndex(Integer.parseInt(settingItemViewModel.getText()));
        } else if(label.equals(getString(R.string.setting_merchant_amount_digits))){
            merchantInfo.setAmountDigits(Integer.parseInt(settingItemViewModel.getText()));
        } else if(label.equals(getString(R.string.setting_merchant_min_amount))){
            merchantInfo.setMinAmount(Long.parseLong(settingItemViewModel.getText()));
        } else if(label.equals(getString(R.string.setting_merchant_max_amount))){
            merchantInfo.setMaxAmount(Long.parseLong(settingItemViewModel.getText()));
        } else if (label.equals(getString(R.string.setting_merchant_tle_index))) {
            merchantInfo.setTleIndex(Integer.parseInt(settingItemViewModel.getText()));
        } else if (label.equals(getString(R.string.setting_merchant_enable_tle))) {
            merchantInfo.setEnableTle(settingItemViewModel.isChecked());
        }
        useCaseSaveMerchantInfo.execute(merchantInfo);
    }

    private void saveChangedTerminalCfgItem(SettingItemViewModel settingItemViewModel) {
        String label = settingItemViewModel.getLabel();
        if(label.equals(getString(R.string.setting_terminal_operation_timeout))){
            terminalCfg.setOperationTimeout(Integer.parseInt(settingItemViewModel.getText()));
        } else if(label.equals(getString(R.string.setting_terminal_supper_password))){
            terminalCfg.setSuperPassword(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_terminal_setting_password))){
            terminalCfg.setSettingPassword(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_terminal_manager_password))){
            terminalCfg.setTransManagerPassword(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_terminal_manual_password))){
            terminalCfg.setManualPassword(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_terminal_invoice_num))){
            terminalCfg.setSysInvoiceNum(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_terminal_enable_auto_settlement))){
            terminalCfg.setEnableAutoSettlement(settingItemViewModel.isChecked());
            if (terminalCfg.isEnableAutoSettlement()) {
                useCaseSaveTerminalCfg.execute(terminalCfg);
                AndroidUtil.sendBroadcast(AutoSettlementReceiver.ACTION_SET_AUTO_SETTLEMENT_TIMER);
            } else {
                AndroidUtil.sendBroadcast(AutoSettlementReceiver.ACTION_CLEAR_AUTO_SETTLEMENT_TIMER);
            }
        } else if(label.equals(getString(R.string.setting_terminal_auto_settle_time))){
            terminalCfg.setAutoSettlementTime(settingItemViewModel.getText());
            if (terminalCfg.isEnableAutoSettlement()) {
                useCaseSaveTerminalCfg.execute(terminalCfg);
                AndroidUtil.sendBroadcast(AutoSettlementReceiver.ACTION_SET_AUTO_SETTLEMENT_TIMER);
            }
        } else if(label.equals(getString(R.string.setting_terminal_enable_force_settlement))){
            terminalCfg.setEnableForceSettlement(settingItemViewModel.isChecked());
            if (terminalCfg.isEnableForceSettlement()) {
                useCaseSaveTerminalCfg.execute(terminalCfg);
                AndroidUtil.sendBroadcast(ForceSettlementReceiver.ACTION_SET_FORCE_SETTLEMENT_TIMER);
            } else {
                AndroidUtil.sendBroadcast(ForceSettlementReceiver.ACTION_CLEAR_FORCE_SETTLEMENT_TIMER);
            }
        } else if(label.equals(getString(R.string.setting_terminal_force_settle_time))){
            terminalCfg.setForceSettlementTime(settingItemViewModel.getText());
            if (terminalCfg.isEnableForceSettlement()) {
                useCaseSaveTerminalCfg.execute(terminalCfg);
                AndroidUtil.sendBroadcast(ForceSettlementReceiver.ACTION_SET_FORCE_SETTLEMENT_TIMER);
            }
        } else if(label.equals(getString(R.string.setting_terminal_allow_fallback))){
            terminalCfg.setAllowFallback(settingItemViewModel.isChecked());
        } else if(label.equals(getString(R.string.setting_terminal_fallback_retry_time))){
            terminalCfg.setFallbackRetryTime(Integer.parseInt(settingItemViewModel.getText()));
        } else if(label.equals(getString(R.string.setting_terminal_max_offline_upload_num))){
            terminalCfg.setMaxOfflineUploadNum(Integer.parseInt(settingItemViewModel.getText()));
        } else if(label.equals(getString(R.string.setting_terminal_offline_upload_limit))){
            terminalCfg.setOfflineUploadLimit(Integer.parseInt(settingItemViewModel.getText()));
        } else if(label.equals(getString(R.string.setting_terminal_enable_training_mode))){
            terminalCfg.setEnableTrainingMode(settingItemViewModel.isChecked());
        } else if(label.equals(getString(R.string.setting_terminal_iso_log))){
            terminalCfg.setEnableISOLog(settingItemViewModel.isChecked());
        } else if(label.equals(getString(R.string.setting_terminal_max_perauth_amount))){
            terminalCfg.setMaxPreAuthAmount(StringUtil.parseLong(settingItemViewModel.getText(), 0));
        } else if (label.equals(getString(R.string.setting_terminal_electronic_signature))) {
            terminalCfg.setEnableESign(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_terminal_enable_check_luhn))) {
            terminalCfg.setEnableCheckLuhn(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_terminal_enable_tip))) {
            terminalCfg.setEnableTip(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_terminal_settle_failed_retry_interval))) {
            terminalCfg.setSettleFailedRetryIntervalMins(StringUtil.parseInt(settingItemViewModel.getText(), 2));
        }
        useCaseSaveTerminalCfg.execute(terminalCfg);
    }

    private void saveChangedCardBinInfoItem(SettingItemViewModel settingItemViewModel) {
        CardBinInfo cardBinInfo = cardBinInfoList.get(settingItemViewModel.getArrayIndex());
        String label = settingItemViewModel.getLabel();
        LogUtil.d(TAG, "label=" + label);

        if (label.equals(getString(R.string.setting_carbin_card_lable))) {
            cardBinInfo.setCardLabel(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_carbin_card_type))) {
            cardBinInfo.setCardType(StringUtil.parseInt(settingItemViewModel.getText(), 0));
        } else if (label.equals(getString(R.string.setting_carbin_pan_low))) {
            cardBinInfo.setPanLow(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_carbin_pan_high))) {
            cardBinInfo.setPanHigh(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_carbin_allow_installment))) {
            cardBinInfo.setAllowInstallment(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_carbin_allow_offline))) {
            cardBinInfo.setAllowOfflineSale(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_carbin_allow_refund))) {
            cardBinInfo.setAllowRefund(settingItemViewModel.isChecked());
        } else if (label.equals(getString(R.string.setting_carbin_host_idex))) {
            cardBinInfo.setHostIndexs(settingItemViewModel.getText());
        } else if (label.equals(getString(R.string.setting_carbin_cvv2_control))) {
            cardBinInfo.setCvv2Control(StringUtil.parseInt(settingItemViewModel.getText(), CVV2ControlType.ALLOW_BYPASS));
        } else if (label.equals(getString(R.string.setting_cardbin_cvv2_max))) {
            cardBinInfo.setCvv2Max(StringUtil.parseInt(settingItemViewModel.getText(), 4));
        } else if (label.equals(getString(R.string.setting_cardbin_cvv2_min))) {
            cardBinInfo.setCvv2Min(StringUtil.parseInt(settingItemViewModel.getText(), 0));
        } else if (label.equals(getString(R.string.setting_carbin_tip_percent))) {
            cardBinInfo.setTipPercent(StringUtil.parseInt(settingItemViewModel.getText(), 100));
        } else if (label.equals(getString(R.string.setting_cardbin_sign_limit_amount))) {
            cardBinInfo.setSignLimitAmount(StringUtil.parseLong(settingItemViewModel.getText(), 0));
        } else if (label.equals(getString(R.string.setting_cardbin_print_limit_amount))) {
            cardBinInfo.setPrintLimitAmount(StringUtil.parseLong(settingItemViewModel.getText(), 0));
        } else if (label.equals(getString(R.string.setting_cardbin_allow_pin_bypass))) {
            cardBinInfo.setAllowPinBypass(settingItemViewModel.isChecked());
        }
        useCaseSaveCardBinInfo.execute(cardBinInfo);
    }

    private void doExportLogProcess() {
        doUICmd_cancelSettingUITimer();
        doUICmd_showCheckPasswordDialog(OperatorInfo.TYPE_EXPORT_LOG, isCorrectPassword -> {
            //ishtiak
            if (!isCorrectPassword) {
                LogOutPutUtil.setIsOutPutLog(true);
                doUICmd_showToastMessage(ResUtil.getString(R.string.setting_toast_export_log_enabled));
            }
        });
    }

    private void doUICmd_showSystemLanguageInterface() {
        execute(ui -> ui.showSystemLanguageInterface());
    }

    private void doUICmd_showSystemTimeInterface() {
        execute(ui -> ui.showSystemTimeInterface());
    }
}
