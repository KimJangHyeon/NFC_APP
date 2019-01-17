package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums.EmvCardScheme;

import java.util.Collection;
import java.util.List;

public class EmvCard extends AbstractData {
    private static final long serialVersionUID = 736740432469989941L;
    private String aid;
    private String applicationLabel;
    private Collection<String> atrDescription;
    private String cardNumber;
    private String expireDate;
    private String holderFirstname;
    private String holderLastname;
    private int leftPinTry;
    private List<EmvTransactionRecord> listTransactions;
    private boolean nfcLocked;
    private Service service;
    private EmvCardScheme type;

    public String getAid() {
        return this.aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getHolderLastname() {
        return this.holderLastname;
    }

    public void setHolderLastname(String holderLastname) {
        this.holderLastname = holderLastname;
    }

    public String getHolderFirstname() {
        return this.holderFirstname;
    }

    public void setHolderFirstname(String holderFirstname) {
        this.holderFirstname = holderFirstname;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpireDate() {
        return this.expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public List<EmvTransactionRecord> getListTransactions() {
        return this.listTransactions;
    }

    public void setListTransactions(List<EmvTransactionRecord> listTransactions) {
        this.listTransactions = listTransactions;
    }

    public EmvCardScheme getType() {
        return this.type;
    }

    public void setType(EmvCardScheme type) {
        this.type = type;
    }

    public String getApplicationLabel() {
        return this.applicationLabel;
    }

    public void setApplicationLabel(String applicationLabel) {
        this.applicationLabel = applicationLabel;
    }

    public boolean equals(Object arg0) {
        return (arg0 instanceof EmvCard) && this.cardNumber != null && this.cardNumber.equals(((EmvCard) arg0).getCardNumber());
    }

    public int getLeftPinTry() {
        return this.leftPinTry;
    }

    public void setLeftPinTry(int leftPinTry) {
        this.leftPinTry = leftPinTry;
    }

    public Collection<String> getAtrDescription() {
        return this.atrDescription;
    }

    public void setAtrDescription(Collection<String> atrDescription) {
        this.atrDescription = atrDescription;
    }

    public Service getService() {
        return this.service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public boolean isNfcLocked() {
        return this.nfcLocked;
    }

    public void setNfcLocked(boolean nfcLocked) {
        this.nfcLocked = nfcLocked;
    }
}
