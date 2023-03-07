package com.vfi.android.domain.utils;

import java.security.PrivateKey;

public class RSAData {
    private String publicKeyExponent;
    private String publicKeyModulus;
    private PrivateKey privateKey;

    public String getPublicKeyExponent() {
        return publicKeyExponent;
    }

    public void setPublicKeyExponent(String publicKeyExponent) {
        this.publicKeyExponent = publicKeyExponent;
    }

    public String getPublicKeyModulus() {
        return publicKeyModulus;
    }

    public void setPublicKeyModulus(String publicKeyModulus) {
        this.publicKeyModulus = publicKeyModulus;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}
