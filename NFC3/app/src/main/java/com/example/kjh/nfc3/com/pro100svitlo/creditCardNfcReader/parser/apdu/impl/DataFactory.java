package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser.apdu.impl;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.enums.IKeyEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser.apdu.annotation.AnnotationData;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils.EnumUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import fr.devnied.bitlib.BitUtils;

public final class DataFactory {
    public static final int BCD_DATE = 1;
    public static final String BCD_FORMAT = "BCD_Format";
    public static final int HALF_BYTE_SIZE = 4;
    public static final Logger LOGGER = LoggerFactory.getLogger(DataFactory.class.getName());

    private static Date getDate(AnnotationData pAnnotation, BitUtils pBit) {
        if (pAnnotation.getDateStandard() == 1) {
            return pBit.getNextDate(pAnnotation.getSize(), pAnnotation.getFormat(), true);
        }
        return pBit.getNextDate(pAnnotation.getSize(), pAnnotation.getFormat());
    }

    private static int getInteger(AnnotationData pAnnotation, BitUtils pBit) {
        return pBit.getNextInteger(pAnnotation.getSize());
    }

    public static Object getObject(AnnotationData pAnnotation, BitUtils pBit) {
        Class<?> clazz = pAnnotation.getField().getType();
        if (clazz.equals(Integer.class)) {
            return Integer.valueOf(getInteger(pAnnotation, pBit));
        }
        if (clazz.equals(Float.class)) {
            return getFloat(pAnnotation, pBit);
        }
        if (clazz.equals(String.class)) {
            return getString(pAnnotation, pBit);
        }
        if (clazz.equals(Date.class)) {
            return getDate(pAnnotation, pBit);
        }
        if (clazz.equals(Boolean.class)) {
            return Boolean.valueOf(pBit.getNextBoolean());
        }
        if (clazz.isEnum()) {
            return getEnum(pAnnotation, pBit);
        }
        return null;
    }

    private static Float getFloat(AnnotationData pAnnotation, BitUtils pBit) {
        if (BCD_FORMAT.equals(pAnnotation.getFormat())) {
            return Float.valueOf(Float.parseFloat(pBit.getNextHexaString(pAnnotation.getSize())));
        }
        return Float.valueOf((float) getInteger(pAnnotation, pBit));
    }

    private static IKeyEnum getEnum(AnnotationData pAnnotation, BitUtils pBit) {
        int val = 0;
        try {
            val = Integer.parseInt(pBit.getNextHexaString(pAnnotation.getSize()), pAnnotation.isReadHexa() ? 16 : 10);
        } catch (NumberFormatException e) {
        }
        //return EnumUtils.getValue(val, pAnnotation.getField().getType());
        return EnumUtils.getValue(val, (Class<? extends IKeyEnum>) pAnnotation.getField().getType());
    }

    private static String getString(AnnotationData pAnnotation, BitUtils pBit) {
        if (pAnnotation.isReadHexa()) {
            return pBit.getNextHexaString(pAnnotation.getSize());
        }
        return pBit.getNextString(pAnnotation.getSize()).trim();
    }

    private DataFactory() {
    }
}
