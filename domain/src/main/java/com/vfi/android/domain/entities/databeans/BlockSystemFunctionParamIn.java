package com.vfi.android.domain.entities.databeans;

/**
 * Created by fusheng.z on 2018/3/16.
 */

public class BlockSystemFunctionParamIn {
    private boolean isHomeEnable;
    private boolean isStatusBarEnable;

    public boolean isHomeEnable() {
        return isHomeEnable;
    }

    public void setHomeEnable(boolean homeEnable) {
        isHomeEnable = homeEnable;
    }

    public boolean isStatusBarEnable() {
        return isStatusBarEnable;
    }

    public void setStatusBarEnable(boolean statusBarEnable) {
        isStatusBarEnable = statusBarEnable;
    }
}
