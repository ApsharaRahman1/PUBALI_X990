package com.vfi.android.data.deviceservice;

import android.os.Bundle;
import android.os.RemoteException;

import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.IPrinterStatus;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.PrinterParamIn;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.smartpos.deviceservice.aidl.PrinterListener;

import io.reactivex.Observable;

/**
 * Created by fusheng.z on 29/12/2017.
 */

public class PrinterObservable {
    private static final String TAG = TAGS.PRINT;
    private PosServiceImpl posService;

    private static class SingletonHolder {
        private static final PrinterObservable INSTANCE = new PrinterObservable();
    }

    private PrinterObservable() {
    }

    public static final PrinterObservable getInstance() {
        return PrinterObservable.SingletonHolder.INSTANCE;
    }

    public PrinterObservable build(PosServiceImpl posService) {
        this.posService = posService;
        return this;
    }

    Observable<Object> getStatus() {
        return Observable.create(e -> {
            // If do print this in emv process will cause transaction failed.
//            int retState = posService.getHandler().getPrinter().getStatus();
//            LogUtil.i(TAG, "printer status: " + retState);
//            if (retState == IPrinterStatus.ERROR_NONE.getId()) {
                e.onNext(true);
                e.onComplete();
//            } else {
//                e.onError(new CommonException(IPrinterStatus.findPrinterStatusById(retState).getExceptionType()));
//            }
        });
    }

    Observable<Object> setGray(final PrinterParamIn param) {
        LogUtil.i("setGray", "param gray " + param.getGray());
        return Observable.create(e -> {
            posService.getHandler().getPrinter().setGray(param.getGray());
            e.onNext(0);
            e.onComplete();
        });
    }

    Observable<Object> addText(final PrinterParamIn param) {
        Bundle bundle = new Bundle();
        bundle.putInt("font", param.getFontSize());
        bundle.putInt("align", param.getAlign());
        bundle.putBoolean("bold", param.isBold());
        bundle.putBoolean("newline", param.isNewline());
        return Observable.create(e -> {
            posService.getHandler().getPrinter().addText(bundle, param.getContent());
            e.onNext(0);
            e.onComplete();
        });
    }

    Observable<Object> addDiyAlignText(final PrinterParamIn param) {
        Bundle bundle = new Bundle();
        bundle.putInt("fontSize", param.getFontSize());
        bundle.putInt("align", param.getAlign());
        bundle.putBoolean("bold", param.isBold());
        bundle.putString("fontStyle",param.getFontStyle());
        return Observable.create(e -> {
            posService.getHandler().getPrinter().addTextInLine(bundle,param.getLeftContent(),param.getMiddleContent(),param.getRightContent() ,param.getMode());
            e.onNext(0);
            e.onComplete();
        });
    }

    Observable<Object> addBarCode(final PrinterParamIn param) {
        Bundle bundle = new Bundle();
        bundle.putInt("align", param.getAlign());
        bundle.putInt("pixelPoint", param.getPixelPoint());
        bundle.putInt("height", param.getHeight());
        return Observable.create(e -> {
            posService.getHandler().getPrinter().addBarCode(bundle, param.getContent());
            e.onNext(0);
            e.onComplete();
        });
    }

    Observable<Object> addQrCode(final PrinterParamIn param) {
        Bundle bundle = new Bundle();
        bundle.putInt("offset", param.getOffset());
        bundle.putInt("expectedHeight", param.getHeight());
        return Observable.create(e -> {
            posService.getHandler().getPrinter().addQrCode(bundle, param.getContent());
            e.onNext(0);
            e.onComplete();
        });
    }

    Observable<Object> addImage(final PrinterParamIn param) {
        Bundle bundle = new Bundle();
        bundle.putInt("offset", param.getOffset());
        bundle.putInt("width", param.getWidth());
        bundle.putInt("height", param.getHeight());
        return Observable.create(e -> {
            posService.getHandler().getPrinter().addImage(bundle, param.getImageData());
            e.onNext(0);
            e.onComplete();
        });
    }

    Observable<Object> feedLine(final PrinterParamIn param) {
        return Observable.create(e -> {
            posService.getHandler().getPrinter().feedLine(param.getLines());
            e.onNext(0);
            e.onComplete();
        });
    }

    Observable<Object> setLineSpace(final PrinterParamIn param) {
        return Observable.create(e -> {
            posService.getHandler().getPrinter().setLineSpace(param.getLineSpace());
            e.onNext(0);
            e.onComplete();
        });
    }

    Observable<Object> startPrint() {
        return Observable.create(e -> posService.getHandler().getPrinter().startPrint(new PrinterListener.Stub() {
            @Override
            public void onFinish() throws RemoteException {
                LogUtil.d(TAG, "Print onFinish");
                e.onNext(PrinterParamIn.PRINT_FINISH);
                e.onComplete();
            }

            @Override
            public void onError(int error) throws RemoteException {
                LogUtil.i(TAG, "onError: error code " + error);
                int errorCode = IPrinterStatus.findPrinterStatusById(error).getErrorCode();
                e.onError(new CommonException(ExceptionType.PRINT_FAILED, errorCode));
            }
        }));
    }

    Observable<Object> startPrintInEmv() {
        return Observable.create(e -> posService.getHandler().getPrinter().startPrintInEmv(new PrinterListener.Stub() {
            @Override
            public void onFinish() throws RemoteException {
                e.onNext(PrinterParamIn.PRINT_FINISH);
                e.onComplete();
            }

            @Override
            public void onError(int error) throws RemoteException {
                LogUtil.i(TAG, "onError: error code " + error);
                int errorCode = IPrinterStatus.findPrinterStatusById(error).getErrorCode();
                e.onError(new CommonException(ExceptionType.PRINT_FAILED, errorCode));
            }
        }));
    }

    Observable<Object> startSaveCachePrint() {
        return Observable.create(e -> posService.getHandler().getPrinter().startPrint(new PrinterListener.Stub() {
            @Override
            public void onFinish() throws RemoteException {
                e.onNext(0);
                e.onComplete();
            }

            @Override
            public void onError(int error) throws RemoteException {
                int errorCode = IPrinterStatus.findPrinterStatusById(error).getErrorCode();
                e.onError(new CommonException(ExceptionType.PRINT_FAILED, errorCode));
            }
        }));
    }

    Observable<Object> addTextInLine(final PrinterParamIn param) {
        Bundle bundle = new Bundle();
        bundle.putInt("fontSize", param.getFontSize());
//        bundle.putInt("align", param.getAlign());
        bundle.putBoolean("bold", param.isBold());
//        bundle.putBoolean("newline", param.isNewline());
        return Observable.create(e -> {
            posService.getHandler().getPrinter().addTextInLine(bundle, param.getLeftContent(), param.getMiddleContent(), param.getRightContent(), param.getMode());
            e.onNext(0);
            e.onComplete();
        });
    }

}
