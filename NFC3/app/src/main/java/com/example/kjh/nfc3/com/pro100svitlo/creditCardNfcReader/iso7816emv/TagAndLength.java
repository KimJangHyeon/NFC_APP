package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv;

import java.util.Arrays;

public class TagAndLength {
    private int length;
    private ITag tag;

    public TagAndLength(ITag tag, int length) {
        this.tag = tag;
        this.length = length;
    }

    public ITag getTag() {
        return this.tag;
    }

    public int getLength() {
        return this.length;
    }

    public byte[] getBytes() {
        byte[] tagBytes = this.tag.getTagBytes();
        byte[] tagAndLengthBytes = Arrays.copyOf(tagBytes, tagBytes.length + 1);
        tagAndLengthBytes[tagAndLengthBytes.length - 1] = (byte) this.length;
        return tagAndLengthBytes;
    }

    public String toString() {
        return this.tag.toString() + " length: " + this.length;
    }
}
