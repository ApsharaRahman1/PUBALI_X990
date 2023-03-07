package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.List;

public interface OptionUI extends UI {
    void showLogoutOptions(List<String> options);
    void showLoginUI();
    void navigatorToHistoryActivity();
}
