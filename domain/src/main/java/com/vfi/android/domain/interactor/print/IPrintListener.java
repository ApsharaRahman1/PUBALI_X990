package com.vfi.android.domain.interactor.print;

public interface IPrintListener {
    void onSuccess(int taskId);
    void onError(Throwable throwable);
}
