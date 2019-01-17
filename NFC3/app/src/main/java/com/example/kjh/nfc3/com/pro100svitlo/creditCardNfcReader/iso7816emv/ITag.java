package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums.TagTypeEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums.TagValueTypeEnum;


public interface ITag {

    public enum Class {
        UNIVERSAL,
        APPLICATION,
        CONTEXT_SPECIFIC,
        PRIVATE
    }

    String getDescription();

    String getName();

    int getNumTagBytes();

    byte[] getTagBytes();

    Class getTagClass();

    TagValueTypeEnum getTagValueType();

    TagTypeEnum getType();

    boolean isConstructed();
}
