package com.vfi.android.payment.presentation.view.activities.history;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.models.HistoryItemModel;
import com.vfi.android.payment.presentation.presenters.history.HistoryPresenter;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseMvpActivity;
import com.vfi.android.payment.presentation.view.adapters.HistoryAdapter;
import com.vfi.android.payment.presentation.view.adapters.HistoryScrollListener;
import com.vfi.android.payment.presentation.view.contracts.HistoryUI;
import com.vfi.android.payment.presentation.view.itemdecorations.ItemDecorationWithoutFooter;
import com.vfi.android.payment.presentation.view.widget.EmptyQuerySearchView;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.vfi.android.payment.presentation.presenters.history.HistoryListPresenter.PAGE_SIZE;

public class HistoryActivity extends BaseMvpActivity<HistoryUI> implements HistoryUI {
    @BindView(R.id.et_invoice_num)
    EditText et_invoice_num;
    @BindView(R.id.iv_search)
    ImageView iv_search;
    @BindView(R.id.tv_top_title)
    TextView tv_top_title;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_summary_report)
    Button btn_summary_report;
    @BindView(R.id.btn_detail_report)
    Button btn_detail_report;

    @Inject
    HistoryPresenter historyPresenter;

    private HistoryAdapter adapter;
    private HistoryScrollListener historyScrollListener;
    private boolean isSelfClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setBackGround(R.color.color_top_background);
        tv_top_title.setText(getString(R.string.history_title));

        et_invoice_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    if (!isSelfClear) {
                        et_invoice_num.setHint(R.string.history_search_hint);
                        historyPresenter.showAllHistoryList();
                    }
                    isSelfClear = false;
                }
            }
        });

        et_invoice_num.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                historyPresenter.showHistoryListByInvoiceNo(et_invoice_num.getText().toString().trim());
                isSelfClear = true;
                et_invoice_num.setText("");
            }
            return false;
        });

        iv_search.setOnClickListener(v -> {
            historyPresenter.showHistoryListByInvoiceNo(et_invoice_num.getText().toString().trim());
            isSelfClear = true;
            et_invoice_num.setText("");
        });

        btn_summary_report.setOnClickListener(v -> {
            historyPresenter.printSummaryReport();
        });

        btn_detail_report.setOnClickListener(v -> {
            historyPresenter.printDetailReport();
        });

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
//        recyclerView.addItemDecoration(new ItemDecorationWithoutFooter(this));
        adapter = new HistoryAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(historyItemModel ->
                HistoryDetailActivity.launchActivity(this, historyItemModel.getInvoiceNum(), historyItemModel.getTraceNum()));
//        historyScrollListener = new HistoryScrollListener(llm, page ->
//                historyPresenter.addRecordInfoList(page * PAGE_SIZE));
//        recyclerView.addOnScrollListener(historyScrollListener);
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(historyPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_date) {
            int year, month, day;
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, 0,
                    (datePicker, y, m, d) -> historyPresenter.showHistoryListByDate(y + String.format("%02d", m + 1) + String.format("%02d", d))
                    , year, month, day);
            datePickerDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showRecordInfoList(int opType, List<HistoryItemModel> historyItemModels) {
        LogUtil.d(TAGS.HISTORY, "showRecordInfoList");
        if (opType == HistoryPresenter.OPTYPE_RESET) {
            adapter.resetList(historyItemModels);
            adapter.updateFooterStatus(HistoryAdapter.NO_LOAD_MORE);
        } else {
            adapter.addList(historyItemModels);
        }
        hideSoftInput();
    }

    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0); //force hide keyboard
        }
    }

    @Override
    public void showPrintFailedDialog(String errMsg, boolean isSummaryReport) {
        String negativeButtonText = ResUtil.getString(R.string.btn_hint_cancel);
        String positiveButtonText = ResUtil.getString(R.string.btn_hint_reprint);
        DialogUtil.showAskDialog(this, errMsg, negativeButtonText, positiveButtonText, new DialogUtil.AskDialogListener() {
            @Override
            public void onClick(boolean isSure) {
                if (isSure) {
                    historyPresenter.rePrint(isSummaryReport);
                }
            }
        });
    }
}
