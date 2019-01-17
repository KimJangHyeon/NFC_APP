package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums.SwEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public final class ResponseUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseUtils.class);

    public static boolean isSucceed(byte[] pByte) {
        return isEquals(pByte, SwEnum.SW_9000);
    }

    public static boolean isEquals(byte[] pByte, SwEnum pEnum) {
        SwEnum val = SwEnum.getSW(pByte);
        if (LOGGER.isDebugEnabled() && pByte != null) {
            LOGGER.debug("Response Status <" + BytesUtils.bytesToStringNoSpace(Arrays.copyOfRange(pByte, pByte.length - 2, pByte.length)) + "> : " + (val != null ? val.getDetail() : "Unknow"));
        }
        return val != null && val == pEnum;
    }

    private ResponseUtils() {
    }
}
