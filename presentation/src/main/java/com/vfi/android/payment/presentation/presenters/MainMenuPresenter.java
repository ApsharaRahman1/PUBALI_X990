package com.vfi.android.payment.presentation.presenters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.LogPrinter;
import android.util.Printer;
import android.view.View;
import android.widget.Toast;

import com.vfi.android.data.deviceservice.PinPadObservable;
import com.vfi.android.data.deviceservice.PosServiceImpl;
import com.vfi.android.domain.entities.businessbeans.DeviceInformation;
import com.vfi.android.domain.entities.businessbeans.SwitchParameter;
import com.vfi.android.domain.entities.consts.CTLSLedStatus;
import com.vfi.android.domain.entities.consts.InterceptorResult;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.PrintTask;
import com.vfi.android.domain.interactor.deviceservice.UseCaseBindDeviceService;
import com.vfi.android.domain.interactor.deviceservice.UseCaseEmvStop;
import com.vfi.android.domain.interactor.deviceservice.UseCaseSetCTLSLedStatus;
import com.vfi.android.domain.interactor.print.PrintTaskManager;
import com.vfi.android.domain.interactor.print.UseCaseStartPrintSlip;
import com.vfi.android.domain.interactor.repository.UseCaseGetDeviceInfo;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalCfg;
import com.vfi.android.domain.interactor.repository.UseCaseGetTransSwitchMap;
import com.vfi.android.domain.interactor.repository.UseCaseGetTransSwitchParameter;
import com.vfi.android.domain.interactor.repository.UseCaseSaveMenuId;
import com.vfi.android.domain.interactor.repository.UseCaseSaveMenuTitle;
import com.vfi.android.domain.interactor.transaction.UseCaseCheckAndInitTrans;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.MenuInfoToViewMapper;
import com.vfi.android.payment.presentation.mappers.TransInterceptorResultMapper;
import com.vfi.android.payment.presentation.mappers.MenuTitleMapper;
import com.vfi.android.payment.presentation.models.MenuViewModel;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.transflows.InstallmentUIFlow2;
import com.vfi.android.payment.presentation.transflows.OfflineUIFlow2;
import com.vfi.android.payment.presentation.transflows.PreAuthCompUIFlow2;
import com.vfi.android.payment.presentation.transflows.PreAuthUIFlow2;
import com.vfi.android.payment.presentation.transflows.SaleUIFlow2;
import com.vfi.android.payment.presentation.transflows.SettlementUIFlow;
import com.vfi.android.payment.presentation.transflows.TipAdjustUIFlow;
import com.vfi.android.payment.presentation.transflows.VoidUIFlow;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TransUtil;
import com.vfi.android.payment.presentation.view.activities.history.HistoryActivity;
import com.vfi.android.payment.presentation.view.contracts.MainMenuUI;
import com.vfi.android.payment.presentation.view.menu.MenuInfo;
import com.vfi.android.payment.presentation.view.menu.MenuManager;
import com.vfi.smartpos.deviceservice.aidl.IDeviceService;
import com.vfi.smartpos.deviceservice.aidl.IPrinter;
import com.vfi.smartpos.deviceservice.aidl.PrinterListener;
import com.vfi.smartpos.deviceservice.aidl.QrCodeContent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

public class MainMenuPresenter extends BasePresenter<MainMenuUI> {
    private final String TAG = TAGS.UILevel;
    String response_logon;
    private final UseCaseBindDeviceService useCaseBindDeviceService;
    private final UseCaseCheckAndInitTrans useCaseCheckAndInitTrans;
    private final UseCaseGetTerminalCfg useCaseGetTerminalCfg;
    private final UseCaseSaveMenuId useCaseSaveMenuId;
    private final UseCaseSaveMenuTitle useCaseSaveMenuTitle;
    private final UseCaseGetTransSwitchMap useCaseGetTransSwitchMap;
    private final UseCaseSetCTLSLedStatus useCaseSetCTLSLedStatus;
    private final UseCaseEmvStop useCaseEmvStop;
    private final MenuInfoToViewMapper menuInfoToViewMapper;
    private final MenuManager menuManager;
    private final UINavigator uiNavigator;
    private final UseCaseGetTransSwitchParameter useCaseGetTransSwitchParameter;
    private final DeviceInformation deviceInformation;
    IDeviceService idevice;
    IPrinter printer;

    @Inject
    public MainMenuPresenter(UseCaseBindDeviceService useCaseBindDeviceService,
                             MenuManager menuManager,
                             UINavigator uiNavigator,
                             UseCaseCheckAndInitTrans useCaseCheckAndInitTrans,
                             UseCaseGetTerminalCfg useCaseGetTerminalCfg,
                             UseCaseSaveMenuId useCaseSaveMenuId,
                             UseCaseSaveMenuTitle useCaseSaveMenuTitle,
                             UseCaseGetTransSwitchMap useCaseGetTransSwitchMap,
                             UseCaseSetCTLSLedStatus useCaseSetCTLSLedStatus,
                             UseCaseGetTransSwitchParameter useCaseGetTransSwitchParameter,
                             UseCaseEmvStop useCaseEmvStop,
                             UseCaseGetDeviceInfo useCaseGetDeviceInfo,
                             MenuInfoToViewMapper menuInfoToViewMapper) {
        this.useCaseBindDeviceService = useCaseBindDeviceService;
        this.menuInfoToViewMapper = menuInfoToViewMapper;
        this.menuManager = menuManager;
        this.useCaseCheckAndInitTrans = useCaseCheckAndInitTrans;
        this.uiNavigator = uiNavigator;
        this.useCaseGetTerminalCfg = useCaseGetTerminalCfg;
        this.useCaseSaveMenuId = useCaseSaveMenuId;
        this.useCaseSaveMenuTitle = useCaseSaveMenuTitle;
        this.useCaseGetTransSwitchMap = useCaseGetTransSwitchMap;
        this.useCaseSetCTLSLedStatus = useCaseSetCTLSLedStatus;
        this.useCaseEmvStop = useCaseEmvStop;
        this.useCaseGetTransSwitchParameter = useCaseGetTransSwitchParameter;
        this.deviceInformation = useCaseGetDeviceInfo.execute(null);
    }

    @Override
    protected void onFirstUIAttachment() {
        loadMenu();
        doUICmd_showTerminalSN(deviceInformation.getSerialNo());
    }

    private void loadMenu() {
        final List<MenuInfo> menuInfos = menuManager.getHomeMenuList(null);
        List<Integer> transTypeList = new ArrayList<>();
        for (MenuInfo menuInfo : menuInfos) {
            transTypeList.add(menuInfo.getTransType());
        }

        Disposable disposable = useCaseGetTransSwitchMap.asyncExecute(transTypeList).subscribe(transSwitchMap -> {
            doUICmd_showMainMenu(menuInfoToViewMapper.toViewModel(filterDisableTransMenu(menuInfos, transSwitchMap)));
        }, throwable -> {
            doUICmd_showMainMenu(menuInfoToViewMapper.toViewModel(menuInfos));
        });
    }

    private List<MenuInfo> filterDisableTransMenu(List<MenuInfo> menuInfos, Map<Integer, SwitchParameter> map) {
        if (menuInfos != null) {
            Iterator<MenuInfo> iterator = menuInfos.iterator();
            while (iterator.hasNext()) {
                MenuInfo menuInfo = iterator.next();
                SwitchParameter switchParameter = map.get(menuInfo.getTransType());
                if (switchParameter != null && !switchParameter.isEnableTrans()) {
                    iterator.remove();
                }
            }
        }
        return menuInfos;
    }

    private boolean doTransCheckAndInit(int transType) {
        boolean bRet = true;

        try {
            int ret = useCaseCheckAndInitTrans.execute(transType);
            if (ret != InterceptorResult.NORMAL) {
                doUICmd_showToastMessage(TransInterceptorResultMapper.toString(ret));
                bRet = false;
            }
        } catch (Exception e) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_init_trans_exception));
            e.printStackTrace();
            bRet = false;
        }

        return bRet;
    }

    public void startTradeFlow(MenuViewModel menuViewModel) {
        if (menuViewModel.isMenuGroup()) {
            // Menu item have sub items
            useCaseSaveMenuTitle.execute(MenuTitleMapper.toString(menuViewModel.getTransType()));
            useCaseSaveMenuId.execute(menuViewModel.getMenuID());
            doUICmd_navigatorToSubMenu();
        } else {
            int selectTransType = menuViewModel.getTransType();
            LogUtil.d(TAG, "selectTransType=" + selectTransType);

            if (doTransCheckAndInit(selectTransType)) {
                useCaseSaveMenuTitle.execute(MenuTitleMapper.toString(menuViewModel.getTransType()));
                TransUtil.doUINavigatorParamInit(uiNavigator, selectTransType, useCaseGetTerminalCfg, useCaseGetTransSwitchParameter);

                if (selectTransType == TransType.SALE) {
                    Log.d("dataxx", "startTradeFlow: SALE");
                    uiNavigator.setTransUiFlow(new SaleUIFlow2());
                } else if (selectTransType == TransType.VOID) {
                    uiNavigator.setTransUiFlow(new VoidUIFlow());
                } else if (selectTransType == TransType.SETTLEMENT) {
                    uiNavigator.setTransUiFlow(new SettlementUIFlow());
                } else if (selectTransType == TransType.PREAUTH) {
                    uiNavigator.setTransUiFlow(new PreAuthUIFlow2());
                } else if (selectTransType == TransType.PREAUTH_COMP) {
                    uiNavigator.setTransUiFlow(new PreAuthCompUIFlow2());
                } else if (selectTransType == TransType.OFFLINE) {
                    uiNavigator.setTransUiFlow(new OfflineUIFlow2());
                } else if (selectTransType == TransType.TIP_ADJUST) {
                    uiNavigator.setTransUiFlow(new TipAdjustUIFlow());
                } else if (selectTransType == TransType.INSTALLMENT) {
                    uiNavigator.setTransUiFlow(new InstallmentUIFlow2());
                } else if (selectTransType == TransType.LOGON) {

                    SendfeedbackJob job = new SendfeedbackJob();
                    job.execute("x", "a");


                } else if (selectTransType == TransType.REPORT) {

                    Context Context = null;
                    AndroidUtil.startActivity(Context, HistoryActivity.class);


                } else {
                    uiNavigator.setTransUiFlow(null);
                }

                doUICmd_navigatorToNext();
            }
        }
    }

    private class SendfeedbackJob extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] params) {
            // do above Server call here
            try {
                // Create a new socket
                String length = "0036600002000008002020010000C00004920000000019000232303030303030323437323930353030303030313030300006303030303031";

                Socket socket = new Socket();

                socket.connect(new InetSocketAddress("172.31.1.42", 5200), 30 * 1000);
                if (socket.isConnected()) {
                    Log.d("response", "connected");
                }
                // Get the input and output streams
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                // Send data to the server
                Log.d("response", "before send");
                byte[] data = hexStringToByteArray(length);
                outputStream.write(data);
                Log.d("response", "after send");
                // Receive data from the server
                Log.d("response", hexStringToByteArray(length).toString());
                Log.d("response", "before recv");
                byte[] buffer = new byte[58];
                int bytesRead = inputStream.read(buffer);
                Log.d("response", String.valueOf(bytesRead));

                String gt = byte2HexStr(buffer);
                Log.d("response1", gt);
                Log.d("response2", gt.substring((gt.length() - 32)));
                String response = new String(buffer, 0, bytesRead);
                Log.d("response", response + "n");

                response_logon = gt.substring(gt.length() - 32);
                // check code

                inputStream.close();
                outputStream.close();
                // Close the connection
                socket.close();

                PinPadObservable pinPadObservable = new PinPadObservable();
                pinPadObservable.ishtiakLoad(hexStringToByteArray(response_logon), hexStringToByteArray("response_logon"));

            } catch (Exception e) {
                Log.d("ishtiak", e.toString());
            }


            return "some message";
        }

        @Override
        protected void onPostExecute(String message) {
            //process message
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byte2HexStr(byte[] data) {
        if (data == null || data.length <= 0) {
            return null;
        }
        try {
            String stmp = "";
            StringBuilder sb = new StringBuilder("");
            for (int n = 0; n < data.length; n++) {
                stmp = Integer.toHexString(data[n] & 0xFF);
                sb.append(stmp.length() == 1 ? "0" + stmp : stmp);
            }
            return sb.toString().toUpperCase().trim();
        } catch (Exception e) {
        }
        return null;
    }

    public void updateIsDoingTrans(boolean isDoingTrans) {

    }

    public void checkBindServiceStatus() {
        useCaseBindDeviceService.asyncExecuteWithoutResult(null);
    }

    public void checkPowerStatus(Context context) {

    }

    public void checkTmsParameters() {

    }

    public void checkCTLSLedStatus() {
        useCaseSetCTLSLedStatus.asyncExecuteWithoutResult(CTLSLedStatus.CLEAR_ALL_LEDS);
    }

    public void checkStopEmv() {
        useCaseEmvStop.asyncExecuteWithoutResult(null);
    }

    private void doUICmd_showMainMenu(ArrayList<MenuViewModel> menuModels) {
        execute(ui -> ui.showMainMenu(menuModels));
    }

    private void doUICmd_navigatorToSubMenu() {
        execute(ui -> ui.navigatorToSubMenu());
    }

    private void doUICmd_showTerminalSN(String sn) {
        execute(ui -> ui.showTerminalSN(sn));
    }
}
