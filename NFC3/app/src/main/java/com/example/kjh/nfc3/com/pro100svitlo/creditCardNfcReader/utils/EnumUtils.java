package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.enums.IKeyEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EnumUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnumUtils.class);

//    public static <T extends IKeyEnum> T getValue(int pKey, Class<T> pClass) {
//        for (T val : (IKeyEnum[]) pClass.getEnumConstants()) {
//            if (val.getKey() == pKey) {
//                return val;
//            }
//        }
//        LOGGER.error("Unknow value:" + pKey + " for Enum:" + pClass.getName());
//        return null;
//    }
    @SuppressWarnings("unchecked")
    public static <T extends IKeyEnum> T getValue(final int pKey, final Class<T> pClass) {
        for (IKeyEnum val : pClass.getEnumConstants()) {
            if (val.getKey() == pKey) {
                return (T) val;
            }
        }
        LOGGER.error("Unknow value:" + pKey + " for Enum:" + pClass.getName());
        return null;
    }

    private EnumUtils() {
    }
}
