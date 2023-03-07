package com.vfi.android.payment.presentation.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.models.SettingMenuItemViewModel;

import java.util.List;

public class SettingMenuAdapter extends RecyclerView.Adapter {
    private final String TAG = this.getClass().getSimpleName();
    private List<SettingMenuItemViewModel> menuViewModelList;
    private OnItemClickListener onItemClickListener;

    public SettingMenuAdapter(List<SettingMenuItemViewModel> menuViewModelList) {
        this.menuViewModelList = menuViewModelList;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_setting_menu, parent, false);
        viewRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, (int)v.getTag());
                }
            }
        });
        return new MenuViewHolder(viewRoot);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MenuViewHolder menuViewHolder = (MenuViewHolder) holder;
        SettingMenuItemViewModel menuViewModel = menuViewModelList.get(position);
        menuViewHolder.getMenuIcon().setImageResource(menuViewModel.getMenuIconResId());
        menuViewHolder.getRootView().setTag(position);
        menuViewHolder.getMenuTitle().setText(menuViewModel.getMenuTitle());
    }

    @Override
    public int getItemCount() {
        LogUtil.d(TAG, "Item count=" + menuViewModelList.size());
        return menuViewModelList.size();
    }

    private class MenuViewHolder extends RecyclerView.ViewHolder {
        private View rootView;
        private ImageView menuIcon;
        private TextView menuTitle;

        public MenuViewHolder(View rootView) {
            super(rootView);

            this.rootView = rootView;
            menuIcon = rootView.findViewById(R.id.menu_icon);
            menuTitle = rootView.findViewById(R.id.menu_title);
        }

        public ImageView getMenuIcon() {
            return menuIcon;
        }

        public TextView getMenuTitle() {
            return menuTitle;
        }

        public View getRootView() {
            return rootView;
        }
    }
}
