package com.vfi.android.payment.presentation.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.models.SettlementResultModel;

import java.util.List;

public class SettlementResultAdapter extends RecyclerView.Adapter {
    private final String TAG = this.getClass().getSimpleName();
    private List<SettlementResultModel> settlementItemResultList;
    private OnItemClickListener onItemClickListener;
    private OnItemSelectChangedListener onItemSelectChangedListener;

    public SettlementResultAdapter(List<SettlementResultModel> settlementItemResultList) {
        this.settlementItemResultList = settlementItemResultList;
        LogUtil.d(TAG, "Settlement list count=" + settlementItemResultList.size());
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public interface OnItemSelectChangedListener {
        void onItemSelected(View view, List<SettlementResultModel> settlementItemModels, int position, boolean isChecked);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectChangedListener(OnItemSelectChangedListener onItemSelectChangedListener) {
        this.onItemSelectChangedListener = onItemSelectChangedListener;
    }

    public List<SettlementResultModel> getSettlementResultResultList() {
        return settlementItemResultList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_settlement_result, parent, false);
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
        SettlementResultModel viewModel = settlementItemResultList.get(position);
        viewHolder.getTitle().setText(viewModel.getSettlementItemTitle());
        switch (viewModel.getSettlementStatus()) {
            case SettlementResultModel.SETTLEMENT_SUCCESS:
                viewHolder.getImageView().setImageResource(R.drawable.icon_settle_success);
                break;
            case SettlementResultModel.SETTLEMENT_FAILED:
                viewHolder.getImageView().setImageResource(R.drawable.icon_settle_failed);
                break;
            case SettlementResultModel.SETTLEMENT_EMPTY_BATCH:
                viewHolder.getImageView().setImageResource(R.drawable.icon_settle_empty);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return settlementItemResultList.size();
    }

    private class SettlementViewHolder extends RecyclerView.ViewHolder {
        private View rootView;
        private TextView title;
        private ImageView imageView;

        public SettlementViewHolder(View rootView) {
            super(rootView);

            this.rootView = rootView;
            title = rootView.findViewById(R.id.tv_settlement_type);
            imageView = rootView.findViewById(R.id.imageview_settlement_result);
        }

        public TextView getTitle() {
            return title;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }
}
