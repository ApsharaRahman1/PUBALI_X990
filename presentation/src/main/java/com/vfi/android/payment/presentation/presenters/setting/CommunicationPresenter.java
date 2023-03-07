package com.vfi.android.payment.presentation.presenters.setting;

import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.interactor.repository.UseCaseGetAllHostInfo;
import com.vfi.android.domain.interactor.repository.UseCaseGetAllMerchantInfo;
import com.vfi.android.domain.interactor.repository.UseCaseSaveHostInfo;
import com.vfi.android.domain.interactor.repository.UseCaseSaveMerchantInfo;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.BuildConfig;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.models.HostItemModel;
import com.vfi.android.payment.presentation.models.ParamItemModel;
import com.vfi.android.payment.presentation.presenters.base.BaseSettingItemPresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.CommunicationUI;
import com.vfi.android.payment.presentation.models.SettingItemViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_BOOLEAN;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_CONST;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_INDEXS_LIST;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_IP_ADDR;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_NUMBER;
import static com.vfi.android.payment.presentation.models.ParamItemModel.FMT_STRING;
import static com.vfi.android.payment.presentation.utils.ResUtil.*;

public class CommunicationPresenter extends BaseSettingItemPresenter<CommunicationUI> {
    private final UseCaseSaveHostInfo useCaseSaveHostInfo;
    private final UseCaseSaveMerchantInfo useCaseSaveMerchantInfo;
    private List<HostInfo> hostInfoList;
    private Map<Integer,List<SettingItemViewModel>> listMap;
    private List<MerchantInfo> merchantInfoList;

    private int lViewtype = SettingItemViewModel.ViewType.HOST_INFO;
    private int tViewtype = SettingItemViewModel.ViewType.LABEL_AND_TEXT;
    private int sViewtype = SettingItemViewModel.ViewType.LABEL_AND_SWITCH;

    private static final int ID_ROOT = 0;
    private static final int HOST_INFO = 01;
    private static final int ID_HOST_TLE = 02;
    private static final int MERCHANT_TLE = 03;

    @Inject
    public CommunicationPresenter(UseCaseGetAllHostInfo useCaseGetAllHostInfo,
                                  UseCaseGetAllMerchantInfo useCaseGetAllMerchantInfo,
                                  UseCaseSaveMerchantInfo useCaseSaveMerchantInfo,
                                  UseCaseSaveHostInfo useCaseSaveHostInfo){
        this.useCaseSaveHostInfo = useCaseSaveHostInfo;
        hostInfoList = useCaseGetAllHostInfo.execute(null);
        merchantInfoList = useCaseGetAllMerchantInfo.execute(null);
        this.useCaseSaveMerchantInfo = useCaseSaveMerchantInfo;
        initData();
    }

    @Override
    protected void onFirstUIAttachment() {
        super.onFirstUIAttachment();
        doUICmd_showTitle(ResUtil.getString(R.string.setting_communication));
    }

    private void initData() {
        listMap = new HashMap<>();
        List<SettingItemViewModel> list = new ArrayList<>();
        for(int i = 0;i<hostInfoList.size();i++){
            HostInfo hostInfo = hostInfoList.get(i);
            list.add(new SettingItemViewModel(ID_ROOT,lViewtype,new HostItemModel(hostInfo.getHostName(), hostInfo.getPrimaryIp(), hostInfo.getPrimaryPort()), i));
        }
        listMap.put(ID_ROOT,list);
    }

    @Override
    public List<SettingItemViewModel> initSettingItems() {
        return listMap.get(ID_ROOT);
    }

    @Override
    public void onItemSelected(SettingItemViewModel settingItemViewModel) {
        switch (settingItemViewModel.getId()) {
            case ID_ROOT:
                if (settingItemViewModel.getHostItemModel().isShowOperationListSelected()) {
                    settingItemViewModel.getHostItemModel().setShowOperationListSelected(false);
                    doUICmd_showHostOperationItemList(settingItemViewModel.getHostItemModel().getHostName(), settingItemViewModel);
                } else {
                    showHostDetail(settingItemViewModel);
                }
                break;
            case ID_HOST_TLE:
                displayHostTleStatus(settingItemViewModel);
                break;
        }
    }

    private void displayHostTleStatus(SettingItemViewModel settingItemViewModel) {
        HostInfo hostInfo = hostInfoList.get(settingItemViewModel.getArrayIndex());
        String merchantIndexs = hostInfo.getMerchantIndexs();
        if (merchantIndexs == null || merchantIndexs.length() == 0) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.tv_hint_error_no_merchant_found));
            return;
        }

        String[] merchantIndexsList = merchantIndexs.split(",");
        List<SettingItemViewModel> list = new ArrayList<>();
        for (int i = 0; i < merchantIndexsList.length; i++) {
            int merchantIndex = StringUtil.parseInt(merchantIndexsList[i], -1);
            MerchantInfo merchantInfo = getMerchantByIndex(merchantIndex);
            if (merchantInfo != null) {
                list.add(new SettingItemViewModel(MERCHANT_TLE, sViewtype, merchantInfo.getMerchantName(), merchantInfo.isEnableTle(), merchantIndex));
            }
        }

        if (list.size() == 0) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.tv_hint_error_no_merchant_found));
        } else {
            doUICmd_showSettingItem(list);
        }
    }

    private MerchantInfo getMerchantByIndex(int merchantIndex) {
        if (merchantInfoList == null) {
            LogUtil.e(TAG, "MerchantInfoList is null");
            return null;
        }

        for (MerchantInfo merchantInfo : merchantInfoList) {
            if (merchantInfo.getMerchantIndex() == merchantIndex) {
                return merchantInfo;
            }
        }

        LogUtil.e(TAG, "No merchant found, merchantIndex=" + merchantIndex);
        return null;
    }

    @Override
    public void onItemValueChanged(SettingItemViewModel settingItemViewModel) {
        switch (settingItemViewModel.getId()) {
            case HOST_INFO:
                saveChangedHostInfoItem(settingItemViewModel);
                break;
            case MERCHANT_TLE:
                saveMerchantTleStatus(settingItemViewModel);
                break;
        }
    }

    @Override
    public void processUnSavedItems(List<SettingItemViewModel> unSavedItemList) {

    }

    public void showHostDetail(SettingItemViewModel settingItemViewModel){
        HostInfo hostInfo = hostInfoList.get(settingItemViewModel.getArrayIndex());
        int index = settingItemViewModel.getArrayIndex();
        List<SettingItemViewModel> list = new ArrayList<>();
        list.add(create(new ParamItemModel(HOST_INFO, FMT_CONST, getString(R.string.setting_host_index), "" + hostInfo.getHostIndex(),index)));
        list.add(create(new ParamItemModel(HOST_INFO, FMT_CONST, getString(R.string.setting_host_name),hostInfo.getHostName(),index)));
        list.add(create(new ParamItemModel(HOST_INFO, FMT_IP_ADDR, getString(R.string.setting_host_primary_ip),hostInfo.getPrimaryIp(),index)));
        list.add(create(new ParamItemModel(HOST_INFO, FMT_NUMBER, getString(R.string.setting_host_primary_post), hostInfo.getPrimaryPort(),index)));
        list.add(create(new ParamItemModel(HOST_INFO, FMT_IP_ADDR, getString(R.string.setting_host_secondary_ip),hostInfo.getSecondaryIp(),index)));
        list.add(create(new ParamItemModel(HOST_INFO, FMT_NUMBER, getString(R.string.setting_host_secondary_post),String.valueOf(hostInfo.getSecondaryPort()),index)));
        list.add(create(new ParamItemModel(HOST_INFO, FMT_NUMBER, getString(R.string.setting_host_type),String.valueOf(hostInfo.getHostType()),index)));
        list.add(create(new ParamItemModel(HOST_INFO, FMT_NUMBER, getString(R.string.setting_host_nii),hostInfo.getNII(),index)));
        list.add(create(new ParamItemModel(HOST_INFO, FMT_NUMBER, getString(R.string.setting_host_tpdu),hostInfo.getTPDU(),index)));
        list.add(create(new ParamItemModel(HOST_INFO, FMT_INDEXS_LIST, getString(R.string.setting_host_merchant_indexs),hostInfo.getMerchantIndexs(),index)));
        list.add(new SettingItemViewModel(ID_HOST_TLE, tViewtype, getString(R.string.setting_host_tle),"", index));
        doUICmd_showSettingItem(list);
    }

    private void saveMerchantTleStatus(SettingItemViewModel settingItemViewModel) {
        MerchantInfo merchantInfo = getMerchantByIndex(settingItemViewModel.getArrayIndex());
        merchantInfo.setEnableTle(settingItemViewModel.isChecked());
        useCaseSaveMerchantInfo.execute(merchantInfo);
    }

    private void saveChangedHostInfoItem(SettingItemViewModel settingItemViewModel) {
        HostInfo hostInfo = hostInfoList.get(settingItemViewModel.getArrayIndex());
        String label = settingItemViewModel.getLabel();
        if (BuildConfig.DEBUG) {
            LogUtil.d(TAG, "Label" + label + "=[" + settingItemViewModel.getText() + "]");
        }
        if(label.equals(getString(R.string.setting_host_name))){
            hostInfo.setHostName(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_host_primary_ip))){
            hostInfo.setPrimaryIp(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_host_primary_post))){
            hostInfo.setPrimaryPort(Integer.parseInt(settingItemViewModel.getText()));
        } else if(label.equals(getString(R.string.setting_host_secondary_ip))){
            hostInfo.setSecondaryIp(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_host_secondary_post))){
            hostInfo.setSecondaryPort(Integer.parseInt(settingItemViewModel.getText()));
        } else if(label.equals(getString(R.string.setting_host_type))){
            hostInfo.setHostType(Integer.parseInt(settingItemViewModel.getText()));
        } else if(label.equals(getString(R.string.setting_host_nii))){
            hostInfo.setNII(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_host_tpdu))){
            hostInfo.setTPDU(settingItemViewModel.getText());
        } else if(label.equals(getString(R.string.setting_host_merchant_indexs))){
            hostInfo.setMerchantIndexs(settingItemViewModel.getText());
        }
        useCaseSaveHostInfo.execute(hostInfo);
    }

    public void loadTWK() {
    }

    public void loadTMK() {
    }

    public void testHost() {
    }

    private void doUICmd_showHostOperationItemList(String hostName, SettingItemViewModel itemViewModel) {
        execute(ui -> ui.showHostOperationItemList(hostName, itemViewModel));
    }
}
