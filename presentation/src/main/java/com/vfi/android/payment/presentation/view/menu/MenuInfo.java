package com.vfi.android.payment.presentation.view.menu;

public class MenuInfo {
    private int menuNameStrId;
    private int menuIconResId;
    private int menuId;
    private int parentMenuId;
    private int transType;
    private boolean isMenuGroup;
    private boolean isShowOnMainMenu;

    public MenuInfo(int menuNameStrId,
                    int menuIconResId,
                    int transType,
                    int menuId,
                    int parentMenuId,
                    boolean isMenuGroup,
                    boolean isShowOnMainMenu) {
        this.menuNameStrId = menuNameStrId;
        this.menuIconResId = menuIconResId;
        this.menuId = menuId;
        this.parentMenuId = parentMenuId;
        this.transType = transType;
        this.isMenuGroup = isMenuGroup;
        this.isShowOnMainMenu = isShowOnMainMenu;
    }

    public int getMenuNameStrId() {
        return menuNameStrId;
    }

    public void setMenuNameStrId(int menuNameStrId) {
        this.menuNameStrId = menuNameStrId;
    }

    public int getMenuIconResId() {
        return menuIconResId;
    }

    public void setMenuIconResId(int menuIconResId) {
        this.menuIconResId = menuIconResId;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getParentMenuId() {
        return parentMenuId;
    }

    public void setParentMenuId(int parentMenuId) {
        this.parentMenuId = parentMenuId;
    }

    public boolean isMenuGroup() {
        return isMenuGroup;
    }

    public void setMenuGroup(boolean menuGroup) {
        isMenuGroup = menuGroup;
    }

    public boolean isShowOnMainMenu() {
        return isShowOnMainMenu;
    }

    public void setShowOnMainMenu(boolean showOnMainMenu) {
        isShowOnMainMenu = showOnMainMenu;
    }

    public int getTransType() {
        return transType;
    }
}
