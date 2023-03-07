package com.vfi.android.domain.interfaces.service;

import android.graphics.Bitmap;


import com.vfi.android.domain.entities.businessbeans.CardInformation;
import com.vfi.android.domain.entities.businessbeans.DeviceInformation;
import com.vfi.android.domain.entities.businessbeans.EMVCallback;
import com.vfi.android.domain.entities.databeans.AIDParams;
import com.vfi.android.domain.entities.databeans.APIDParam;
import com.vfi.android.domain.entities.databeans.BlockSystemFunctionParamIn;
import com.vfi.android.domain.entities.databeans.CAPKParams;
import com.vfi.android.domain.entities.databeans.CheckCardParamIn;
import com.vfi.android.domain.entities.databeans.EMVParamIn;
import com.vfi.android.domain.entities.databeans.EmvOnlineResultParamIn;
import com.vfi.android.domain.entities.databeans.EmvProcessOnlineResult;
import com.vfi.android.domain.entities.databeans.EmvTagListParam;
import com.vfi.android.domain.entities.databeans.LedParam;
import com.vfi.android.domain.entities.databeans.PinPadCalcMacParamIn;
import com.vfi.android.domain.entities.databeans.PinPadEncryptTrackParamIn;
import com.vfi.android.domain.entities.databeans.PinPadGeneralParamIn;
import com.vfi.android.domain.entities.databeans.PinPadInitPinInputCustomViewParamIn;
import com.vfi.android.domain.entities.databeans.PrinterParamIn;
import com.vfi.android.domain.interactor.transaction.tle.apdu.ApduCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.ApduResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public interface IPosService {
    Observable<Boolean> bind();

    // check card related interfaces
    Observable<CardInformation> startCheckCard(CheckCardParamIn checkCardParamIn);

    Observable<Object> stopCheckCard();

    // beep interfaces
    Observable<Object> beep(int times);

    // device info interfaces
    Observable<Boolean> updateSystemTime(String date, String time);

    Observable<DeviceInformation> getDeviceInfo();

    // pinpad interfaces
    Observable<Map<String, String>> initPinInputCustomView(PinPadInitPinInputCustomViewParamIn param);

    Observable<Object> startPinInputCustomView();

    Observable<Object> endPinInputCustomView();

    Observable<byte[]> calcMAC(PinPadCalcMacParamIn param);

    void setPowerKeyStatus(boolean isEnable);

    // key related interfaces
    boolean isKeyExist(int keyType, int keyId);

    Observable<Object> loadTEK(PinPadGeneralParamIn param);

    Observable<Boolean> loadEncryptMainKey(PinPadGeneralParamIn param);

    Observable<Boolean> loadWorkKey(List<PinPadGeneralParamIn> paramIns);

    // encrypt decrypt interfaces
    Observable<byte[]> encryptTrackData(PinPadEncryptTrackParamIn param);

    // emv parameters interfaces
    Observable<Boolean> updateAID(AIDParams aidParamsIn);

    Observable<Boolean> updateRID(CAPKParams capkParamsIn);

    Observable<Boolean> updateAPID(APIDParam apidParam);

    // emv flow related interfaces
    Observable<String> getEMVTagList(EmvTagListParam tagList);

    String getEMVTagList(String[] tagList);

    Observable<EMVCallback> startEmvFlow(EMVParamIn emvParamIn);

    Observable<Object> stopEmvFlow();

    Observable<Object> importPin(int option, byte[] data);

    Observable<Object> importAmount(long amount);

    Observable<Object> importCardConfirmResult(boolean pass);

    Observable<Object> importCertConfirmResult(int option);

    Observable<Object> importAppSelected(int index);

    Observable<EmvProcessOnlineResult> inputOnlineResult(EmvOnlineResultParamIn pbocOnlineResultParamIn);

    Observable<Object> startPrint();

    Observable<Object> startPrintInEmv();

    Observable<Object> addPrintText(PrinterParamIn param);

    Observable<Object> addDiyAlignText(PrinterParamIn param);

    Observable<Object> addPrintBarCode(PrinterParamIn param);

    Observable<Object> addPrintQrCode(PrinterParamIn param);

    Observable<Object> addPrintImage(PrinterParamIn param);

    Observable<Object> addPrintFeedLine(PrinterParamIn param);

    Observable<Object> addPrintTextInLine(PrinterParamIn param);

    Observable<Object> getPrinterStatus();

    Observable<Object> setGray(final PrinterParamIn param);

    // check ic card is remove or not
    Observable<Boolean> isIcCardPresent();

    // usb serial interfaces
    Observable<Boolean> openUsbSerial(int bps, int par, int dbs);

    Observable<Integer> readUsbSerial(byte[] buffer, int readLen, int timeout);

    Observable<Integer> writeUsbSerail(byte[] data, int timeout);

    Observable<Boolean> closeUsbSerial();

    // led interfaces
    Observable<Boolean> controlLedStatus(LedParam param);

    Observable<String[]> getCapks();

    Observable<Boolean> setSystemFunction(BlockSystemFunctionParamIn paramIn);

    Observable<byte[]> getCompressedImageData(Bitmap bitmap);

    Observable<Boolean> checkIsCardRemoved(int checkTimeSecond);

    Observable<Boolean> simulatorCardRemoved();

    void setEmvTag(String tagValue);

    Observable<Boolean> isRfCardPresent();

    Observable<Boolean> powerOnSmartCardReader();

    Observable<Boolean> powerOffSmartCardReader();

    Observable<byte[]> executeAPDU(byte[] apduCmd);

    byte[] executeAPDU(ApduCmd apduCmd);
}
