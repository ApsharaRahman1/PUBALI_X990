package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.models.MenuViewModel;
import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.ArrayList;

public interface SubMenuUI extends UI {
    void showSubMenu(ArrayList<MenuViewModel> menuViewModelList);
}
