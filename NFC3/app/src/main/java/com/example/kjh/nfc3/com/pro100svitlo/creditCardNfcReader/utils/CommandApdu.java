package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums.CommandEnum;


public class CommandApdu {
    protected int mCla;
    protected byte[] mData;
    protected int mIns;
    protected int mLc;
    protected int mLe;
    protected boolean mLeUsed;
    protected int mP1;
    protected int mP2;

    public CommandApdu(CommandEnum pEnum, byte[] data, int le) {
        int i = 0;
        this.mCla = 0;
        this.mIns = 0;
        this.mP1 = 0;
        this.mP2 = 0;
        this.mLc = 0;
        this.mData = new byte[0];
        this.mLe = 0;
        this.mLeUsed = false;
        this.mCla = pEnum.getCla();
        this.mIns = pEnum.getIns();
        this.mP1 = pEnum.getP1();
        this.mP2 = pEnum.getP2();
        if (data != null) {
            i = data.length;
        }
        this.mLc = i;
        this.mData = data;
        this.mLe = le;
        this.mLeUsed = true;
    }

    public CommandApdu(CommandEnum pEnum, int p1, int p2, int le) {
        this.mCla = 0;
        this.mIns = 0;
        this.mP1 = 0;
        this.mP2 = 0;
        this.mLc = 0;
        this.mData = new byte[0];
        this.mLe = 0;
        this.mLeUsed = false;
        this.mCla = pEnum.getCla();
        this.mIns = pEnum.getIns();
        this.mP1 = p1;
        this.mP2 = p2;
        this.mLe = le;
        this.mLeUsed = true;
    }

    public CommandApdu(CommandEnum pEnum, int p1, int p2) {
        this.mCla = 0;
        this.mIns = 0;
        this.mP1 = 0;
        this.mP2 = 0;
        this.mLc = 0;
        this.mData = new byte[0];
        this.mLe = 0;
        this.mLeUsed = false;
        this.mCla = pEnum.getCla();
        this.mIns = pEnum.getIns();
        this.mP1 = p1;
        this.mP2 = p2;
        this.mLeUsed = false;
    }

    public byte[] toBytes() {
        int length = 4;
        if (!(this.mData == null || this.mData.length == 0)) {
            length = 4 + 1;
            length = this.mData.length + 5;
        }
        if (this.mLeUsed) {
            length++;
        }
        byte[] apdu = new byte[length];
        apdu[0] = (byte) this.mCla;
        int index = 0 + 1;
        apdu[index] = (byte) this.mIns;
        index++;
        apdu[index] = (byte) this.mP1;
        index++;
        apdu[index] = (byte) this.mP2;
        index++;
        if (!(this.mData == null || this.mData.length == 0)) {
            apdu[index] = (byte) this.mLc;
            System.arraycopy(this.mData, 0, apdu, index + 1, this.mData.length);
            index = this.mData.length + 5;
        }
        if (this.mLeUsed) {
            apdu[index] = (byte) (apdu[index] + ((byte) this.mLe));
        }
        return apdu;
    }
}
