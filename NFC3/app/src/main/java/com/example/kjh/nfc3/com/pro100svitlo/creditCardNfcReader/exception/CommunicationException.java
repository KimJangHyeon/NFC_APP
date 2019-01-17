package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.exception;

import java.io.IOException;

public class CommunicationException extends IOException {
    public CommunicationException(String pMessage) {
        super(pMessage);
    }
}
