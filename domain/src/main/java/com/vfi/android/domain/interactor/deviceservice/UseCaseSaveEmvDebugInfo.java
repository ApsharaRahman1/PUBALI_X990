/**
 * Created by yunlongg1 on 05/09/2017.
 */
package com.vfi.android.domain.interactor.deviceservice;

import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveEmvDebugInfo extends UseCase<Object, Void> {

    private final IPosService posService;
    private final IRepository iRepository;

    @Inject
    UseCaseSaveEmvDebugInfo(IPosService posService, IRepository iRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Object> buildUseCaseObservable(Void unused) {
        String[] tagList = {"50", "5A", "57", "5F2A", "5F34", "8A", "82", "8F", "91", "95", "9B", "9A", "9C", "9F02", "9F03", "9F0D", "9F0E", "9F0F", "9F10", "9F11", "9F12", "9F15", "9F1A", "9F20", "9F26", "9F27", "9F33", "9F34", "9F36", "9F37", "9F5B", "9F71"};
        String emvDebugInfoTlvTags = posService.getEMVTagList(tagList);
        LogUtil.d(TAGS.EmvFlow, "UseCaseSaveEmvDebugInfo tlvTags=" + emvDebugInfoTlvTags);
        TerminalStatus terminalStatus = iRepository.getTerminalStatus();
        terminalStatus.setLastEmvDebugInfo(emvDebugInfoTlvTags);
        iRepository.putTerminalStatus(terminalStatus);

        return Observable.just(true);
    }
}
