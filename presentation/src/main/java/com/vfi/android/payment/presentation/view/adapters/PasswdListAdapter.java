package com.vfi.android.payment.presentation.view.adapters;

import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;

public class PasswdListAdapter extends RecyclerView.Adapter {
    private final String TAG = this.getClass().getSimpleName();
    private final int MAX_PASSWD_LEN = 8;
    private int passwdBoxNum = 6;

    private int showPasswdLen = 0;
    private OnItemClickListener onItemClickListener;
    private StringBuffer passwdStringBuffer;

    public PasswdListAdapter(int passwdBoxNum) {
        if (passwdBoxNum > 0 && passwdBoxNum <= MAX_PASSWD_LEN) {
            this.passwdBoxNum = passwdBoxNum;
        }
        LogUtil.d(TAG, "Passwd box number=" + passwdBoxNum);
        passwdStringBuffer = new StringBuffer();
    }

    public void showPasswd(int passwdLen) {
        LogUtil.d(TAG, "show passwd passwdlen=" + passwdLen + " max box=" + passwdBoxNum);
        if (passwdLen < 0 || passwdLen > passwdBoxNum) {
            return;
        }

        boolean isUIThread = (Looper.myLooper() == Looper.getMainLooper());
        if (!isUIThread) {
            throw new RuntimeException("Need call this function on UI thread!");
        }

        showPasswdLen = passwdLen;
        notifyDataSetChanged();
    }

    public void addOnePasswordChar(String onePasswdChar) {
        LogUtil.d("str buff len=" + passwdStringBuffer.length());
        if (passwdStringBuffer.length() < passwdBoxNum) {
            if (onePasswdChar != null && onePasswdChar.length() == 1) {
                showPasswdLen++;
                showPasswd(showPasswdLen);
                passwdStringBuffer.append(onePasswdChar);
            }
        }
    }

    public void delOnePasswordChar() {
        int passwdLen = passwdStringBuffer.length();
        if (passwdLen > 0) {
            showPasswdLen--;
            showPasswd(showPasswdLen);
            passwdStringBuffer.delete(passwdLen - 1, passwdLen);
        }
    }

    public String getPasswd() {
        return passwdStringBuffer.toString();
    }

    public void clearPasswd() {
        showPasswd(0);
        passwdStringBuffer.delete(0, passwdStringBuffer.length());
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_passwd, parent, false);
        viewRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, (int)v.getTag());
                }
            }
        });
        return new PasswdViewHolder(viewRoot);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PasswdViewHolder passwdViewHolder = (PasswdViewHolder) holder;
        passwdViewHolder.getRootView().setTag(position);

        if (position <= showPasswdLen - 1) {
            passwdViewHolder.getTvPasswd().setText("●");
        } else {
            passwdViewHolder.getTvPasswd().setText("");
        }
    }

    @Override
    public int getItemCount() {
        return passwdBoxNum;
    }

    private class PasswdViewHolder extends RecyclerView.ViewHolder {
        private View rootView;
        private TextView tvPasswd;

        public PasswdViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            tvPasswd = rootView.findViewById(R.id.tv_passwd);
        }


        public View getRootView() {
            return rootView;
        }

        public TextView getTvPasswd() {
            return tvPasswd;
        }
    }
}
