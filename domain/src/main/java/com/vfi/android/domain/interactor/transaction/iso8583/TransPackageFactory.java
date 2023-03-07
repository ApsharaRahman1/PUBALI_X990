package com.vfi.android.domain.interactor.transaction.iso8583;

import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.interactor.transaction.iso8583.base.ITransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.TransPackageData;
import com.vfi.android.domain.interactor.transaction.iso8583.tle.DownloadRKIKeyPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.tle.DownloadTLEKeyPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.trans.BatchUploadInstallmentPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.trans.BatchUploadPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.trans.InstallmentSalePackage;
import com.vfi.android.domain.interactor.transaction.iso8583.trans.OfflineUploadPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.trans.PreAuthCompPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.trans.PreAuthPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.trans.ReversalInstallmentPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.trans.ReversalPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.trans.SalePackage;
import com.vfi.android.domain.interactor.transaction.iso8583.trans.SettlementPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.trans.TCUploadPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.trans.TipAdjustUploadPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.trans.VoidInstallmentPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.trans.VoidPackage;

public class TransPackageFactory {
    public static ITransPackage getTransPackage(int transType, TransPackageData transPackageData) {
        ITransPackage transPackage = null;

        switch (transType) {
            case TransType.SALE:
                transPackage = new SalePackage(transPackageData);
                break;
            case TransType.VOID:
                if (transPackageData.getRecordInfo().getVoidOrgTransType() == TransType.INSTALLMENT) {
                    transPackage = new VoidInstallmentPackage(transPackageData);
                } else {
                    transPackage = new VoidPackage(transPackageData);
                }
                break;
            case TransType.REVERSAL:
                if (transPackageData.getRecordInfo().getReversalOrgTransType() == TransType.INSTALLMENT) {
                    transPackage = new ReversalInstallmentPackage(transPackageData);
                } else {
                    transPackage = new ReversalPackage(transPackageData);
                }
                break;
            case TransType.SETTLEMENT:
            case TransType.SETTLEMENT_TAILER:
                transPackage = new SettlementPackage(transPackageData);
                break;
            case TransType.BATCH_UPLOAD:
                if (transPackageData.getRecordInfo().getTransType() == TransType.INSTALLMENT) {
                    transPackage = new BatchUploadInstallmentPackage(transPackageData);
                } else {
                    transPackage = new BatchUploadPackage(transPackageData);
                }
                break;
            case TransType.PREAUTH:
                transPackage = new PreAuthPackage(transPackageData);
                break;
            case TransType.PREAUTH_COMP:
                transPackage = new PreAuthCompPackage(transPackageData);
                break;
            case TransType.OFFLINE_UPLOAD:
                transPackage = new OfflineUploadPackage(transPackageData);
                break;
            case TransType.TC_UPLOAD:
                transPackage = new TCUploadPackage(transPackageData);
                break;
            case TransType.TIP_ADJUST_UPLOAD:
                transPackage = new TipAdjustUploadPackage(transPackageData);
                break;
            case TransType.INSTALLMENT:
                transPackage = new InstallmentSalePackage(transPackageData);
                break;
            case TransType.RKI_KEY_DOWNLOAD:
                transPackage = new DownloadRKIKeyPackage(transPackageData);
                break;
            case TransType.TLE_KEY_DOWNLOAD:
                transPackage = new DownloadTLEKeyPackage(transPackageData);
                break;
        }

        return transPackage;
    }
}
