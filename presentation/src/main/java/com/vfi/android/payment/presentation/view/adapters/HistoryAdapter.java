package com.vfi.android.payment.presentation.view.adapters;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.models.HistoryItemModel;
import com.vfi.android.payment.presentation.models.OnClickDelayModel;
import com.vfi.android.payment.presentation.utils.ResUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryAdapter extends RecyclerView.Adapter {
    private final String TAG = TAGS.HISTORY;

    private List<HistoryItemModel> historyList = new ArrayList<>();
    private OnHistoryClickListener onClickListener;
    private OnItemRefreshListener onRefreshListener;
    private CountDownTimer countDownTimer;
    private Context context;
    private final int TYPE_ITEM = 0;
    private final int TYPE_FOOTER = 1;
    // load more
    public static final int PULLUP_LOAD_MORE = 0;
    // loading
    public static final int LOADING_MORE = 1;
    // no more
    public static final int NO_LOAD_MORE = 2;
    // load only one
    public static final int LOAD_ONLY_ONE = 3;
    // default
    private int mFooterStatus = PULLUP_LOAD_MORE;

    public HistoryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.d(TAG, "viewType=" + viewType + " TYPE_ITEM = 0 TYPE_FOOTER = 1");
        if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.app_loadmore_item, parent, false);
            return new FooterViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview_history_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
            ItemViewHolder ivh = (ItemViewHolder) holder;
            HistoryItemModel itemModel = historyList.get(position);

            if (itemModel.isNeedRedColor()) {
                ivh.tvPayType.setTextColor(context.getResources().getColor(R.color.color_red, context.getTheme()));
            } else {
                ivh.tvPayType.setTextColor(context.getResources().getColor(R.color.color_history_item, context.getTheme()));
            }

            if (position % 2 == 0) {
                ivh.ll_history_item.setBackgroundColor(context.getResources().getColor(R.color.color_bg_history_item, null));
            } else {
                ivh.ll_history_item.setBackgroundColor(context.getResources().getColor(R.color.color_white, null));
            }

            String amount2Show = itemModel.getTotalAmountText() + " " + itemModel.getCurrencyText();
            ivh.tvAmount.setText(amount2Show);
            ivh.tvInvoiceNo.setText(context.getString(R.string.invoice_num, itemModel.getInvoiceNum()));
            ivh.tvPayType.setText(itemModel.getTransTypeText());
            ivh.tvDate.setText(itemModel.getTransDateTimeText());
            holder.itemView.setOnClickListener(view -> {
                if (onClickListener != null) {
                    if (OnClickDelayModel.Control()) {
                        onClickListener.onClick(itemModel);
                    }
                }
            });
        } else if (getItemViewType(position) == TYPE_FOOTER) {
            FooterViewHolder fvh = (FooterViewHolder) holder;
            fvh.pbShowState.setVisibility(View.VISIBLE);
            switch (mFooterStatus) {
                case PULLUP_LOAD_MORE:
//                    LogUtil.i(TAG, "PULLUP_LOAD_MORE");
                    fvh.tvShowLoadingMsg.setText(ResUtil.getString(R.string.loading));
                    break;
                case LOADING_MORE:
//                    LogUtil.i(TAG, "LOAD_ONLY_ONE");
                    break;
                case LOAD_ONLY_ONE:
//                    LogUtil.i(TAG, "LOAD_ONLY_ONE");
                    fvh.llLoadingArea.setVisibility(View.GONE);
                    break;
                case NO_LOAD_MORE:
//                    LogUtil.i(TAG, "NO_LOAD_MORE");
                    fvh.llLoadingArea.setVisibility(View.VISIBLE);
                    fvh.pbShowState.setVisibility(View.GONE);
                    fvh.tvShowLoadingMsg.setText(ResUtil.getString(R.string.all_show_finish));
                    delayHideLoading(fvh.llLoadingArea);
                    break;
            }
            if (onRefreshListener != null) {
                onRefreshListener.onRefresh(mFooterStatus);
            }
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size() + 1;
    }

    public void resetList(List<HistoryItemModel> historyItemModels) {
        LogUtil.d(TAG, "resetList size=" + historyItemModels.size());
        historyList.clear();
        historyList.addAll(historyItemModels);
        notifyDataSetChanged();
    }

    public void addList(List<HistoryItemModel> historyItemModels) {
        LogUtil.d(TAG, "addList size=" + historyItemModels.size());
        historyList.addAll(historyItemModels);
        notifyDataSetChanged();
    }

    private void delayHideLoading(LinearLayout llLoadingArea) {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    llLoadingArea.setVisibility(View.GONE);
                    countDownTimer = null;
                }
            }.start();
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_history_item)
        LinearLayout ll_history_item;
        @BindView(R.id.tv_amount)
        TextView tvAmount;
        @BindView(R.id.tv_invoice_no)
        TextView tvInvoiceNo;
        @BindView(R.id.tv_pay_type)
        TextView tvPayType;
        @BindView(R.id.tv_date)
        TextView tvDate;

        private ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pbShowState)
        ProgressBar pbShowState;
        @BindView(R.id.tvShowLoadingMsg)
        TextView tvShowLoadingMsg;
        @BindView(R.id.llLoadingArea)
        LinearLayout llLoadingArea;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnHistoryClickListener {
        void onClick(HistoryItemModel historyItemModel);
    }

    public interface OnItemRefreshListener {
        void onRefresh(int state);
    }

    public void setOnClickListener(OnHistoryClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnRefreshListener(OnItemRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public void updateFooterStatus(int status) {
        mFooterStatus = status;
        notifyItemChanged(getItemCount() - 1);
    }

    public List<HistoryItemModel> getHistoryItems() {
        return historyList;
    }

    @Override
    public int getItemViewType(int position) {
        LogUtil.d(TAG, "getItemViewType position=[" + position + "] getItemCount=[" + getItemCount() + "]");
        return position == getItemCount() - 1 ? TYPE_FOOTER : TYPE_ITEM;
    }
}
