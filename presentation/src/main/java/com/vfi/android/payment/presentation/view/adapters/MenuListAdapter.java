package com.vfi.android.payment.presentation.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.models.MenuViewModel;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MenuListAdapter extends RecyclerView.Adapter {
    private final String TAG = this.getClass().getSimpleName();
    private ArrayList<MenuViewModel> menuViewModelList;
    private OnItemClickListener onItemClickListener;

    public MenuListAdapter(ArrayList<MenuViewModel> menuViewModelList) {
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
        View viewRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_menu, parent, false);
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
        MenuViewModel menuViewModel = menuViewModelList.get(position);
//        menuViewHolder.getMenuIcon().setImageResource(getDrawbleResId(menuViewModel.getMenuIconResId()));
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

    private int getDrawbleResId(String drawableName) {
        Class drawable = R.drawable.class;
        Field field;
        int r_id;
        try {
            field = drawable.getField(drawableName);
            r_id = field.getInt(field.getName());
        } catch (Exception e) {
            r_id = R.mipmap.ic_launcher;
        }
        return r_id;
    }
}
