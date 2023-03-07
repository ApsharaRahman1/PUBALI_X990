package com.vfi.android.payment.presentation.mappers;


import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.models.MenuViewModel;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.menu.MenuInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class MenuInfoToViewMapper {
    private final String TAG = TAGS.UILevel;

    @Inject
    public MenuInfoToViewMapper() {

    }

    public ArrayList<MenuViewModel> toViewModel(List<MenuInfo> menuInfos) {
        ArrayList<MenuViewModel> menuViewModelArrayList = new ArrayList<>();

        if (menuInfos == null) {
            return menuViewModelArrayList;
        }

        Iterator<MenuInfo> iterator = menuInfos.iterator();
        while (iterator.hasNext()) {
            menuViewModelArrayList.add(toViewModel(iterator.next()));
        }

        return menuViewModelArrayList;
    }

    public MenuViewModel toViewModel(MenuInfo menuInfo) {
        int transType = menuInfo.getTransType();
        String menuTitle = ResUtil.getString(menuInfo.getMenuNameStrId());
        int menuIconResId = menuInfo.getMenuIconResId();
        boolean isGroupMenu = menuInfo.isMenuGroup();
        int menuId = menuInfo.getMenuId();
        LogUtil.d(TAG, "Type=" + transType + " MenuName=" + menuTitle + " isMenuGroup=" + isGroupMenu);
        return new MenuViewModel(transType, menuTitle, menuIconResId, isGroupMenu, menuId);
    }
}
