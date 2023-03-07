/**
 * Created by yunlongg1 on 05/09/2017.
 */
package com.vfi.android.data.deviceservice;

import android.content.Context;
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
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.smartpos.deviceservice.aidl.IDeviceService;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

@Singleton
public class PosServiceImpl implements IPosService {

    private Context context;

    private IDeviceService handler;

    @Inject
    PosServiceImpl(Context context) {
        this.context = context;
    }

    public IDeviceService getHandler() {
        return this.handler;
    }

    void setHandler(IDeviceService handler) {
        this.handler = handler;
    }

    public Context getContext() {
        return this.context;
    }


    @Override
    public Observable<Boolean> bind() {
        return BindObservable.getInstance().build(this).create();
    }

    @Override
    public Observable<CardInformation> startCheckCard(CheckCardParamIn checkCardParamIn) {
        return bind().flatMap(unused -> CheckCardObservable.getInstance().build(this).start(checkCardParamIn));
    }

    @Override
    public Observable<Object> stopCheckCard() {
        return bind().flatMap(unused -> CheckCardObservable.getInstance().build(this).stop());
    }

    @Override
    public Observable<Object> beep(int times) {
        return bind().flatMap(unused -> BeepObservable.getInstance().build(this).beep(times));
    }

    @Override
    public Observable<Boolean> updateSystemTime(String date, String time) {
        return bind().flatMap(unused -> DeviceInfoObservable.getInstance().build(this).updateSystemTime(date, time));
    }

    @Override
    public Observable<DeviceInformation> getDeviceInfo() {
        return bind().flatMap(unused -> DeviceInfoObservable.getInstance().build(this).getDeviceInformation());
    }

    @Override
    public Observable<Object> importPin(int option, byte[] data) {
        return bind().flatMap(unused -> PinPadObservable.getInstance().build(this).importPin(option, data));
    }

    @Override
    public Observable<Object> importAmount(long amount) {
        return bind().flatMap(unused -> EMVObservable.getInstance().build(this).importAmount(amount));
    }

    @Override
    public Observable<Object> importCardConfirmResult(boolean pass) {
        return bind().flatMap(unused -> EMVObservable.getInstance().build(this).importCardConfirmResult(pass));
    }

    @Override
    public Observable<Object> importCertConfirmResult(int option) {
        return bind().flatMap(unused -> EMVObservable.getInstance().build(this).importCertConfirmResult(option));
    }

    @Override
    public Observable<Object> importAppSelected(int index) {
        return bind().flatMap(unused -> EMVObservable.getInstance().build(this).importAppSelected(index));
    }

    @Override
    public Observable<EmvProcessOnlineResult> inputOnlineResult(EmvOnlineResultParamIn emvOnlineResultParamIn) {
        return bind().flatMap(unused -> EMVObservable.getInstance().build(this).inputOnlineResult(emvOnlineResultParamIn));
    }

    @Override
    public Observable<Boolean> isIcCardPresent() {
        return bind().flatMap(unused -> ICCardReaderObservable.getInstance().build(this).isIcCardPresent());
    }

    @Override
    public Observable<Boolean> openUsbSerial(int bps, int par, int dbs) {
        return bind().flatMap(unused -> UsbSerialObservable.getInstance().build(this).connect(bps, par, dbs));
    }

    @Override
    public Observable<Integer> readUsbSerial(byte[] buffer, int readLen, int timeout) {
        return bind().flatMap(unused -> UsbSerialObservable.getInstance().build(this).read(buffer, readLen, timeout));
    }

    @Override
    public Observable<Integer> writeUsbSerail(byte[] data, int timeout) {
        return bind().flatMap(unused -> UsbSerialObservable.getInstance().build(this).write(data, timeout));
    }

    @Override
    public Observable<Boolean> closeUsbSerial() {
        return bind().flatMap(unused -> UsbSerialObservable.getInstance().build(this).disconnect());
    }

    @Override
    public Observable<Boolean> controlLedStatus(LedParam param) {
        return bind().flatMap(unused -> LedObservable.getInstance().build(this).controlLedLight(param));
    }

    @Override
    public Observable<Map<String, String>> initPinInputCustomView(PinPadInitPinInputCustomViewParamIn param) {
        return bind().flatMap(unused -> PinPadObservable.getInstance().build(this).initPinInputCustomView(param));
    }

    @Override
    public Observable<Object> startPinInputCustomView() {
        return bind().flatMap(unused -> PinPadObservable.getInstance().build(this).startPinInputCustomView());
    }

    @Override
    public Observable<Object> endPinInputCustomView() {
        return bind().flatMap(unused -> PinPadObservable.getInstance().build(this).endPinInputCustomView());
    }

    @Override
    public Observable<byte[]> calcMAC(PinPadCalcMacParamIn param) {
        return bind().flatMap(unused -> PinPadObservable.getInstance().build(this).calcMACWithCalType(param));
    }

    @Override
    public void setPowerKeyStatus(boolean isEnable) {
        DeviceInfoObservable.getInstance().build(this).setPowerKeyStatus(isEnable);
    }

    @Override
    public boolean isKeyExist(int keyType, int keyId) {
        return PinPadObservable.getInstance().build(this).isKeyExist(keyType, keyId);
    }

    @Override
    public Observable<Object> loadTEK(PinPadGeneralParamIn param) {
        return bind().flatMap(unused -> PinPadObservable.getInstance().build(this).loadTEK(param));
    }

    @Override
    public Observable<Boolean> loadEncryptMainKey(PinPadGeneralParamIn param) {
        return bind().flatMap(unused -> PinPadObservable.getInstance().build(this).loadEncryptMainKey(param));
    }

    @Override
    public Observable<Boolean> loadWorkKey(List<PinPadGeneralParamIn> paramIns) {
        return bind().flatMap(unused -> PinPadObservable.getInstance().build(this).loadWorkKey(paramIns));
    }

    @Override
    public Observable<byte[]> encryptTrackData(PinPadEncryptTrackParamIn param) {
        return PinPadObservable.getInstance().build(this).encryptTrackData(param);
    }

    @Override
    public Observable<Boolean> updateAID(AIDParams aidParamsIn) {
        return EMVObservable.getInstance().build(this).updateAID(aidParamsIn);
    }

    @Override
    public Observable<Boolean> updateRID(CAPKParams capkParamsIn) {
        return EMVObservable.getInstance().build(this).updateRID(capkParamsIn);
    }

    @Override
    public Observable<Boolean> updateAPID(APIDParam apidParam) {
        return EMVObservable.getInstance().build(this).updateVisaAPID(apidParam);
    }

    @Override
    public Observable<String> getEMVTagList(EmvTagListParam tagList) {
        return EMVObservable.getInstance().build(this).getEMVTagList(tagList);
    }

    @Override
    public String getEMVTagList(String[] tagList) {
        return EMVObservable.getInstance().build(this).getEMVTagList(tagList);
    }

    @Override
    public Observable<EMVCallback> startEmvFlow(EMVParamIn emvParamIn) {
        return EMVObservable.getInstance().build(this).start(emvParamIn);
    }

    @Override
    public Observable<Object> stopEmvFlow() {
        return EMVObservable.getInstance().build(this).stop();
    }

    @Override
    public Observable<Object> addPrintImage(PrinterParamIn param) {
        return bind().flatMap(unused -> PrinterObservable.getInstance().build(this).addImage(param));
    }

    @Override
    public Observable<Object> addPrintFeedLine(PrinterParamIn param) {
        return bind().flatMap(unused -> PrinterObservable.getInstance().build(this).feedLine(param));
    }

    @Override
    public Observable<Object> addPrintTextInLine(PrinterParamIn param) {
        return bind().flatMap(unused -> PrinterObservable.getInstance().build(this).addTextInLine(param));
    }

    @Override
    public Observable<Object> getPrinterStatus() {
        return bind().flatMap(unused -> PrinterObservable.getInstance().build(this).getStatus());
    }

    @Override
    public Observable<Object> startPrint() {
        return bind().flatMap(unused -> PrinterObservable.getInstance().build(this).startPrint());
    }

    @Override
    public Observable<Object> startPrintInEmv() {
        return bind().flatMap(unused -> PrinterObservable.getInstance().build(this).startPrintInEmv());
    }

    @Override
    public Observable<Object> addPrintText(PrinterParamIn param) {
        return bind().flatMap(unused -> PrinterObservable.getInstance().build(this).addText(param));
    }

    @Override
    public Observable<Object> addDiyAlignText(PrinterParamIn param) {
        return bind().flatMap(unused -> PrinterObservable.getInstance().build(this).addDiyAlignText(param));
    }

    @Override
    public Observable<Object> addPrintBarCode(PrinterParamIn param) {
        return bind().flatMap(unused -> PrinterObservable.getInstance().build(this).addBarCode(param));
    }

    @Override
    public Observable<Object> addPrintQrCode(PrinterParamIn param) {
        return bind().flatMap(unused -> PrinterObservable.getInstance().build(this).addQrCode(param));
    }

    @Override
    public Observable<Object> setGray(final PrinterParamIn param) {
        return bind().flatMap(unused -> PrinterObservable.getInstance().build(this).setGray(param));
    }

    @Override
    public Observable<String[]> getCapks() {
        return bind().flatMap(unused -> EMVObservable.getInstance().build(this).getCapK());
    }

    @Override
    public Observable<Boolean> setSystemFunction(BlockSystemFunctionParamIn paramIn) {
        return bind().flatMap(unused -> DeviceInfoObservable.getInstance().build(this).setSystemFunction(paramIn));
    }

    @Override
    public Observable<byte[]> getCompressedImageData(Bitmap bitmap) {
        return bind().flatMap(unused -> UtilsObservable.getInstance().build(this).getCompressedImageData(bitmap));
    }

    @Override
    public Observable<Boolean> checkIsCardRemoved(int checkTimeSecond) {
        return bind().flatMap(unused -> CheckCardObservable.getInstance().build(this).checkIsCardRemoved(checkTimeSecond));
    }

    @Override
    public Observable<Boolean> simulatorCardRemoved() {
        return bind().flatMap(unused -> CheckCardObservable.getInstance().build(this).simulatorCheckCardRemoved());
    }

    @Override
    public void setEmvTag(String tag) {
        EMVObservable.getInstance().build(this).setEmvTag(tag);
    }

    @Override
    public Observable<Boolean> isRfCardPresent() {
        return bind().flatMap(unused -> RfCardReaderObservable.getInstance().build(this).isRfCardPresent());
    }

    @Override
    public Observable<Boolean> powerOnSmartCardReader() {
        return bind().flatMap(unused -> ICCardReaderObservable.getInstance().build(this).icCardPowerOn());
    }

    @Override
    public Observable<Boolean> powerOffSmartCardReader() {
        return bind().flatMap(unused -> ICCardReaderObservable.getInstance().build(this).icCardPowerOff());
    }

    @Override
    public Observable<byte[]> executeAPDU(byte[] apduCmd) {
        return bind().flatMap(unused -> ICCardReaderObservable.getInstance().build(this).exchangeApdu(apduCmd));
    }

    @Override
    public byte[] executeAPDU(ApduCmd apduCmd) {
        return executeAPDU(apduCmd.getApduCmd()).blockingSingle();
    }
}
