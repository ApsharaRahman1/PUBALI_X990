package com.vfi.android.payment.presentation.presenters;

import android.util.Log;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.consts.InterceptorResult;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalCfg;
import com.vfi.android.domain.interactor.repository.UseCaseGetTransSwitchParameter;
import com.vfi.android.domain.interactor.repository.UseCaseSaveMenuTitle;
import com.vfi.android.domain.interactor.transaction.UseCaseCheckAndInitTrans;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.MenuInfoToViewMapper;
import com.vfi.android.payment.presentation.mappers.MenuTitleMapper;
import com.vfi.android.payment.presentation.mappers.TransInterceptorResultMapper;
import com.vfi.android.payment.presentation.models.MenuViewModel;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.transflows.SaleUIFlow2;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TransUtil;
import com.vfi.android.payment.presentation.view.contracts.SubMenuUI;
import com.vfi.android.payment.presentation.view.menu.MenuInfo;
import com.vfi.android.payment.presentation.view.menu.MenuManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SubMenuPresenter extends BasePresenter<SubMenuUI> {
    private final String TAG = TAGS.UILevel;

    private final UseCaseGetTerminalCfg useCaseGetTerminalCfg;
    private final UseCaseCheckAndInitTrans useCaseCheckAndInitTrans;
    private final UseCaseGetCurTranData useCaseGetCurTranData;
    private final UseCaseSaveMenuTitle useCaseSaveMenuTitle;
    private final UseCaseGetTransSwitchParameter useCaseGetTransSwitchParameter;
    private CurrentTranData currentTranData;
    private final UINavigator uiNavigator;
    private final MenuInfoToViewMapper menuInfoToViewMapper;
    private final MenuManager menuManager;

    @Inject
    SubMenuPresenter(UseCaseGetTerminalCfg useCaseGetTerminalCfg,
                     UseCaseGetCurTranData useCaseGetCurTranData,
                     UseCaseCheckAndInitTrans useCaseCheckAndInitTrans,
                     UseCaseSaveMenuTitle useCaseSaveMenuTitle,
                     UseCaseGetTransSwitchParameter useCaseGetTransSwitchParameter,
                     UINavigator uiNavigator,
                     MenuManager menuManager,
                     MenuInfoToViewMapper menuInfoToViewMapper){
        currentTranData = useCaseGetCurTranData.execute(null);
        this.useCaseGetTerminalCfg = useCaseGetTerminalCfg;
        this.menuManager = menuManager;
        this.useCaseCheckAndInitTrans = useCaseCheckAndInitTrans;
        this.useCaseGetCurTranData = useCaseGetCurTranData;
        this.uiNavigator = uiNavigator;
        this.menuInfoToViewMapper = menuInfoToViewMapper;
        this.useCaseSaveMenuTitle = useCaseSaveMenuTitle;
        this.useCaseGetTransSwitchParameter = useCaseGetTransSwitchParameter;
    }

    @Override
    protected void onFirstUIAttachment() {
        doUICmd_showTitle(currentTranData.getTitle(), "");
        loadMenu();
    }

    private void loadMenu(){
        List<MenuInfo> menuInfos = menuManager.getSubMenuList(currentTranData.getMenuId());
        if(menuInfos.size() % 2 != 0) {
            MenuInfo menuInfo = new MenuInfo(-1, -1, -1, -1, -1, false, false);
            menuInfos.add(menuInfo);
        }

        doUICmd_showSubMenu(menuInfoToViewMapper.toViewModel(menuInfos));
    }

    private boolean doTransCheckAndInit(int transType) {
        boolean bRet = true;

        try {
            int ret = useCaseCheckAndInitTrans.execute(transType);
            if (ret != InterceptorResult.NORMAL) {
                doUICmd_showToastMessage(TransInterceptorResultMapper.toString(ret));
                bRet = false;
            }
        } catch (Exception e) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_init_trans_exception));
            e.printStackTrace();
            bRet = false;
        }

        return bRet;
    }

    public void startTradeFlow(MenuViewModel menuViewModel) {
        if (menuViewModel.isMenuGroup()) {
            // Menu item have sub items
//            useCaseSaveMenuTitle.execute(MenuTitleMapper.toString(menuViewModel.getTransType()));
//            useCaseSaveMenuId.execute(menuViewModel.getMenuID());
//            doUICmd_navigatorToSubMenu();
        } else {
            int selectTransType = menuViewModel.getTransType();
            LogUtil.d(TAG, "selectTransType=" + selectTransType);
            Log.d("dataxx", "startTradeFlow: r"+String.valueOf(selectTransType));

            if (doTransCheckAndInit(selectTransType)) {
                useCaseSaveMenuTitle.execute(MenuTitleMapper.toString(menuViewModel.getTransType()));
                TransUtil.doUINavigatorParamInit(uiNavigator, selectTransType, useCaseGetTerminalCfg, useCaseGetTransSwitchParameter);

                if (selectTransType == TransType.SALE) {
                    uiNavigator.setTransUiFlow(new SaleUIFlow2());
                } else {
                    uiNavigator.setTransUiFlow(null);
                }

                doUICmd_navigatorToNext();
            }
        }
    }

    private void doUICmd_showSubMenu(ArrayList<MenuViewModel> arrayList) {
        execute(new UICommand<SubMenuUI>() {
            @Override
            public void execute(SubMenuUI ui) {
                ui.showSubMenu(arrayList);
            }
        });
    }
}
