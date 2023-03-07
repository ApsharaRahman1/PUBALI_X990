package com.vfi.android.domain.utils;

import java.io.UnsupportedEncodingException;

public class TlvUtils {

    private static final String TAG ="TlvUtils";

    /**
     * @param tag     tag
     * @param value   hex字符串
     * @param isAscii 是否是ascii
     * @return tag+len+value字符串
     */
    public static String hexTlv2Str(String tag, String value, boolean isAscii) {
        String tmp;

        if (value == null) {
            LogUtil.i( TAG, "hexTlv2Str[" + tag + "] = NULL" );
            return "";
        }

        //8583打包会asc2bcd, 所以这里要转一下
        if (isAscii) {
            tmp = StringUtil.bcd2Asc( value.getBytes() ); //("123"->"313233")
        } else {
            tmp = value;
        }

        StringBuilder stringBuilder = new StringBuilder();
        String tagLength = String.format( "%02x", tmp.length() / 2 );

        stringBuilder.append( tag );
        stringBuilder.append( tagLength );
        stringBuilder.append( tmp );

        return stringBuilder.toString();
    }

    /**
     * @param tag   tag
     * @param value ascii字符串
     * @return tag+len+value字符串
     */
    public static String tlv2Str(String tag, String value) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append( tag );
        stringBuilder.append( String.format( "%03d", value.length() ) );
        stringBuilder.append( value );

        return stringBuilder.toString();
    }

    /**
     * @param field 域
     * @param value ascii字符串
     * @return tag+len+value字符串
     */
    public static String tlv2BcdOctal(int field, String value) {
        StringBuilder stringBuilder = new StringBuilder();

        String tag = String.format( "%02o", field );
        String len = calcProtectedTextValLen( (value.length() + 1) / 2 );

        LogUtil.i( TAG, "tlv2BcdOctal T: " + tag + " L: " + len + " V: " + value );

        //八进制
        stringBuilder.append( String.format( "%02o", field ) );
        stringBuilder.append( len );
        stringBuilder.append( value );

        try {
            return new String( StringUtil.asc2BcdLeft( stringBuilder.toString(), stringBuilder.length() ), "ISO-8859-1" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String tlv2StrOctal(int field, String value, boolean isLLASC) {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder tagAndLen = new StringBuilder();

        String tag = String.format( "%02o", field );
        String len;
        if (isLLASC)
            len = calcProtectedTextValLen( value.length() + 2 );
        else
            len = calcProtectedTextValLen( value.length() );

        LogUtil.i( TAG, "tlv2StrOctal T: " + tag + " L: " + len + " V: " + value );

        //八进制
        tagAndLen.append( String.format( "%02o", field ) );
        tagAndLen.append( len );
        if (isLLASC)
            tagAndLen.append( String.format( "%04d", value.length() ) );

        try {
            stringBuilder.append( new String( StringUtil.asc2BcdLeft( tagAndLen.toString(), tagAndLen.length() ), "ISO-8859-1" ) );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        stringBuilder.append( value );

        return stringBuilder.toString();
    }

    private static String calcProtectedTextValLen(int lenInt) {
        if (lenInt > 127) {
            byte len[] = new byte[2];
            len[0] = (byte) (lenInt / 128);
            len[0] = (byte) (len[0] | 0x80);
            len[1] = (byte) (lenInt % 128);
            return StringUtil.byte2Str( len );
        } else {
            byte len[] = new byte[1];
            len[0] = (byte) lenInt;
            return StringUtil.byte2Str( len );
        }
    }
}
