package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv;

import java.util.Arrays;

public final class ByteArrayWrapper {
    private final byte[] data;
    private final int hashcode;

    private ByteArrayWrapper(byte[] data) {
        this.data = data;
        this.hashcode = Arrays.hashCode(data);
    }

    public static ByteArrayWrapper wrapperAround(byte[] data) {
        if (data != null) {
            return new ByteArrayWrapper(data);
        }
        throw new NullPointerException();
    }

    public boolean equals(Object other) {
        if (other instanceof ByteArrayWrapper) {
            return Arrays.equals(this.data, ((ByteArrayWrapper) other).data);
        }
        return false;
    }

    public int hashCode() {
        return this.hashcode;
    }
}
