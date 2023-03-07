package com.vfi.android.data.comm.network;

import com.vfi.android.domain.entities.comm.CommErrorType;
import com.vfi.android.domain.entities.comm.CommParam;
import com.vfi.android.domain.entities.comm.CommType;
import com.vfi.android.domain.entities.comm.SocketParam;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interfaces.comm.IComm;
import com.vfi.android.domain.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.inject.Inject;

public class SocketCommImpl implements IComm {
    private final String TAG = TAGS.COMM;
    private Socket socket = null;
    private OutputStream os = null;
    private InputStream is = null;

    private SocketParam param;

    @Inject
    public SocketCommImpl() {

    }

    @Override
    public void initCommParam(CommParam commParam) throws CommonException {
        LogUtil.d(TAG, "initCommParam type=" + commParam.getCommType());

        if (commParam.getCommType() != CommType.SOCKET) {
            LogUtil.d(TAG, "Wrong comm type=" + commParam.getCommType());
            throw new CommonException(ExceptionType.COMM_EXCEPTION, CommErrorType.COMM_PARAM_ERROR);
        }

        this.param = commParam.getSocketParam();
    }

    @Override
    public boolean connect() throws CommonException {
        String hostIp = param.getHostIp();
        int port = param.getPort();
        int timeout = param.getConnectTimeout();
        try {
            socket = new Socket();
            LogUtil.d(TAG, "hostIp=" + hostIp + " port=" + port + " timeout=" + timeout);
            socket.connect( new InetSocketAddress(hostIp, port), timeout * 1000);
            os = socket.getOutputStream();
            is = socket.getInputStream();
        } catch (SocketTimeoutException e) {
            LogUtil.d(TAG, "===Connect timeout.===");
            e.printStackTrace();
            throw new CommonException(ExceptionType.COMM_EXCEPTION, CommErrorType.CONNECT_TIMEOUT);
        } catch (Exception e) {
            LogUtil.d(TAG, "===Connect failed.===");
            e.printStackTrace();
            throw new CommonException(ExceptionType.COMM_EXCEPTION, CommErrorType.CONNECT_FAILED);
        }

        return true;
    }

    @Override
    public int send(byte[] data) throws CommonException {
        if (data == null) {
            return 0;
        }

        LogUtil.d(TAG, "send data, len=" + data.length);
        try {
            os.write(data);
            os.flush();
        } catch (Exception e) {
            LogUtil.d(TAG, "===Send data failed.===");
            e.printStackTrace();
            throw new CommonException(ExceptionType.COMM_EXCEPTION, CommErrorType.SEND_DATA_FAILED);
        }

        return data.length;
    }

    @Override
    public int receive(byte[] buf, int readLen, int timeoutMillisecond) throws CommonException {
        LogUtil.d(TAG, "receive readLen=" + readLen);

        if (buf == null || buf.length == 0) {
            LogUtil.d(TAG, "receive empty buffer.");
            return 0;
        }

        if (buf.length < readLen) {
            LogUtil.d(TAG, "reset read length to " + buf.length);
            readLen = buf.length;
        }

        long beginTime = System.currentTimeMillis();  // get current time
        long leftTimeout = timeoutMillisecond;  // 计算超时时间(毫秒)
        int offset = 0;
        int leftLength = readLen;
        do {
            try {
                LogUtil.d(TAG, "set timeout: " + leftTimeout + "  ms.");
                socket.setSoTimeout((int) leftTimeout);

                LogUtil.d(TAG, "offset=" + offset + " leftLength=" + leftLength);
                int ret = is.read(buf, offset, leftLength);
                if (ret <= leftLength && ret >= 0) {
                    LogUtil.d(TAG, " want length: " + leftLength + "  ,read length: " + ret);

                    offset += ret;
                    leftLength -= ret;

                    if (leftLength > 0) {
                        // calculate left timeout
                        long timeTook = Math.abs(System.currentTimeMillis() - beginTime);
                        leftTimeout = leftTimeout - timeTook;
                        LogUtil.d(TAG, " read again ");
                    }
                } else if (ret < 0) {
                    LogUtil.d(TAG, "read data, socket closed.");
                    throw new CommonException(ExceptionType.COMM_EXCEPTION, CommErrorType.READ_DATA_FAILED);
                }
            } catch (SocketTimeoutException e) {
                LogUtil.d(TAG, "read data timeout.");
                throw new CommonException(ExceptionType.COMM_EXCEPTION, CommErrorType.READ_DATA_TIMEOUT);
            } catch (IOException e) {
                LogUtil.d(TAG, "read data, failed.");
                e.printStackTrace();
                throw new CommonException(ExceptionType.COMM_EXCEPTION, CommErrorType.READ_DATA_FAILED);
            }
        } while (leftLength > 0 && leftTimeout > 0);

        return offset;
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (os != null) {
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (is != null) {
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
