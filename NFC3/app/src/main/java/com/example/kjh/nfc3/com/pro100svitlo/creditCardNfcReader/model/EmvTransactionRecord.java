package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.enums.CountryCodeEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.enums.CurrencyEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.enums.TransactionTypeEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser.apdu.annotation.Data;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser.apdu.impl.AbstractByteBean;

import java.io.Serializable;
import java.util.Date;

public class EmvTransactionRecord extends AbstractByteBean<EmvTransactionRecord> implements Serializable {
    private static final long serialVersionUID = -7050737312961921452L;
    @Data(format = "BCD_Format", index = 1, size = 48, tag = "9f02")
    private Float amount;
    @Data(index = 4, size = 16, tag = "5f2a")
    private CurrencyEnum currency;
    @Data(index = 2, readHexa = true, size = 8, tag = "9f27")
    private String cyptogramData;
    @Data(dateStandard = 1, format = "yyMMdd", index = 5, size = 24, tag = "9a")
    private Date date;
    @Data(index = 3, size = 16, tag = "9f1a")
    private CountryCodeEnum terminalCountry;
    @Data(dateStandard = 1, format = "HHmmss", index = 7, size = 24, tag = "9f21")
    private Date time;
    @Data(index = 6, readHexa = true, size = 8, tag = "9c")
    private TransactionTypeEnum transactionType;

    public Float getAmount() {
        return this.amount;
    }

    public String getCyptogramData() {
        return this.cyptogramData;
    }

    public CurrencyEnum getCurrency() {
        return this.currency;
    }

    public TransactionTypeEnum getTransactionType() {
        return this.transactionType;
    }

    public CountryCodeEnum getTerminalCountry() {
        return this.terminalCountry;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public void setCyptogramData(String cyptogramData) {
        this.cyptogramData = cyptogramData;
    }

    public void setTerminalCountry(CountryCodeEnum terminalCountry) {
        this.terminalCountry = terminalCountry;
    }

    public void setCurrency(CurrencyEnum currency) {
        this.currency = currency;
    }

    public void setTransactionType(TransactionTypeEnum transactionType) {
        this.transactionType = transactionType;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
