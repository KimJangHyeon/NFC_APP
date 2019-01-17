package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.enums;

public enum TransactionTypeEnum implements IKeyEnum {
    PURCHASE(0),
    CASH_ADVANCE(1),
    CASHBACK(9),
    REFUND(32);
    
    private final int value;

    private TransactionTypeEnum(int value) {
        this.value = value;
    }

    public int getKey() {
        return this.value;
    }
}
