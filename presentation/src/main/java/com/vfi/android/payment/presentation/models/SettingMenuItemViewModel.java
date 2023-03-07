package com.vfi.android.payment.presentation.models;

public class SettingMenuItemViewModel {
    private String menuTitle;
    private int menuIconResId;
    private int menuID;

    public SettingMenuItemViewModel(String menuTitle, int menuIconResId, int menuID) {
        this.menuTitle = menuTitle;
        this.menuIconResId = menuIconResId;
        this.menuID = menuID;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public int getMenuIconResId() {
        return menuIconResId;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public void setMenuIconResId(int menuIconResId) {
        this.menuIconResId = menuIconResId;
    }

    public int getMenuID() {
        return menuID;
    }

    public void setMenuID(int menuID) {
        this.menuID = menuID;
    }
}
