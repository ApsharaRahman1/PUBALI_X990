package com.vfi.android.payment.presentation.view.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.CheckCardPresenter;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TvShowUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.CheckCardUI;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckCardActivity extends BaseTransFlowActivity<CheckCardUI> implements CheckCardUI {
    @BindView(R.id.imageview_touch)
    ImageView imageview_touch;
    @BindView(R.id.imageview_cardin)
    ImageView imageview_cardin;
    @BindView(R.id.imageview_swipe)
    ImageView imageview_swipe;
    @BindView(R.id.tv_desc_touch)
    TextView tv_desc_touch;
    @BindView(R.id.tv_desc_cardin)
    TextView tv_desc_cardin;
    @BindView(R.id.tv_desc_swipe)
    TextView tv_desc_swipe;
    @BindView(R.id.tv_show_amount)
    TextView tv_show_amount;
    @BindView(R.id.btn_cancel)
    Button btn_cancel;
    @BindView(R.id.btn_manual_input)
    Button btn_manual_input;
    @BindView(R.id.ll_amount_area)
    LinearLayout ll_amount_area;

    @Inject
    CheckCardPresenter checkCardPresenter;
    private Dialog customDialog = null;
    private Lock lock = new ReentrantLock();
    private Dialog inputDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_card);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void onResume() {
        lock.lock();
        try {
            checkCardPresenter.startCheckCard();
        } finally {
            lock.unlock();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        lock.lock();
        try {
            checkCardPresenter.stopCheckCard();
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void onBackPressed() {
        // disable back press function
    }

    private void initView() {
        setBackGround(R.drawable.bg_checkcard);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCardPresenter.doCancelCheckCard();
            }
        });

        btn_manual_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCardPresenter.doManualInputCardNum();
            }
        });
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(checkCardPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void showAmount(String amount) {
        tv_show_amount.setVisibility(View.VISIBLE);
        tv_show_amount.setText(amount);
    }

    @Override
    public void hideAmount() {
        ll_amount_area.setVisibility(View.INVISIBLE);
    }

    @Override
    public void displayCheckCardIcon(boolean isSupportTap, boolean isSupportInsert, boolean isSupportSwipe) {
        if (isSupportTap) {
            imageview_touch.setVisibility(View.VISIBLE);
            tv_desc_touch.setVisibility(View.VISIBLE);
        } else {
            imageview_touch.setVisibility(View.GONE);
            tv_desc_touch.setVisibility(View.GONE);
        }

        if (isSupportInsert) {
            imageview_cardin.setVisibility(View.VISIBLE);
            tv_desc_cardin.setVisibility(View.VISIBLE);
        } else {
            imageview_cardin.setVisibility(View.GONE);
            tv_desc_cardin.setVisibility(View.GONE);
        }

        if (isSupportSwipe) {
            imageview_swipe.setVisibility(View.VISIBLE);
            tv_desc_swipe.setVisibility(View.VISIBLE);
        } else {
            imageview_swipe.setVisibility(View.GONE);
            tv_desc_swipe.setVisibility(View.GONE);
        }
    }

    @Override
    public void showTransNotAllowDialog() {
        DialogUtil.showWarnDialog(this, ResUtil.getString(R.string.tv_hint_trans_not_allowed), new DialogUtil.WarnDialogListener() {
            @Override
            public void onClick() {
                getUiNavigator().getUiFlowControlData().setGoBackToMainMenu(true);
                getUiNavigator().getUiFlowControlData().setNotNeedDialogConfirmUIBack(true);
                navigatorToNextStep();
            }
        });
    }

    @Override
    public void showCardNotSupportDialog() {

    }

    @Override
    public void showFallbackRemoveCardDialog(String msg) {
        customDialog = DialogUtil.showWarnDialog(this, msg, new DialogUtil.WarnDialogListener() {
            @Override
            public void onClick() {
                checkCardPresenter.userConfirmFallback();
            }
        });
    }

    @Override
    public void dismissFallbackRemoveCardDialog() {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
    }

    @Override
    public void enableManualInputBtn(boolean isEnable) {
        if (isEnable) {
            btn_manual_input.setVisibility(View.VISIBLE);
        } else {
            btn_manual_input.setVisibility(View.GONE);
        }
    }

    @Override
    public void showInputLast4CardNumDialog() {
        inputDialog = DialogUtil.showInputNumberDialog(this, "Last 4 digits Account", new DialogUtil.InputDialogListener() {
            @Override
            public void onInput(String inputStr, boolean isConfirm) {
                if (isConfirm) {
                    checkCardPresenter.checkLast4DigitAccount(inputStr);
                } else {
                    showAskDialog(ResUtil.getString(R.string.tv_hint_confirm_to_cancel), isSure -> {
                        if (isSure) {
                            getUiNavigator().getUiFlowControlData().setGoBackToMainMenu(true);
                            getUiNavigator().getUiFlowControlData().setNotNeedDialogConfirmUIBack(true);
                            navigatorToNextStep();
                        } else {
                            checkCardPresenter.startCheckCard();
                        }
                    });
                }
            }
        });
    }



    @Override
    protected void onDestroy() {
        dismissFallbackRemoveCardDialog();
        if (inputDialog != null && inputDialog.isShowing()) {
            inputDialog.dismiss();
        }
        super.onDestroy();
    }
}
