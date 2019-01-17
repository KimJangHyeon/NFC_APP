package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.exception.CommunicationException;


public interface IProvider {
    byte[] transceive(byte[] bArr) throws CommunicationException, CommunicationException;
}
