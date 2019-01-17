package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model;

public class Afl {
    private int firstRecord;
    private int lastRecord;
    private boolean offlineAuthentication;
    private int sfi;

    public int getSfi() {
        return this.sfi;
    }

    public void setSfi(int sfi) {
        this.sfi = sfi;
    }

    public int getFirstRecord() {
        return this.firstRecord;
    }

    public void setFirstRecord(int firstRecord) {
        this.firstRecord = firstRecord;
    }

    public int getLastRecord() {
        return this.lastRecord;
    }

    public void setLastRecord(int lastRecord) {
        this.lastRecord = lastRecord;
    }

    public boolean isOfflineAuthentication() {
        return this.offlineAuthentication;
    }

    public void setOfflineAuthentication(boolean offlineAuthentication) {
        this.offlineAuthentication = offlineAuthentication;
    }
}
