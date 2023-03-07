package com.vfi.android.payment.presentation.view.activities.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.vfi.android.domain.interactor.other.UseCaseTimer;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.models.OnClickDelayModel;
import com.vfi.android.payment.presentation.presenters.history.HistoryDetailPresenter;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseMvpActivity;
import com.vfi.android.payment.presentation.view.contracts.HistoryDetailUI;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryDetailActivity extends BaseMvpActivity<HistoryDetailUI> implements HistoryDetailUI {
    public static final String TAG = "HistoryDetailActivity";
    public static final String INTENT_KEY_TRACE_NO = "INTENT_KEY_TRACE_NO";
    public static final String INTENT_KEY_INVOICE_NO = "INTENT_KEY_INVOICE_NO";
    public static final String INTENT_KEY_IS_SETTLED = "INTENT_KEY_IS_SETTLED";
    public static final String INTENT_KEY_HOST_TYPE = "INTENT_KEY_HOST_TYPE";

    private String traceNo;
    private String invoiceNum;

    @BindView(R.id.tv_top_title)
    TextView tv_top_title;

    @Inject
    HistoryDetailPresenter historyDetailPresenter;
    @Inject
    UseCaseTimer useCaseTimer;

    public static void launchActivity(Context context, String invoiceNum, String traceNo) {
        Intent intent = new Intent(context, HistoryDetailActivity.class);
        intent.putExtra(INTENT_KEY_TRACE_NO, traceNo);
        intent.putExtra(INTENT_KEY_INVOICE_NO, invoiceNum);
//        intent.putExtra(INTENT_KEY_HOST_TYPE, hostType);
//        intent.putExtra(INTENT_KEY_IS_SETTLED, isSettled);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
    }

    public void initData() {
        traceNo = getIntent().getStringExtra(INTENT_KEY_TRACE_NO);
        invoiceNum = getIntent().getStringExtra(INTENT_KEY_INVOICE_NO);
        historyDetailPresenter.initialize(traceNo, invoiceNum);
    }

    public void initView() {
        setContentView(R.layout.activity_history_detail_card);
        ButterKnife.bind(this);

        setBackGround(R.color.color_top_background);
        tv_top_title.setText(getString(R.string.history_detail_title));
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(historyDetailPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    public void onClick(View view) {
        if (OnClickDelayModel.Control()) {
            switch (view.getId()) {
                case R.id.btn_reprint_up:
                case R.id.btn_reprint_down:
                    historyDetailPresenter.rePrintTransSlip(false);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra(INTENT_KEY_TRACE_NO, traceNo);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void setViewVisibility(int resId, int visibility) {
        View view = findViewById(resId);
        if (view != null)
            view.setVisibility(visibility);
    }

    @Override
    public void setViewText(int resId, String text) {
        TextView view = findViewById(resId);
        if (view != null)
            view.setText(text);
    }

    @Override
    public void showPrintException(String errMsg) {
        String negativeButtonText = ResUtil.getString(R.string.btn_hint_cancel);
        String positiveButtonText = ResUtil.getString(R.string.btn_hint_reprint);
        DialogUtil.showAskDialog(this, errMsg, negativeButtonText, positiveButtonText, new DialogUtil.AskDialogListener() {
            @Override
            public void onClick(boolean isSure) {
                if (isSure) {
                    historyDetailPresenter.rePrintTransSlip(true);
                }
            }
        });
    }

    @Override
    public void navigatorToHistoryActivity() {
        AndroidUtil.startActivity(this, HistoryActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
