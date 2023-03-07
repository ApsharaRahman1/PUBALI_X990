package com.vfi.android.payment.presentation.models;

public class MenuViewModel {
    private String menuTitle;
    private int menuIconResId;
    private int transType;
    private boolean isMenuGroup;
    private int menuID;

    public MenuViewModel(int transType, String menuTitle, int menuIconResId, boolean isMenuGroup, int menuID) {
        this.menuTitle = menuTitle;
        this.menuIconResId = menuIconResId;
        this.transType = transType;
        this.menuID = menuID;
        this.isMenuGroup = isMenuGroup;
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

    public void setMenuGroup(boolean menuGroup) {
        isMenuGroup = menuGroup;
    }

    public boolean isMenuGroup() {
        return isMenuGroup;
    }

    public int getTransType() {
        return transType;
    }
}
