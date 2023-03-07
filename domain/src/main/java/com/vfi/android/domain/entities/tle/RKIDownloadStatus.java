package com.vfi.android.domain.entities.tle;

public class RKIDownloadStatus {
    public static final int DOWNLOAD_SUCCESS= 0;
    public static final int DOWNLOAD_FAILED = 1;
    public static final int REQ_INSERT_CARD = 2;
    public static final int REQ_INPUT_PIN   = 3;
    public static final int REQ_RE_INPUT_PIN= 4;
    public static final int START_DOWNLOADING = 5;

    public static String toDebugString(int status) {
        String text = "UNKNOWN[" + status + "]";

        switch (status) {
            case DOWNLOAD_SUCCESS:
                text = "DOWNLOAD_SUCCESS";
                break;
            case DOWNLOAD_FAILED:
                text = "DOWNLOAD_FAILED";
                break;
            case REQ_INSERT_CARD:
                text = "REQ_INSERT_CARD";
                break;
            case REQ_INPUT_PIN:
                text = "REQ_INPUT_PIN";
                break;
            case REQ_RE_INPUT_PIN:
                text = "REQ_RE_INPUT_PIN";
                break;
            case START_DOWNLOADING:
                text = "START_DOWNLOADING";
                break;
        }

        return text;
    }
}
