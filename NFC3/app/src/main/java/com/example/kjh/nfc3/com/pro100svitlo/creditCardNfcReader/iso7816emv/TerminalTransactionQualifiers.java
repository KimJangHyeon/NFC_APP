package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils.BytesUtils;

import java.util.Arrays;

public class TerminalTransactionQualifiers {
    private byte[] data = new byte[4];

    public boolean contactlessMagneticStripeSupported() {
        return BytesUtils.matchBitByBitIndex(this.data[0], 7);
    }

    public boolean contactlessVSDCsupported() {
        return BytesUtils.matchBitByBitIndex(this.data[0], 6);
    }

    public boolean contactlessEMVmodeSupported() {
        return BytesUtils.matchBitByBitIndex(this.data[0], 5);
    }

    public boolean contactEMVsupported() {
        return BytesUtils.matchBitByBitIndex(this.data[0], 4);
    }

    public boolean readerIsOfflineOnly() {
        return BytesUtils.matchBitByBitIndex(this.data[0], 3);
    }

    public boolean onlinePINsupported() {
        return BytesUtils.matchBitByBitIndex(this.data[0], 2);
    }

    public boolean signatureSupported() {
        return BytesUtils.matchBitByBitIndex(this.data[0], 1);
    }

    public boolean onlineCryptogramRequired() {
        return BytesUtils.matchBitByBitIndex(this.data[1], 7);
    }

    public boolean cvmRequired() {
        return BytesUtils.matchBitByBitIndex(this.data[1], 6);
    }

    public boolean contactChipOfflinePINsupported() {
        return BytesUtils.matchBitByBitIndex(this.data[1], 5);
    }

    public boolean issuerUpdateProcessingSupported() {
        return BytesUtils.matchBitByBitIndex(this.data[2], 7);
    }

    public boolean consumerDeviceCVMsupported() {
        return BytesUtils.matchBitByBitIndex(this.data[2], 6);
    }

    public void setContactlessMagneticStripeSupported(boolean value) {
        this.data[0] = BytesUtils.setBit(this.data[0], 7, value);
    }

    public void setContactlessVSDCsupported(boolean value) {
        this.data[0] = BytesUtils.setBit(this.data[0], 6, value);
        if (value) {
            setContactlessEMVmodeSupported(false);
        }
    }

    public void setContactlessEMVmodeSupported(boolean value) {
        this.data[0] = BytesUtils.setBit(this.data[0], 5, value);
    }

    public void setContactEMVsupported(boolean value) {
        this.data[0] = BytesUtils.setBit(this.data[0], 4, value);
    }

    public void setReaderIsOfflineOnly(boolean value) {
        this.data[0] = BytesUtils.setBit(this.data[0], 3, value);
    }

    public void setOnlinePINsupported(boolean value) {
        this.data[0] = BytesUtils.setBit(this.data[0], 2, value);
    }

    public void setSignatureSupported(boolean value) {
        this.data[0] = BytesUtils.setBit(this.data[0], 1, value);
    }

    public void setOnlineCryptogramRequired(boolean value) {
        this.data[1] = BytesUtils.setBit(this.data[1], 7, value);
    }

    public void setCvmRequired(boolean value) {
        this.data[1] = BytesUtils.setBit(this.data[1], 6, value);
    }

    public void setContactChipOfflinePINsupported(boolean value) {
        this.data[1] = BytesUtils.setBit(this.data[1], 5, value);
    }

    public void setIssuerUpdateProcessingSupported(boolean value) {
        this.data[2] = BytesUtils.setBit(this.data[2], 7, value);
    }

    public void setConsumerDeviceCVMsupported(boolean value) {
        this.data[2] = BytesUtils.setBit(this.data[2], 6, value);
    }

    public byte[] getBytes() {
        return Arrays.copyOf(this.data, this.data.length);
    }
}
