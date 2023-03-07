package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.List;

public interface InputCardNumUI extends UI {
    void showTransNotAllowDialog();
    void clearCardNum();
}
