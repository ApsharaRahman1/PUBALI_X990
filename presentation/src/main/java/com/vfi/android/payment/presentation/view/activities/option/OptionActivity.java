package com.vfi.android.payment.presentation.view.activities.option;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.OperatorInfo;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.BuildConfig;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.OptionPresenter;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.xmlparse.AppKeysConfigParser;
import com.vfi.android.payment.presentation.utils.xmlparse.CardConfigParser;
import com.vfi.android.payment.presentation.utils.xmlparse.EmvApplicationsConfigParser;
import com.vfi.android.payment.presentation.utils.xmlparse.EmvCTLSApplicationsConfigParser;
import com.vfi.android.payment.presentation.utils.xmlparse.EmvKeysConfigParser;
import com.vfi.android.payment.presentation.utils.xmlparse.HostConfigParser;
import com.vfi.android.payment.presentation.utils.xmlparse.InstallmentPromoParser;
import com.vfi.android.payment.presentation.utils.xmlparse.MerchantConfigParser;
import com.vfi.android.payment.presentation.utils.xmlparse.PrintConfigParser;
import com.vfi.android.payment.presentation.utils.xmlparse.TerminalConfigParser;
import com.vfi.android.payment.presentation.utils.xmlparse.TleConfigParser;
import com.vfi.android.payment.presentation.utils.xmlparse.XmlParser;
import com.vfi.android.payment.presentation.view.activities.base.BaseMvpActivity;
import com.vfi.android.payment.presentation.view.activities.history.HistoryActivity;
import com.vfi.android.payment.presentation.view.activities.setting.SettingActivity;
import com.vfi.android.payment.presentation.view.contracts.OptionUI;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OptionActivity extends BaseMvpActivity<OptionUI> implements OptionUI {
    private AlertDialog alertDialog;

    @Inject
    EmvApplicationsConfigParser emvApplicationsConfigParser;
    @Inject
    EmvKeysConfigParser emvKeysConfigParser;
    @Inject
    HostConfigParser hostConfigParser;
    @Inject
    TerminalConfigParser terminalConfigParser;
    @Inject
    CardConfigParser cardConfigParser;
    @Inject
    MerchantConfigParser merchantConfigParser;
    @Inject
    EmvCTLSApplicationsConfigParser emvCTLSApplicationsConfigParser;
    @Inject
    PrintConfigParser printConfigParser;
    @Inject
    InstallmentPromoParser installmentPromoParser;
    @Inject
    AppKeysConfigParser appKeysConfigParser;
    @Inject
    TleConfigParser tleConfigParser;

    @BindView(R.id.tv_app_soft_version)
    TextView tv_app_soft_version;
    @BindView(R.id.ll_load_configs)
    LinearLayout ll_load_configs;
    @BindView(R.id.tv_top_title)
    TextView tv_top_title;

    XmlParser xmlParser;

    @Inject
    OptionPresenter optionPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ll_load_configs.setVisibility(View.VISIBLE);
        setBackGround(R.color.color_top_background);
        tv_top_title.setText(getString(R.string.menu_title_option));
        initDialog();
        tv_app_soft_version.setText("Version: " + BuildConfig.VERSION_NAME);
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(optionPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    public void initDialog() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_exit_option)
                .setMessage(R.string.dialog_message_exit_option)
                .setNegativeButton(R.string.btn_hint_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.btn_hint_confirm, (dialog, which) -> finish()).create();
    }

    @OnClick({R.id.tv_history, R.id.tv_exit, R.id.tv_setting, R.id.tv_about, R.id.tv_load_configs, R.id.tv_reprint})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_history:
                showCheckPasswordDialog(OperatorInfo.TYPE_SETTING, isCorrectPassword -> {
                    if (isCorrectPassword) {
                        AndroidUtil.startActivity(this, HistoryActivity.class);
                    }
                });
                break;
            case R.id.tv_exit:
                break;
            case R.id.tv_setting:
//                showCheckPasswordDialog(OperatorInfo.TYPE_SETTING, isCorrectPassword -> {
//                    if (isCorrectPassword) {
                        AndroidUtil.startActivity(this, SettingActivity.class);
//                    }
//                });
                break;
            case R.id.tv_about:
                break;
            case R.id.tv_load_configs:
                showCheckPasswordDialog(OperatorInfo.TYPE_SETTING, isCorrectPassword -> {
                    if (isCorrectPassword) {
                        loadConfigs();
                    }
                });
                break;
            case R.id.tv_reprint:
                AndroidUtil.startActivity(this, ReprintActivity.class);
                break;
        }
    }

    private void loadConfigs() {
        try {
            setLoadingDialogStatus(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    xmlParser = new XmlParser();
                    xmlParser.parserConfigFile(emvApplicationsConfigParser);
                    xmlParser.parserConfigFile(emvKeysConfigParser);
                    xmlParser.parserConfigFile(emvCTLSApplicationsConfigParser);
                    xmlParser.parserConfigFile(hostConfigParser);
                    xmlParser.parserConfigFile(terminalConfigParser);
                    xmlParser.parserConfigFile(cardConfigParser);
                    xmlParser.parserConfigFile(merchantConfigParser);
                    xmlParser.parserConfigFile(printConfigParser);
                    xmlParser.parserConfigFile(installmentPromoParser);
                    xmlParser.parserConfigFile(appKeysConfigParser);
                    xmlParser.parserConfigFile(tleConfigParser);
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            showToastMessage(ResUtil.getString(R.string.option_load_configs_finished));
                            setLoadingDialogStatus(false);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            setLoadingDialogStatus(false);
        }
    }

    @Override
    public void showLogoutOptions(List<String> options) {
    }

    @Override
    public void showLoginUI() {
    }

    @Override
    public void navigatorToHistoryActivity() {
    }
}
