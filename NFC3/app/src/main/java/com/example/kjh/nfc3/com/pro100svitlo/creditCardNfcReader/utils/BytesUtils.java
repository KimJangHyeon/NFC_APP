package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils;

import java.math.BigInteger;
import java.util.Locale;

public final class BytesUtils {
    private static final int DEFAULT_MASK = 255;
    private static final String FORMAT_NOSPACE = "%02x";
    private static final String FORMAT_SPACE = "%02x ";
    private static final int HEXA = 16;
    private static final int MAX_BIT_INTEGER = 31;

    public static int byteArrayToInt(byte[] byteArray) {
        if (byteArray != null) {
            return byteArrayToInt(byteArray, 0, byteArray.length);
        }
        throw new IllegalArgumentException("Parameter 'byteArray' cannot be null");
    }

    public static int byteArrayToInt(byte[] byteArray, int startPos, int length) {
        if (byteArray == null) {
            throw new IllegalArgumentException("Parameter 'byteArray' cannot be null");
        } else if (length <= 0 || length > 4) {
            throw new IllegalArgumentException("Length must be between 1 and 4. Length = " + length);
        } else if (startPos < 0 || byteArray.length < startPos + length) {
            throw new IllegalArgumentException("Length or startPos not valid");
        } else {
            int value = 0;
            for (int i = 0; i < length; i++) {
                value += (byteArray[startPos + i] & 255) << (((length - i) - 1) * 8);
            }
            return value;
        }
    }

    public static String bytesToString(byte[] pBytes) {
        return formatByte(pBytes, FORMAT_SPACE, false);
    }

    public static String bytesToString(byte[] pBytes, boolean pTruncate) {
        return formatByte(pBytes, FORMAT_SPACE, pTruncate);
    }

    public static String bytesToStringNoSpace(byte pByte) {
        return formatByte(new byte[]{pByte}, FORMAT_NOSPACE, false);
    }

    public static String bytesToStringNoSpace(byte[] pBytes) {
        return formatByte(pBytes, FORMAT_NOSPACE, false);
    }

    public static String bytesToStringNoSpace(byte[] pBytes, boolean pTruncate) {
        return formatByte(pBytes, FORMAT_NOSPACE, pTruncate);
    }

    private static String formatByte(byte[] pByte, String pFormat, boolean pTruncate) {
        StringBuffer sb = new StringBuffer();
        if (pByte == null) {
            sb.append("");
        } else {
            boolean t = false;
            byte[] arr$ = pByte;
            int len$ = pByte.length;
            for (int i$ = 0; i$ < len$; i$++) {
                if (arr$[i$] != (byte) 0 || !pTruncate || t) {
                    t = true;
                    sb.append(String.format(pFormat, new Object[]{Integer.valueOf(i$ & 255)}));
                }
            }
        }
        return sb.toString().toUpperCase(Locale.getDefault()).trim();
    }

    public static byte[] fromString(String pData) {
        if (pData == null) {
            throw new IllegalArgumentException("Argument can't be null");
        }
        String text = pData.replace(" ", "");
        if (text.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex binary needs to be even-length :" + pData);
        }
        byte[] commandByte = new byte[Math.round(((float) text.length()) / 2.0f)];
        int j = 0;
        int i = 0;
        while (i < text.length()) {
            int j2 = j + 1;
            commandByte[j] = Integer.valueOf(Integer.parseInt(text.substring(i, i + 2), 16)).byteValue();
            i += 2;
            j = j2;
        }
        return commandByte;
    }

    public static boolean matchBitByBitIndex(int pVal, int pBitIndex) {
        if (pBitIndex < 0 || pBitIndex > 31) {
            throw new IllegalArgumentException("parameter 'pBitIndex' must be between 0 and 31. pBitIndex=" + pBitIndex);
        } else if (((1 << pBitIndex) & pVal) != 0) {
            return true;
        } else {
            return false;
        }
    }

    public static byte setBit(byte pData, int pBitIndex, boolean pOn) {
        if (pBitIndex < 0 || pBitIndex > 7) {
            throw new IllegalArgumentException("parameter 'pBitIndex' must be between 0 and 7. pBitIndex=" + pBitIndex);
        } else if (pOn) {
            return (byte) ((1 << pBitIndex) | pData);
        } else {
            return (byte) (((1 << pBitIndex) ^ -1) & pData);
        }
    }

    public static String toBinary(byte[] pBytes) {
        if (pBytes == null || pBytes.length <= 0) {
            return null;
        }
        StringBuilder build = new StringBuilder(new BigInteger(bytesToStringNoSpace(pBytes), 16).toString(2));
        for (int i = build.length(); i < pBytes.length * 8; i++) {
            build.insert(0, 0);
        }
        return build.toString();
    }

    public static byte[] toByteArray(int value) {
        return new byte[]{(byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value};
    }

    private BytesUtils() {
    }
}
