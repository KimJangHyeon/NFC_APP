package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums;

public enum CommandEnum {
    SELECT(0, 164, 4, 0),
    READ_RECORD(0, 178, 0, 0),
    GPO(128, 168, 0, 0),
    GET_DATA(128, 202, 0, 0);
    
    private final int cla;
    private final int ins;
    private final int p1;
    private final int p2;

    private CommandEnum(int cla, int ins, int p1, int p2) {
        this.cla = cla;
        this.ins = ins;
        this.p1 = p1;
        this.p2 = p2;
    }

    public int getCla() {
        return this.cla;
    }

    public int getIns() {
        return this.ins;
    }

    public int getP1() {
        return this.p1;
    }

    public int getP2() {
        return this.p2;
    }
}
