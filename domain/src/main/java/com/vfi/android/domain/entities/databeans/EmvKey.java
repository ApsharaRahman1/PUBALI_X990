package com.vfi.android.domain.entities.databeans;

import com.vfi.android.domain.utils.LogUtil;

public class EmvKey {
    String keyIndex = "";
    String rid = "";
    String exponent = "";
    String keyLen = "";
    String key = "";
    String hash = "";
    String hashAlgoIndicator = "";
    String pkAlgoIndicator = "";
    String expiryDate = "";

    public String toEmvKeyStr() {
        String emvKey = "";

        emvKey += getEmvKeyTagStr("Index", getKeyIndex());
        emvKey += getEmvKeyTagStr("RID", getRid());
        emvKey += getEmvKeyTagStr("Exponent", getExponent());
        emvKey += getEmvKeyTagStr("Key", getKey());
        emvKey += getEmvKeyTagStr("Hash", getHash());
        emvKey += getEmvKeyTagStr("HashAlgoIndicator", getHashAlgoIndicator());
        emvKey += getEmvKeyTagStr("PKAlgoIndicator", getPkAlgoIndicator());
        emvKey += getEmvKeyTagStr("ExpiryDate", getExpiryDate());

        if (getHashAlgoIndicator().length() != 2) {
            emvKey += "DF060101";
        }

        if (getPkAlgoIndicator().length() != 2) {
            emvKey += "DF070101";
        }

        LogUtil.d("TAG", "Emv key str=[" + emvKey + "]");
        return emvKey;
    }

    public String getEmvTagByBeanTagName(String beanTagName) {
        if (beanTagName != null) {
            switch (beanTagName) {
                case "Index":
                    return "9F22";
                case "RID":
                    return "9F06";
                case "Exponent":
                    return "DF04";
                case "KeyLen":
                    return "";
                case "Key":
                    return "DF0281";
                case "Hash":
                    return "DF03";
                case "HashAlgoIndicator":
                    return "DF06";
                case "PKAlgoIndicator":
                    return "DF07";
                case "ExpiryDate":
                    return "DF05";
            }
        }

        return "";
    }

    public String getEmvKeyTagStr(String beanTagName, String value) {
        String emvAppStr = "";

        if (beanTagName == null || beanTagName.length() == 0
                || value == null || value.length() == 0) {
            return "";
        }

        String emvTag = getEmvTagByBeanTagName(beanTagName);
        if (emvTag.length() == 0) {
            return "";
        }

        if (value.length() % 2 != 0) {
            value = "0" + value;
        }

        emvAppStr += emvTag;
        String lenStr = getLenStr(value.length());
        emvAppStr += lenStr;
        emvAppStr += value;

        LogUtil.d("TAG", "emvAppStr=[" + emvAppStr + "]");
        return emvAppStr;
    }

    public String getLenStr(int len) {
        String lenStr = "";
        LogUtil.d("TAG", "In len=" + len);
        if (len == 0) {
            lenStr = "00";
        } else {
            lenStr = String.format("%02X", len / 2);
        }
        LogUtil.d("TAG", "Out len=" + lenStr);
        return lenStr;
    }

    public String getKeyIndex() {
        return keyIndex;
    }

    public String getRid() {
        return rid;
    }

    public String getExponent() {
        return exponent;
    }

    public String getKeyLen() {
        return keyLen;
    }

    public String getKey() {
        return key;
    }

    public String getHash() {
        return hash;
    }

    public String getHashAlgoIndicator() {
        return hashAlgoIndicator;
    }

    public String getPkAlgoIndicator() {
        return pkAlgoIndicator;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setKeyIndex(String keyIndex) {
        this.keyIndex = keyIndex;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public void setExponent(String exponent) {
        this.exponent = exponent;
    }

    public void setKeyLen(String keyLen) {
        this.keyLen = keyLen;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setHashAlgoIndicator(String hashAlgoIndicator) {
        this.hashAlgoIndicator = hashAlgoIndicator;
    }

    public void setPkAlgoIndicator(String pkAlgoIndicator) {
        this.pkAlgoIndicator = pkAlgoIndicator;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
