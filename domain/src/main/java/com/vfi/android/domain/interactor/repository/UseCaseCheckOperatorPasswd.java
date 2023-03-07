package com.vfi.android.domain.interactor.repository;


import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.OperatorInfo;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;


/**
 * Created by fusheng.z on 2018/04/08.
 */

public class UseCaseCheckOperatorPasswd extends UseCase<Boolean, OperatorInfo> {
    private final String TAG = TAGS.Encryption;
    private final IRepository iRepository;

    @Inject
    UseCaseCheckOperatorPasswd(IRepository iRepository,
                               ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(OperatorInfo operatorInfo) {
        TerminalCfg terminalCfg = iRepository.getTerminalCfg();
        boolean isCorrectPasswd = false;
        String passwd = "";

        LogUtil.d(TAG, "passwd type=" + operatorInfo.getOperatorType());
        String superPasswd = terminalCfg.getSuperPassword();
        LogUtil.d(TAG, "superPasswd=" + superPasswd);

        switch (operatorInfo.getOperatorType()) {
            case OperatorInfo.TYPE_OPERATOR:
                break;
            case OperatorInfo.TYPE_MANAGER:
                passwd = terminalCfg.getTransManagerPassword();
                if (passwd.equals(operatorInfo.getPasswdMd5Val())
                        || superPasswd.equals(operatorInfo.getPasswdMd5Val())) {
                    isCorrectPasswd = true;
                }
                break;
            case OperatorInfo.TYPE_SETTING:
                passwd = terminalCfg.getSettingPassword();
                if (passwd.equals(operatorInfo.getPasswdMd5Val())
                        || superPasswd.equals(operatorInfo.getPasswdMd5Val())) {
                    isCorrectPasswd = true;
                }
                break;
            case OperatorInfo.TYPE_SUPER_MANAGER:
                passwd = superPasswd;
                if (passwd.equals(operatorInfo.getPasswdMd5Val())) {
                    isCorrectPasswd = true;
                }
                break;
        }

        LogUtil.d(TAG, "passwd=[" + passwd + "] check pwd=[" + operatorInfo.getPasswdMd5Val() + "]");

        LogUtil.d(TAG, "isCorrectPasswd=" + isCorrectPasswd);
        return Observable.just(isCorrectPasswd);
    }
}
