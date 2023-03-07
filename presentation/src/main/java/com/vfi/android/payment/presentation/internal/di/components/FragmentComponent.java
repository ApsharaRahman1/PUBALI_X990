package com.vfi.android.payment.presentation.internal.di.components;


import com.vfi.android.payment.presentation.internal.di.PerActivity;
import com.vfi.android.payment.presentation.view.fragments.HistoryListFragment;

import dagger.Component;

/**
 * A base component upon which fragment's components may depend.
 * Activity-level components should extend this component.
 * <p>
 * Subtypes of ActivityComponent should be decorated with annotation:
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class)
public interface FragmentComponent {

    void inject(HistoryListFragment fragment);
}
