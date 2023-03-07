package com.vfi.android.payment.presentation.view.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.models.MenuViewModel;
import com.vfi.android.payment.presentation.presenters.MainMenuPresenter;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseMvpActivity;
import com.vfi.android.payment.presentation.view.activities.history.HistoryActivity;
import com.vfi.android.payment.presentation.view.activities.option.OptionActivity;
import com.vfi.android.payment.presentation.view.adapters.MenuListAdapter;
import com.vfi.android.payment.presentation.view.contracts.MainMenuUI;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainMenuActivity extends BaseMvpActivity<MainMenuUI> implements MainMenuUI {
    private final String TAG = TAGS.UILevel;

    @BindView(R.id.tv_sn)
    TextView tv_sn;
    @BindView(R.id.iv_option)
    ImageView iv_option;
    @BindView(R.id.recyclerview_menu_list)
    RecyclerView recyclerview_menu_list;
    @BindView(R.id.Button_Report)
    LinearLayout Report;
    @BindView(R.id.Button_Option)
    LinearLayout Option;

    @BindView(R.id.SliderImage)
    TextView sliderImage;
    @Inject
    MainMenuPresenter mainMenuPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // change status bar icon and font color
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Report.setOnClickListener(v -> {
            AndroidUtil.startActivity(this, HistoryActivity.class);
        });
        initView();


    }

    private void initView() {
        Option.setOnClickListener(v -> {
            AndroidUtil.startActivity(this, OptionActivity.class);
        });
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(mainMenuPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainMenuPresenter.checkStopEmv();
        mainMenuPresenter.updateIsDoingTrans(false);
        mainMenuPresenter.checkBindServiceStatus();
        mainMenuPresenter.checkTmsParameters();
        mainMenuPresenter.checkPowerStatus(this);
        mainMenuPresenter.checkCTLSLedStatus();
    }

    @Override
    public void onBackPressed() {
        // not allow back to previous UI
    }

    @Override
    public void showMainMenu(ArrayList<MenuViewModel> menuModels) {
        Log.d("dataxx", "Call showMainMenu()");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        recyclerview_menu_list.setLayoutManager(gridLayoutManager);

        MenuListAdapter menuListAdapter = new MenuListAdapter(menuModels);
        recyclerview_menu_list.setAdapter(menuListAdapter);

        menuListAdapter.setOnItemClickListener(new MenuListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mainMenuPresenter.startTradeFlow(menuModels.get(position));
            }
        });
    }

    @Override
    public void navigatorToSubMenu() {
        AndroidUtil.startActivity(this, SubMenuActivity.class);
    }

    @Override
    public void startTmsUpdate(String tmsParams) {

    }

    @Override
    public void showTerminalSN(String sn) {
        tv_sn.setText(ResUtil.getString(R.string.tv_hint_sn) + ":" + sn);
    }
}
