package com.vfi.android.payment.presentation.view.contracts;

import com.vfi.android.payment.presentation.models.MenuViewModel;
import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.ArrayList;

public interface MainMenuUI extends UI {
    void showMainMenu(ArrayList<MenuViewModel> menuModels);
    void navigatorToSubMenu();
    void startTmsUpdate(String tmsParams);
    void showTerminalSN(String sn);
}
