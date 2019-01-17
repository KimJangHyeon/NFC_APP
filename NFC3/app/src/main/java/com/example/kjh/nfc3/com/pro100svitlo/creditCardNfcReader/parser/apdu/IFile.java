package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser.apdu;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.TagAndLength;


import java.util.List;

public interface IFile {
    void parse(byte[] bArr, List<TagAndLength> list);
}
