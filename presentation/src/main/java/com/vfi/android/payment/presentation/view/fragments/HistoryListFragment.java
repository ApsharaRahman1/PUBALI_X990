package com.vfi.android.payment.presentation.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.FragmentComponent;
import com.vfi.android.payment.presentation.models.HistoryItemModel;
import com.vfi.android.payment.presentation.models.OnClickDelayModel;
import com.vfi.android.payment.presentation.presenters.history.HistoryListPresenter;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.activities.history.HistoryDetailActivity;
import com.vfi.android.payment.presentation.view.adapters.HistoryAdapter;
import com.vfi.android.payment.presentation.view.adapters.HistoryScrollListener;
import com.vfi.android.payment.presentation.view.contracts.HistoryListUI;
import com.vfi.android.payment.presentation.view.fragments.base.BaseMvpFragment;
import com.vfi.android.payment.presentation.view.itemdecorations.ItemDecorationWithoutFooter;


import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.vfi.android.payment.presentation.presenters.history.HistoryListPresenter.PAGE_SIZE;


public class HistoryListFragment extends BaseMvpFragment<HistoryListUI> implements HistoryListUI {
    public static final String TAG = "HistoryListFragment";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_summary_report)
    Button btn_summary_report;
    @BindView(R.id.btn_detail_report)
    Button btn_detail_report;

    @BindView(R.id.tv_date)
    TextView tvDate;

    @Inject
    HistoryListPresenter historyListPresenter;
    boolean needInitList;

    private HistoryAdapter adapter;
    private Unbinder unbinder;
    private HistoryScrollListener historyScrollListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    protected void initView() {
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new ItemDecorationWithoutFooter(getActivity()));
        adapter = new HistoryAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(historyItemModel ->
                HistoryDetailActivity.launchActivity(getActivity(), historyItemModel.getInvoiceNum(), historyItemModel.getTraceNum()));
        historyScrollListener = new HistoryScrollListener(llm, page ->
                historyListPresenter.addRecordInfoList(page * PAGE_SIZE));
        recyclerView.addOnScrollListener(historyScrollListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needInitList) {
            historyListPresenter.addReversalInfo();
            historyListPresenter.initRecordInfoList();
            needInitList = false;
        }
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(historyListPresenter, this);
    }

    @Override
    protected void injectComponent(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @OnClick({R.id.btn_summary_report, R.id.btn_detail_report})
    public void onClick(View view) {
        if (OnClickDelayModel.Control()) {
            if (view.getId() == R.id.btn_summary_report) {
                historyListPresenter.printSummaryReport();
            } else if (view.getId() == R.id.btn_detail_report) {
                historyListPresenter.printDetailReport();
            }
        }
    }

    @Override
    public void refreshHistoryByType(int type) {
        historyListPresenter.getRecordInfoListByType(type);
        tvDate.setVisibility(View.GONE);
    }

    @Override
    public void refreshHistoryByInvoiceNum(String invoiceNum) {
        hideIputKeyboard();
        if (TextUtils.isEmpty(invoiceNum)) {
            historyListPresenter.addReversalInfo();
            historyListPresenter.initRecordInfoList();
        } else {
            historyListPresenter.getRecordInfoByInvoiceNo(invoiceNum);
        }
        tvDate.setVisibility(View.GONE);
        btn_detail_report.setVisibility(View.GONE);
        btn_summary_report.setVisibility(View.GONE);
    }

    @Override
    public void refreshHistoryByDate(String date) {
        historyListPresenter.getRecordInfoListByDate(date);
        tvDate.setVisibility(View.VISIBLE);
        tvDate.setText(StringUtil.formatDate(date, "dd MMMM yyyy"));
    }

    @Override
    public void showPrintFailedDialog(String errMsg, boolean isSummaryReport) {
        String negativeButtonText = ResUtil.getString(R.string.btn_hint_cancel);
        String positiveButtonText = ResUtil.getString(R.string.btn_hint_reprint);
        DialogUtil.showAskDialog(getActivity(), errMsg, negativeButtonText, positiveButtonText, new DialogUtil.AskDialogListener() {
            @Override
            public void onClick(boolean isSure) {
                if (isSure) {
                    historyListPresenter.rePrint(isSummaryReport);
                }
            }
        });
    }

    @Override
    public void resetRecordInfoList(List<HistoryItemModel> historyItemModels) {
        historyScrollListener.clear();
        adapter.resetList(historyItemModels);
        adapter.updateFooterStatus(HistoryAdapter.NO_LOAD_MORE);
        updateFooterStatus(historyItemModels.size());
        hideSoftInput();
    }

    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0); //force hide keyboard
        }
    }

    @Override
    public void addRecordInfoList(List<HistoryItemModel> historyItemModels) {
        adapter.addList(historyItemModels);
        updateFooterStatus(historyItemModels.size());
    }


    public void updateFooterStatus(int size) {
        adapter.updateFooterStatus(size < PAGE_SIZE ? HistoryAdapter.NO_LOAD_MORE : HistoryAdapter.PULLUP_LOAD_MORE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }
}
