package com.vfi.android.domain.interactor.transaction;

import com.vfi.android.domain.comm.CommInterfaceFactory;
import com.vfi.android.domain.entities.comm.CommErrorType;
import com.vfi.android.domain.entities.comm.CommParam;
import com.vfi.android.domain.entities.comm.CommStatus;
import com.vfi.android.domain.entities.comm.CommType;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interactor.transaction.base.ITransaction;
import com.vfi.android.domain.interfaces.comm.IComm;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class UseCaseStartTransCommunication extends UseCase<Integer, ITransaction> {
    private final String TAG = TAGS.Transaction;
    private final CommInterfaceFactory commInterfaceFactory;
    private final IRepository iRepository;

    private IComm commInterface;
    private ITransaction transInterface;

    @Inject
    public UseCaseStartTransCommunication(ThreadExecutor threadExecutor,
                                          PostExecutionThread postExecutionThread,
                                          CommInterfaceFactory commInterfaceFactory,
                                          IRepository iRepository
                              ) {
        super(threadExecutor, postExecutionThread);
        this.commInterfaceFactory = commInterfaceFactory;
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Integer> buildUseCaseObservable(ITransaction iTransaction) {
        transInterface = iTransaction;
        LogUtil.d(TAG, "UseCaseStartTransCommunication");

        return Observable.create(emitter -> {
            CommParam commParam = iTransaction.getCommParam();
            if (commParam == null) {
                LogUtil.e(TAG, "commParam is null");
                doErrorProcess(emitter, new CommonException(ExceptionType.COMM_EXCEPTION, CommErrorType.COMM_PARAM_ERROR));
                emitter.onComplete();
                return;
            }

            try {
                if (commParam.getCommType() == CommType.SIMULATE_COMM) {
                    transInterface.transStatusHook(CommStatus.CONNECTING);
                    transInterface.transStatusHook(CommStatus.CONNECTED);
                    transInterface.transStatusHook(CommStatus.SENDING);
                    transInterface.transStatusHook(CommStatus.SENDED);
                    transInterface.transStatusHook(CommStatus.RECEIVING);
                    transInterface.transStatusHook(CommStatus.RECEIVED);
                    transInterface.processTransResult(null);
                    transInterface.transStatusHook(CommStatus.CLOSEING);
                    transInterface.transStatusHook(CommStatus.CLOSED);
                } else {
                    commInterface = commInterfaceFactory.createCommInterface(commParam.getCommType());
                    commInterface.initCommParam(commParam);
                    connect2Host(emitter);
                    sendTransData2Host(emitter);
                    byte[] transResponse = recvTransResponse(emitter);
                    LogUtil.d(TAG, "Begin call processTransResult");
                    transInterface.processTransResult(transResponse);
                }
            } catch (Exception e) {
                doErrorProcess(emitter, e);
            } finally {
                if (commParam.getCommType() != CommType.SIMULATE_COMM) {
                    disconnectHost(emitter);
                }
                emitter.onComplete();
            }
        });
    }

    private void connect2Host(ObservableEmitter<Integer> emitter) throws CommonException {
        LogUtil.d(TAG, "connect2Host");
        // prepare connect host
        transInterface.transStatusHook(CommStatus.CONNECTING);
        emitter.onNext(CommStatus.CONNECTING);

        // start connect host
        commInterface.connect();

        transInterface.transStatusHook(CommStatus.CONNECTED);
        emitter.onNext(CommStatus.CONNECTED);
    }


    private void sendTransData2Host(ObservableEmitter<Integer> emitter) throws CommonException {
        LogUtil.d(TAG, "sendTransData2Host");
        /**
         * Common ISO MSG format is MESSAGE_LENGTH(2byte) + TPDU_HEADER + PAYLOAD
         * transInterface.getTransData only return TPDU_HEADER + PAYLOAD
         */
        byte[] transData = transInterface.getTransData();
        byte[] sendData = new byte[2 + transData.length];
        sendData[0] = (byte) ((transData.length >> 8) & 0xFF);
        sendData[1] = (byte) (transData.length & 0xFF);
        System.arraycopy(transData, 0, sendData, 2, transData.length);

        transInterface.transStatusHook(CommStatus.SENDING);
        emitter.onNext(CommStatus.SENDING);

        LogUtil.d(TAG, "sendData=[" + StringUtil.byte2HexStr(sendData) + "]");
        commInterface.send(sendData);

        transInterface.transStatusHook(CommStatus.SENDED);
        emitter.onNext(CommStatus.SENDED);
    }

    private byte[] recvTransResponse(ObservableEmitter<Integer> emitter) throws CommonException {
        LogUtil.d(TAG, "recvTransResponse");
        transInterface.transStatusHook(CommStatus.RECEIVING);
        emitter.onNext(CommStatus.RECEIVING);

        /**
         * Common ISO MSG format is MESSAGE_LENGTH(2byte) + TPDU_HEADER + PAYLOAD
         */
        byte[] lenBuffer = new byte[2];
        int receiveTimeout = iRepository.getTerminalCfg().getReceiveTimeout();
        if (receiveTimeout <= 0) {
            receiveTimeout = 60;
        }
        commInterface.receive(lenBuffer, 2, receiveTimeout * 1000);
        int msgLength = (lenBuffer[1] & 0xFF) + ((lenBuffer[0] & 0xFF) << 8);
        LogUtil.d(TAG, "Recv msg len will be " + msgLength);

        byte[] recvData = new byte[msgLength];
        commInterface.receive(recvData, msgLength, 2000);

        transInterface.transStatusHook(CommStatus.RECEIVED);
        emitter.onNext(CommStatus.RECEIVED);

        return recvData;
    }

    private void doErrorProcess(ObservableEmitter<Integer> emitter, Exception e) throws CommonException {
        LogUtil.d(TAG, "doErrorProcess");
        if (e instanceof CommonException) {
            CommonException commonException = (CommonException) e;
            LogUtil.d(TAG, "CommonException type=" + commonException.getExceptionType());
            LogUtil.d(TAG, "CommonException subErrType=" + commonException.getSubErrType());
            if (commonException.getExceptionType() == ExceptionType.COMM_EXCEPTION) {
                boolean isNoNeedThrowThisException = false;
                try {
                    isNoNeedThrowThisException = transInterface.processCommError(commonException.getSubErrType());
                } catch (Exception e1) {
                    LogUtil.d(TAG, "Transaction throw exception");
                    emitter.onError(e1);
                }
                LogUtil.d(TAG, "isNoNeedThrowThisException=" + isNoNeedThrowThisException);
                if (!isNoNeedThrowThisException) {
                    emitter.onError(e);
                }
            } else {
                emitter.onError(e);
            }
        } else {
            emitter.onError(e);
            e.printStackTrace();
        }
    }

    private void disconnectHost(ObservableEmitter<Integer> emitter) throws CommonException {
        LogUtil.d(TAG, "disconnectHost");

        transInterface.transStatusHook(CommStatus.CLOSEING);
        emitter.onNext(CommStatus.CLOSEING);

        try {
            commInterface.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        transInterface.transStatusHook(CommStatus.CLOSED);
        emitter.onNext(CommStatus.CLOSED);
    }
}
