/**
 * Created by Frank on 04/09/2017.
 */
package com.vfi.android.payment.presentation.presenters.base;


import com.vfi.android.payment.presentation.view.contracts.base.UI;

/**
 * Interface representing a Presenter in a model view presenter (MVP) pattern.
 */
public interface Presenter<T extends UI> {
    /**
     * Presenter attach to activity/fragment
     * @param ui - UI interface which presenter can control UI display.
     */
    void attachUI(T ui);

    /**
     * Presenter detach from activity/fragemnt
     */
    void detachUI();
}
