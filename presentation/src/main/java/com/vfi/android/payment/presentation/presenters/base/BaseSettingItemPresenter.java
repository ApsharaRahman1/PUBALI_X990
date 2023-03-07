package com.vfi.android.payment.presentation.presenters.base;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.models.ParamItemModel;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.SettingItemUI;
import com.vfi.android.payment.presentation.models.SettingItemViewModel;

import java.util.List;
import java.util.Stack;

public abstract class BaseSettingItemPresenter<T extends SettingItemUI> extends BasePresenter<T> {
    protected final String TAG = TAGS.Setting;

    private Stack<List<SettingItemViewModel>> stack = new Stack<>();
    private Stack<String> titleStack = new Stack<>();
    private String currenctSelectItem;

    @Override
    protected void onFirstUIAttachment() {
        super.onFirstUIAttachment();
        doUICmd_showSettingItem(initSettingItems());
    }

    public abstract List<SettingItemViewModel> initSettingItems();
    public abstract void onItemSelected(SettingItemViewModel settingItemViewModel);
    public abstract void onItemValueChanged(SettingItemViewModel settingItemViewModel);
    public abstract void processUnSavedItems(List<SettingItemViewModel> unSavedItemList);

    public void doUICmd_showSettingItem(List<SettingItemViewModel> itemViewModels) {
        if (currenctSelectItem != null) {
            doUICmd_showTitle(currenctSelectItem);
        }
        putToStack(itemViewModels);
        execute(ui -> ui.showSettingItems(itemViewModels));
    }

    public void doUICmd_finishUI() {
        execute(ui -> ui.finishUI());
    }

    public void doUICmd_cancelSettingUITimer() {
        execute(ui -> ui.cancelSettingUITimer());
    }

    public void doUICmd_startSettingUITimer() {
        execute(ui -> ui.startSettingUITimer());
    }

    public void onSelectItem(SettingItemViewModel itemViewModel){
        currenctSelectItem = itemViewModel.getLabel();
        if (currenctSelectItem == null || currenctSelectItem.length() == 0) {
            currenctSelectItem = getPreviousTitle();
        }
        onItemSelected(itemViewModel);
    }

    private void putToStack(List<SettingItemViewModel> itemViewModels) {
        LogUtil.d(TAG, "Put a list" + stack.size());
        stack.push(itemViewModels);
    }

    public List<SettingItemViewModel> popStack() {
        LogUtil.d(TAG, "Put a list" + stack.size());

        if (stack.isEmpty()) {
            return null;
        }

        List<SettingItemViewModel> list = stack.peek();
        stack.pop();

        return list;
    }

    public void saveConfigs(List<SettingItemViewModel> unSavedCItemModels){
        processUnSavedItems(unSavedCItemModels);
        doUICmd_showToastMessage(ResUtil.getString(R.string.setting_save_success));
    }

    public void onBackPressEvent(){
        popStack();

        LogUtil.d(TAG, "onBackPressEvent" + stack.size());
        if (stack.isEmpty()) {
            doUICmd_finishUI();
        } else {
            String previousTitle = popTitleStack();
            execute(ui -> ui.showSettingTitle(previousTitle));

            List<SettingItemViewModel> list = stack.peek();
            execute(ui -> ui.showSettingItems(list));
        }
    }

    private void pushToTitleStack(String title) {
        LogUtil.d(TAG, "Push title " + title);
        titleStack.push(title);
    }

    private String popTitleStack() {
        if (titleStack.isEmpty()) {
            return "";
        }

        titleStack.pop();
        String title = ResUtil.getString(R.string.option_setting);
        if (!titleStack.isEmpty()) {
            title = titleStack.peek();
        }

        LogUtil.d(TAG, "Pop title " + title);
        return title;
    }

    private String getPreviousTitle() {
        if (titleStack.isEmpty()) {
            return ResUtil.getString(R.string.option_setting);
        }

        String previousTitle = titleStack.peek();
        LogUtil.d(TAG, "previousTitle=" + previousTitle);

        return previousTitle;
    }

    public void doUICmd_showTitle(String title) {
        pushToTitleStack(title);
        execute(ui -> ui.showSettingTitle(title));
    }

    public SettingItemViewModel create(ParamItemModel paramItemModel) {
        return new SettingItemViewModel(paramItemModel);
    }
}
