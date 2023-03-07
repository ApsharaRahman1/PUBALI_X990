package com.vfi.android.domain.interactor.print;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.PrintTask;
import com.vfi.android.domain.entities.databeans.PrinterParamIn;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

@Singleton
public class PrintTaskManager {
    private final String TAG = TAGS.PRINT;
    private LinkedList<PrintTask> printTasks;
    private Lock lock;
    private Condition printCondition;
    private Condition waitingPrintFinishedCondition;
    private IPosService posService;
    private PrinterThread printerThread;
    private IPrintListener printListener;
    private boolean isRunning;

    @Inject
    public PrintTaskManager(IPosService posService) {
        printTasks = new LinkedList<>();
        lock = new ReentrantLock();
        printCondition = lock.newCondition();
        waitingPrintFinishedCondition = lock.newCondition();

        this.posService = posService;

        printerThread = new PrinterThread();
    }

    public void addPrintTask(PrintTask task, IPrintListener printListener) {
        LogUtil.d(TAG, "addPrintTask taskId=" + task.getTaskId());
        lock.lock();
        this.printListener = printListener;
        printTasks.addLast(task);
        printCondition.signalAll();
        lock.unlock();

        LogUtil.d(TAG, "print thread alive=" + printerThread.isAlive());
        if (!isRunning) {
            isRunning = true;
            printerThread = new PrinterThread();
            printerThread.start();
        }
    }

    public void continuePrint(IPrintListener printListener) {
        this.printListener = printListener;
        if (!isRunning) {
            isRunning = true;
            printerThread = new PrinterThread();
            printerThread.start();
        }
    }

    public void clearPrintTasks() {
        lock.lock();
        printTasks.clear();
        lock.unlock();
    }

    private class PrinterThread extends Thread {
        @Override
        public void run() {
            while (isRunning) { // 据说Application挂掉，线程就会挂掉
                lock.lock();
                if (printTasks.size() <= 0) {
                    try {
                        LogUtil.d(TAG, "PrinterThread await.");
                        printCondition.await();
                        LogUtil.d(TAG, "PrinterThread wakeup.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                lock.unlock();

                PrintTask task = printTasks.getFirst();
                task.checkStartPrintFlag();

                LogUtil.d(TAG, "Start print");
                posService.setGray(task.getPrintGary())
                        .flatMap(o -> Observable.fromArray(task.getPrinterParamIns()))
                        .concatMap(printerParamIns -> Observable.fromIterable(printerParamIns))
                        .concatMap(printerParamIn -> {
                            LogUtil.i(TAG, printerParamIn.getContent());
                            switch (printerParamIn.getType()) {
                                case PrinterParamIn.PRINT_TEXT:
                                    return posService.addPrintText(printerParamIn);
                                case PrinterParamIn.PRINT_DIY_ALIGN:
                                    return posService.addDiyAlignText(printerParamIn);
                                case PrinterParamIn.PRINT_BARCODE:
                                    return posService.addPrintBarCode(printerParamIn);
                                case PrinterParamIn.PRINT_QRCODE:
                                    return posService.addPrintQrCode(printerParamIn);
                                case PrinterParamIn.PRINT_IMAGE:
                                    return posService.addPrintImage(printerParamIn);
                                case PrinterParamIn.PRINT_PAPERFEED:
                                    return posService.addPrintFeedLine(printerParamIn);
                                case PrinterParamIn.PRINT_TEXT_IN_LINE:
                                    return posService.addPrintTextInLine(printerParamIn);
                                case PrinterParamIn.PRINT_START:
                                    LogUtil.i(TAG, "打印 -----> 开始");
                                    return posService.startPrint();
                                default:
                                    LogUtil.e(TAG, "Unknown print type=" + printerParamIn.getType());
                                    throw new RuntimeException("Unknown print type.");
                            }
                        }).doOnError(throwable -> {
                    LogUtil.d(TAG, "Print error happened.");
                    isRunning = false;
                    throwable.printStackTrace();
                    if (printListener != null) {
                        printListener.onError(throwable);
                    }
                    lock.lock();
                    waitingPrintFinishedCondition.signal();
                    lock.unlock();
                }).doOnComplete(() -> {
                    lock.lock();
                    waitingPrintFinishedCondition.signal();
                    lock.unlock();
                }).subscribe();

                lock.lock();
                try {
                    waitingPrintFinishedCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }

                if (printListener != null && !task.isOnlyNotifyPrintError()) {
                    printListener.onSuccess(task.getTaskId());
                }
                LogUtil.d(TAG, "End print");

                lock.lock();
                if (isRunning) { // print error do not delete task
                    printTasks.removeFirst();
                }
                lock.unlock();
            }

            LogUtil.d(TAG, "Exit print thread");
        }
    }
}
