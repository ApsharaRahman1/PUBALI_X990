package com.vfi.android.domain.entities.databeans;

public class MenuOrder {
    private int menuId;
    private int orderIndex;

    public MenuOrder(int menuId, int orderIndex) {
        this.menuId = menuId;
        this.orderIndex = orderIndex;
    }
}
