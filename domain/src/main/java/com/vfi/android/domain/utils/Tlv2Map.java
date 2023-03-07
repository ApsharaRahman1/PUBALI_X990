package com.vfi.android.domain.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Created by laikey on 2017/1/3.
 */

public class Tlv2Map {
    public static final int CODE_VALUE_OVERLENGTH = 51;
    public static final int CODE_LENGTH_OVERLENGTH = 52;
    public static final int CODE_PARAMS_INEXISTENCE = 53;

    public Tlv2Map(){}

    public static Map<String, String> tlv2Map(String tlv) {
        return tlv2Map(StringUtil.hexStr2Bytes(tlv));
    }

    public static String map2TlvStr(Map<String, String> map) {
        String tlvStr = StringUtil.bcd2Str(map2Tlv(map));
        if (tlvStr == null) {
            tlvStr = "";
        }

        return tlvStr;
    }

    public static byte[] map2Tlv(Map<String, String> map) {
        if (map == null) {
            throw new RuntimeException("MAP_DATA_IS_REQUIRED");
        }
        int len = 0;
        for (Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                int lenght = ((String) entry.getValue()).length() / 2;
                if (lenght > 0) {
                    if (lenght > 65535) {
                        throw new RuntimeException("VALUE_OVERSIZE");
                    }
                    if (lenght <= 127) {
                        len += 2;
                    }
                    if ((lenght > 127) && (lenght <= 255)) {
                        len += 4;
                    }
                    if ((lenght > 255) && (lenght <= 65535)) {
                        len += 6;
                    }
                    len += ((String) entry.getValue()).length();
                    len += ((String) entry.getKey()).length();
                }
            }
        }
        byte[] tlvData = new byte[len / 2];
        int pos = 0;
        for (Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                byte[] value = StringUtil.hexStr2Bytes((String) entry.getValue());
                int lenght = value.length;
                if (lenght > 0) {
                    if (lenght > 65535) {
                        throw new RuntimeException("VALUE_OVERSIZE");
                    }
                    byte[] key = StringUtil.hexStr2Bytes((String) entry.getKey());
                    System.arraycopy(key, 0, tlvData, pos, key.length);
                    pos += key.length;
                    if ((lenght <= 127) && (lenght > 0)) {
                        tlvData[pos] = ((byte) lenght);
                        pos++;
                    }
                    if ((lenght > 127) && (lenght <= 255)) {
                        tlvData[pos] = -127;
                        pos++;
                        tlvData[pos] = ((byte) lenght);
                        pos++;
                    }
                    if ((lenght > 255) && (lenght <= 65535)) {
                        tlvData[pos] = -126;
                        pos++;
                        tlvData[pos] = ((byte) (lenght >> 8 & 0xFF));
                        pos++;
                        tlvData[pos] = ((byte) (lenght & 0xFF));
                        pos++;
                    }
                    System.arraycopy(value, 0, tlvData, pos, lenght);
                    pos += lenght;
                }
            }
        }
        return tlvData;
    }

    public static Map<String, String> tlv2Map(byte[] tlv) {
        if (tlv == null) {
            return new HashMap<>();
        }
        Map<String, String> map = new HashMap();
        int index = 0;
        while (index < tlv.length) {
            if (((tlv[index] & 0x1F) == 31) && ((tlv[(index + 1)] & 0x80) == 128)) {
                byte[] tag = new byte[3];
                System.arraycopy(tlv, index, tag, 0, 3);
                index += 3;
                index = copyData(tlv, map, index, tag);
            } else if ((tlv[index] & 0x1F) == 31) {
                byte[] tag = new byte[2];
                System.arraycopy(tlv, index, tag, 0, 2);
                index += 2;
                index = copyData(tlv, map, index, tag);
            } else {
                byte[] tag = new byte[1];
                System.arraycopy(tlv, index, tag, 0, 1);
                index++;
                index = copyData(tlv, map, index, tag);
            }
        }
        return map;
    }

    private static int copyData(byte[] tlv, Map<String, String> map, int index, byte[] tag) {
        int length = 0;
        if (tlv[index] >> 7 == 0) {
            length = tlv[index];
            index++;
        } else {
            int lenlen = tlv[index] & 0x7F;
            index++;
            if (lenlen > 2) {
                throw new RuntimeException("TLV_OVERSIZE");
            }
            for (int i = 0; i < lenlen; i++) {
                length <<= 8;
                length += (tlv[index] & 0xFF);
                index++;
            }
        }
        byte[] value = new byte[length];
        System.arraycopy(tlv, index, value, 0, length);
        index += length;
        map.put(StringUtil.bcd2Str(tag), StringUtil.bcd2Str(value));
        return index;
    }

    public static class TlvExcetion extends Exception {
        private static final long serialVersionUID = 5876132721837945560L;
        private int errCode;

        public TlvExcetion(String msg) {
            this(0, msg);
        }

        public TlvExcetion(int code, String msg) {
            super();
            this.errCode = code;
        }

        public int getErrCode() {
            return this.errCode;
        }
    }
}
