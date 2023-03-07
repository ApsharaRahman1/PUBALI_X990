package com.vfi.android.payment.presentation.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.models.SettlementItemModel;

import java.util.Iterator;
import java.util.List;

public class SettlementAdapter extends RecyclerView.Adapter {
    private final String TAG = this.getClass().getSimpleName();
    private List<SettlementItemModel> settlementItemModelList;
    private OnItemClickListener onItemClickListener;
    private OnItemSelectChangedListener onItemSelectChangedListener;

    public SettlementAdapter(List<SettlementItemModel> settlementItemModelList) {
        this.settlementItemModelList = settlementItemModelList;
        LogUtil.d(TAG, "Settlement list count=" + settlementItemModelList.size());
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public interface OnItemSelectChangedListener {
        void onItemSelected(View view, List<SettlementItemModel> settlementItemModels, int position, boolean isChecked);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectChangedListener(OnItemSelectChangedListener onItemSelectChangedListener) {
        this.onItemSelectChangedListener = onItemSelectChangedListener;
    }

    public List<SettlementItemModel> getSettlementItemModelList() {
        return settlementItemModelList;
    }

    public void markAllItemCheckedStatus(boolean isAllNeedSelect) {
        if (settlementItemModelList != null) {
            Iterator<SettlementItemModel> iterator = settlementItemModelList.iterator();
            while (iterator.hasNext()) {
                SettlementItemModel model = iterator.next();
                LogUtil.d(TAG, "model settlementType=" + model.getHostType() + " isAllowSelected=" + model.isAllowSelected());
                if (model.isAllowSelected()) {
                    LogUtil.d(TAG, "------- model" + model.getHostType()  + " " + isAllNeedSelect);
                    model.setSelected(isAllNeedSelect);
                }

                LogUtil.d(TAG, "==== model " + model.isSelected());
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_settlement, parent, false);
        viewRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, (int)v.getTag());
                }
            }
        });

        return new SettlementViewHolder(viewRoot);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SettlementViewHolder viewHolder = (SettlementViewHolder) holder;
        SettlementItemModel viewModel = settlementItemModelList.get(position);
        viewHolder.getTitle().setText(viewModel.getSettlementItemTitle());
        viewHolder.getTotalAmount().setText(viewModel.getSettlementItemTotalAmount());

        if (!viewModel.isAllowSelected()) {
            viewHolder.getChoiceSettlement().setEnabled(false);
        } else {
            viewHolder.getChoiceSettlement().setEnabled(true);
        }

        viewHolder.getChoiceSettlement().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (viewModel.isAllowSelected()) {
                    LogUtil.d("***** model " + viewModel.getHostType() + " position=" + position + " isChecked=" + isChecked);
                    viewModel.setSelected(isChecked);
                }
                if (onItemSelectChangedListener != null) {
                    onItemSelectChangedListener.onItemSelected(buttonView, settlementItemModelList, position, viewModel.isSelected());
                }
            }
        });

        LogUtil.d("+++++++ model " + viewModel.isSelected() + " position=" + position + " type=" + viewModel.getHostType());
        viewHolder.getChoiceSettlement().setChecked(viewModel.isSelected());
    }

    @Override
    public int getItemCount() {
        return settlementItemModelList.size();
    }

    private class SettlementViewHolder extends RecyclerView.ViewHolder {
        private View rootView;
        private TextView title;
        private TextView totalAmount;
        private CheckBox choiceSettlement;

        public SettlementViewHolder(View rootView) {
            super(rootView);

            this.rootView = rootView;
            title = rootView.findViewById(R.id.tv_settlement_type);
            totalAmount = rootView.findViewById(R.id.tv_total_amount);
            choiceSettlement = rootView.findViewById(R.id.checkbox_choice_settlement);
        }

        public TextView getTitle() {
            return title;
        }

        public TextView getTotalAmount() {
            return totalAmount;
        }

        public CheckBox getChoiceSettlement() {
            return choiceSettlement;
        }
    }
}
