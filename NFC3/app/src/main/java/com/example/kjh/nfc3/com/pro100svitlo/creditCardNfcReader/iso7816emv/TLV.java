package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv;

public class TLV {
    private int length;
    private byte[] rawEncodedLengthBytes;
    private ITag tag;
    private byte[] valueBytes;

    public TLV(ITag tag, int length, byte[] rawEncodedLengthBytes, byte[] valueBytes) {
        if (valueBytes == null || length != valueBytes.length) {
            throw new IllegalArgumentException("length != bytes.length");
        }
        this.tag = tag;
        this.rawEncodedLengthBytes = rawEncodedLengthBytes;
        this.valueBytes = valueBytes;
        this.length = length;
    }

    public ITag getTag() {
        return this.tag;
    }

    public void setTag(ITag tag) {
        this.tag = tag;
    }

    public byte[] getRawEncodedLengthBytes() {
        return this.rawEncodedLengthBytes;
    }

    public void setRawEncodedLengthBytes(byte[] rawEncodedLengthBytes) {
        this.rawEncodedLengthBytes = rawEncodedLengthBytes;
    }

    public byte[] getValueBytes() {
        return this.valueBytes;
    }

    public void setValueBytes(byte[] valueBytes) {
        this.valueBytes = valueBytes;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getTagBytes() {
        return this.tag.getTagBytes();
    }
}
