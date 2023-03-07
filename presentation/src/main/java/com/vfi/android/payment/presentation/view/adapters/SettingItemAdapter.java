package com.vfi.android.payment.presentation.view.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;
import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.OperatorInfo;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.models.HostItemModel;
import com.vfi.android.payment.presentation.models.ParamItemModel;
import com.vfi.android.payment.presentation.models.SettingItemViewModel;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class SettingItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = TAGS.Setting;

    private List<SettingItemViewModel> itemModels = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;
    private AdapterDelegatesManager<List<SettingItemViewModel>> adapterDelegatesManager;
    private OnItemValueChangedListener onItemValueChangedListener;
    private Dialog dialog;

    public SettingItemAdapter(Context context){
        adapterDelegatesManager = new AdapterDelegatesManager<>();
        layoutInflater = LayoutInflater.from(context);
        adapterDelegatesManager.addDelegate(new LabelAndTextViewHolder())
                .addDelegate(new LabelAndEditViewHolder())
                .addDelegate(new LabelAndSwitchViewHolder())
                .addDelegate(new LabelAndNumViewHolder())
                .addDelegate(new LabelAndNumPasswdHolder())
                .addDelegate(new HostListViewHolder());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return adapterDelegatesManager.onCreateViewHolder(viewGroup,i);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        adapterDelegatesManager.onBindViewHolder(itemModels,i,viewHolder);
    }

    @Override
    public int getItemViewType(int position) {
        return adapterDelegatesManager.getItemViewType(itemModels,position);
    }

    public void setItemModels(List<SettingItemViewModel> itemModels) {
        this.itemModels.clear();
        this.itemModels.addAll(itemModels);
        notifyDataSetChanged();
    }

    public List<SettingItemViewModel> getItemModels() {
        return itemModels;
    }

    public List<SettingItemViewModel> getUnSavedCItemModels() {
        List<SettingItemViewModel> itemViewModels = new ArrayList<>();
        for (SettingItemViewModel item : itemModels) {
            if (item.isNeedSaveItem())
                itemViewModels.add(item);
        }
        return itemViewModels;
    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemValueChangedListener(OnItemValueChangedListener onItemValueChangedListener) {
        this.onItemValueChangedListener = onItemValueChangedListener;
    }

    public interface OnItemClickListener {
        void onItemClick(SettingItemViewModel itemViewModel);
    }

    public interface OnItemValueChangedListener {
        void onItemChanged(SettingItemViewModel itemViewModel);
    }

    public class LabelAndTextViewHolder extends AbsListItemAdapterDelegate<SettingItemViewModel,SettingItemViewModel, LabelAndTextViewHolder.ViewHolder> {

        @Override
        protected boolean isForViewType(@NonNull SettingItemViewModel item, @NonNull List<SettingItemViewModel> items, int position) {
            return SettingItemViewModel.ViewType.LABEL_AND_TEXT == items.get(position).getViewType();
        }

        @NonNull
        @Override
        protected ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
            return new ViewHolder(layoutInflater.inflate(R.layout.setting_item_lable_text,parent,false));
        }

        @Override
        protected void onBindViewHolder(@NonNull SettingItemViewModel item, @NonNull ViewHolder holder, @NonNull List<Object> payloads) {
            holder.label.setText(item.getLabel());
            holder.text.setText(item.getText());
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item));
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView label;
            TextView text;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.label = itemView.findViewById(R.id.tv_label);
                this.text = itemView.findViewById(R.id.tv_text);
            }
        }
    }

    public class LabelAndEditViewHolder extends AbsListItemAdapterDelegate<SettingItemViewModel,SettingItemViewModel, LabelAndEditViewHolder.ViewHolder> {

        @Override
        protected boolean isForViewType(@NonNull SettingItemViewModel item, @NonNull List<SettingItemViewModel> items, int position) {
            return SettingItemViewModel.ViewType.LABEL_AND_EDIT == items.get(position).getViewType();
        }

        @NonNull
        @Override
        protected ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
            return new ViewHolder(layoutInflater.inflate(R.layout.setting_item_lable_edit,parent,false));
        }

        @Override
        protected void onBindViewHolder(@NonNull SettingItemViewModel item, @NonNull ViewHolder holder, @NonNull List<Object> payloads) {
            holder.label.setText(item.getLabel());
            holder.edit.setText(item.getText());
            holder.edit.setOnFocusChangeListener((v, hasFocus) -> {
                LogUtil.d(TAG, "hasFocus=" + hasFocus + " isParamItem=" + (item.getParamItemModel() == null ? "null" : "not null"));
                if (hasFocus) {
                    holder.edit.clearFocus();
                    doEditParameterProcess(v.getContext(), item, holder.edit);
                }
            });
        }

        private class ViewHolder extends RecyclerView.ViewHolder{
            TextView label;
            EditText edit;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.label = itemView.findViewById(R.id.lable);
                this.edit = itemView.findViewById(R.id.edit);
                InputMethodManager imm = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit.getWindowToken(),0);
            }
        }
    }

    public class LabelAndNumPasswdHolder extends AbsListItemAdapterDelegate<SettingItemViewModel,SettingItemViewModel, LabelAndNumPasswdHolder.ViewHolder> {

        @Override
        protected boolean isForViewType(@NonNull SettingItemViewModel item, @NonNull List<SettingItemViewModel> items, int position) {
            return SettingItemViewModel.ViewType.LABEL_AND_NUM_PASSWD == items.get(position).getViewType();
        }

        @NonNull
        @Override
        protected ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
            return new ViewHolder(layoutInflater.inflate(R.layout.setting_item_lable_passwd,parent,false));
        }

        @Override
        protected void onBindViewHolder(@NonNull SettingItemViewModel item, @NonNull LabelAndNumPasswdHolder.ViewHolder viewHolder, @NonNull List<Object> payloads) {
            viewHolder.label.setText(item.getLabel());
            viewHolder.edit.setText(item.getText());
            viewHolder.edit.setOnFocusChangeListener((v, hasFocus) -> {
                LogUtil.d(TAG, "hasFocus=" + hasFocus + " isParamItem=" + (item.getParamItemModel() == null ? "null" : "not null"));
                if (hasFocus) {
                    viewHolder.edit.clearFocus();
                    doEditParameterProcess(v.getContext(), item, viewHolder.edit);
                }
            });
        }

        private class ViewHolder extends RecyclerView.ViewHolder{
            TextView label;
            EditText edit;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.label = itemView.findViewById(R.id.lable);
                this.edit = itemView.findViewById(R.id.edit);
                InputMethodManager imm = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit.getWindowToken(),0);
            }
        }
    }

    public class LabelAndSwitchViewHolder extends AbsListItemAdapterDelegate<SettingItemViewModel,SettingItemViewModel, LabelAndSwitchViewHolder.ViewHolder> {

        @Override
        protected boolean isForViewType(@NonNull SettingItemViewModel item, @NonNull List<SettingItemViewModel> items, int position) {
            return SettingItemViewModel.ViewType.LABEL_AND_SWITCH == items.get(position).getViewType();
        }

        @NonNull
        @Override
        protected ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
            return new ViewHolder(layoutInflater.inflate(R.layout.setting_item_lable_switch,parent,false));
        }

        @Override
        protected void onBindViewHolder(@NonNull SettingItemViewModel item, @NonNull ViewHolder holder, @NonNull List<Object> payloads) {
            holder.label.setText(item.getLabel());
            holder.aSwitch.setOnClickListener(null);
            holder.aSwitch.setChecked(item.isChecked());
            holder.aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!buttonView.isPressed()) {
                    return;
                }
                item.setChecked(isChecked);
                onItemValueChangedListener.onItemChanged(item);
            });
        }

        private class ViewHolder extends RecyclerView.ViewHolder{
            TextView label;
            Switch aSwitch;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.label = itemView.findViewById(R.id.switch_label);
                this.aSwitch = itemView.findViewById(R.id.item_switch);
            }
        }
    }

    public class LabelAndNumViewHolder extends AbsListItemAdapterDelegate<SettingItemViewModel,SettingItemViewModel, LabelAndNumViewHolder.ViewHolder> {

        @Override
        protected boolean isForViewType(@NonNull SettingItemViewModel item, @NonNull List<SettingItemViewModel> items, int position) {
            return SettingItemViewModel.ViewType.LABEL_AND_NUM == items.get(position).getViewType();
        }

        @NonNull
        @Override
        protected ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
            return new ViewHolder(layoutInflater.inflate(R.layout.setting_item_lable_num,parent,false));
        }

        @Override
        protected void onBindViewHolder(@NonNull SettingItemViewModel item, @NonNull ViewHolder holder, @NonNull List<Object> payloads) {
            holder.label.setText(item.getLabel());
            holder.edit.setText(item.getText());
            holder.edit.setOnFocusChangeListener((v, hasFocus) -> {
                LogUtil.d(TAG, "hasFocus=" + hasFocus + " isParamItem=" + (item.getParamItemModel() == null ? "null" : "not null"));
                if (hasFocus) {
                    holder.edit.clearFocus();
                    doEditParameterProcess(v.getContext(), item, holder.edit);
                }
            });
        }

        private class ViewHolder extends RecyclerView.ViewHolder{
            TextView label;
            EditText edit;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.label = itemView.findViewById(R.id.num_label);
                this.edit = itemView.findViewById(R.id.ed_num);
                InputMethodManager imm = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit.getWindowToken(),0);
            }
        }
    }

    public class HostListViewHolder extends AbsListItemAdapterDelegate<SettingItemViewModel,SettingItemViewModel, HostListViewHolder.ViewHolder> {

        @Override
        protected boolean isForViewType(@NonNull SettingItemViewModel item, @NonNull List<SettingItemViewModel> items, int position) {
            return SettingItemViewModel.ViewType.HOST_INFO == items.get(position).getViewType();
        }

        @NonNull
        @Override
        protected ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
            return new ViewHolder(layoutInflater.inflate(R.layout.setting_item_host_list,parent,false));
        }

        @Override
        protected void onBindViewHolder(@NonNull SettingItemViewModel item, @NonNull ViewHolder holder, @NonNull List<Object> payloads) {
            HostItemModel hostItemModel = item.getHostItemModel();
            if (hostItemModel == null) {
                return;
            }

            holder.hostName.setText(hostItemModel.getHostName());
            holder.ip.setText(ResUtil.getString(R.string.setting_ip) + " : "+ hostItemModel.getHostIp());
            holder.port.setText(ResUtil.getString(R.string.setting_port) +  " : "+ hostItemModel.getHostPort());
            holder.host_op_list.setOnClickListener(v -> {
                hostItemModel.setShowOperationListSelected(true);
                onItemClickListener.onItemClick(item);
            });
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item));
        }

        private class ViewHolder extends RecyclerView.ViewHolder{
            TextView hostName;
            TextView ip;
            TextView port;
            ImageView host_op_list;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.hostName = itemView.findViewById(R.id.tv_host_name);
                this.ip = itemView.findViewById(R.id.tv_host_ip);
                this.port = itemView.findViewById(R.id.tv_host_port);
                this.host_op_list = itemView.findViewById(R.id.ib_host_op_list);
            }
        }
    }

    public void disMissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void doEditParameterProcess(Context context, SettingItemViewModel item, EditText editText) {
        String paramName = item.getLabel();
        String paramValue = item.getText();
        ParamItemModel paramItemModel = item.getParamItemModel();
        if (paramItemModel != null) {
            LogUtil.d(TAG, "isNeedCheckBatchEmpty=" + paramItemModel.isNeedCheckBatchEmpty());
            LogUtil.d(TAG, "isCurrentEmptyBatch=" + paramItemModel.isCurrentEmptyBatch());
            if (paramItemModel.isNeedCheckBatchEmpty() && !paramItemModel.isCurrentEmptyBatch()) {
                ToastUtil.showToastLong(ResUtil.getString(R.string.setting_toast_not_allow_batch_not_empty));
            } else if (paramItemModel.isNeedCheckSuperPassword()) {
                dialog = DialogUtil.showPasswordDialog(context, OperatorInfo.TYPE_SUPER_MANAGER, isCorrectPassword -> {
                    if (!isCorrectPassword) {
                        return;
                    }

                    dialog = DialogUtil.showEditParameterDialog(context, paramName, paramValue, paramItemModel.getParamFormatType(), newParam -> {
                        if (onItemValueChangedListener != null) {
                            editText.setText(newParam);
                            item.setText(newParam);
                            onItemValueChangedListener.onItemChanged(item);
                        }
                    });
                });
            } else {
                dialog = DialogUtil.showEditParameterDialog(context, paramName, paramValue, paramItemModel.getParamFormatType(), newParam -> {
                    if (onItemValueChangedListener != null) {
                        editText.setText(newParam);
                        item.setText(newParam);
                        onItemValueChangedListener.onItemChanged(item);
                    }
                });
            }
        } else {
            int paramFormat = ParamItemModel.FMT_STRING;
            dialog = DialogUtil.showEditParameterDialog(context, paramName, paramValue, paramFormat, newParam -> {
                if (onItemValueChangedListener != null) {
                    editText.setText(newParam);
                    item.setText(newParam);
                    onItemValueChangedListener.onItemChanged(item);
                }
            });
        }
    }
}
