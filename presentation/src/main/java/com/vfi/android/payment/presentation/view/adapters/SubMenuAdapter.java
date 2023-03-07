package com.vfi.android.payment.presentation.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.models.MenuViewModel;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class SubMenuAdapter extends RecyclerView.Adapter {

    private ArrayList<MenuViewModel> menuViewModelList;
    private OnitemClickListener onItemClickListener;
    public SubMenuAdapter(ArrayList<MenuViewModel> list){
        this.menuViewModelList = list;
    }

    private class Holder extends RecyclerView.ViewHolder{

        View view;
        ImageView menuIcon ;
        TextView tvTitle;

        public Holder(View itemView) {
            super(itemView);
            view = itemView;
            menuIcon = itemView.findViewById(R.id.menu_icon_union_pay);
            tvTitle = itemView.findViewById(R.id.tv_submenu_title);
        }

        private ImageView getMenuIcon(){
            return menuIcon;
        }
        private View getView (){
            return view;
        }

        public TextView getTvTitle() {
            return tvTitle;
        }
    }

    public interface OnitemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnitemClickListener listener){
        this.onItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyview_sub_menu,parent,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null && (menuViewModelList.get((int) v.getTag()).getMenuIconResId() != -1)){
                    onItemClickListener.onItemClick(v,(int)v.getTag());
                }
            }
        });
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SubMenuAdapter.Holder menuViewHolder = (SubMenuAdapter.Holder) holder;
        MenuViewModel menuViewModel = menuViewModelList.get(position);
        if (menuViewModel.getMenuIconResId() != -1) {
            menuViewHolder.getMenuIcon().setImageResource(menuViewModel.getMenuIconResId());
        }
        menuViewHolder.getView().setTag(position);
        menuViewHolder.getTvTitle().setText("" + menuViewModelList.get(position).getMenuTitle());
    }

    @Override
    public int getItemCount() {
        return menuViewModelList.size();
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
