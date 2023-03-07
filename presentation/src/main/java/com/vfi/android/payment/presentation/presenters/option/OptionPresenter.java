package com.vfi.android.payment.presentation.presenters;


import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.interactor.repository.UseCaseSetDoingTransactionFlag;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.view.contracts.OptionUI;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class OptionPresenter extends BasePresenter<OptionUI> {
    private final String TAG = TAGS.Setting;

    private final UINavigator uiNavigator;

    @Inject
    public OptionPresenter( UINavigator uiNavigator) {
        this.uiNavigator = uiNavigator;
    }

    public void doExitProcess() {
    }

    public void doOptionProcess(List<String> options, int index) {
        switch (index) {
            case 0:
                break;
            case 1:
                doUICmd_navigatorToHistoryActivity();
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }

    private void doUICmd_showLoginUI() {
        execute(new UICommand<OptionUI>() {
            @Override
            public void execute(OptionUI ui) {
                ui.showLoginUI();
            }
        });
    }

    void doUICmd_showLogoutOptions(List<String> options) {
        execute(new UICommand<OptionUI>() {
            @Override
            public void execute(OptionUI ui) {
                ui.showLogoutOptions(options);
            }
        });
    }

    void doUICmd_navigatorToHistoryActivity() {
        execute(new UICommand<OptionUI>() {
            @Override
            public void execute(OptionUI ui) {
                ui.navigatorToHistoryActivity();
            }
        });
    }
}
