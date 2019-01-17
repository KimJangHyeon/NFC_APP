package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.enums.CountryCodeEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.enums.CurrencyEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.enums.TransactionTypeEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils.BytesUtils;

import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class EmvTerminal {
    private static final SecureRandom random = new SecureRandom();

    public static byte[] constructValue(TagAndLength pTagAndLength) {
        byte[] ret = new byte[pTagAndLength.getLength()];
        byte[] val = null;
        if (pTagAndLength.getTag() == EmvTags.TERMINAL_TRANSACTION_QUALIFIERS) {
            TerminalTransactionQualifiers terminalQual = new TerminalTransactionQualifiers();
            terminalQual.setContactlessEMVmodeSupported(true);
            terminalQual.setReaderIsOfflineOnly(true);
            val = terminalQual.getBytes();
        } else if (pTagAndLength.getTag() == EmvTags.TERMINAL_COUNTRY_CODE) {
            val = BytesUtils.fromString(StringUtils.leftPad(String.valueOf(CountryCodeEnum.FR.getNumeric()), pTagAndLength.getLength() * 2, "0"));
        } else if (pTagAndLength.getTag() == EmvTags.TRANSACTION_CURRENCY_CODE) {
            val = BytesUtils.fromString(StringUtils.leftPad(String.valueOf(CurrencyEnum.EUR.getISOCodeNumeric()), pTagAndLength.getLength() * 2, "0"));
        } else if (pTagAndLength.getTag() == EmvTags.TRANSACTION_DATE) {
            val = BytesUtils.fromString(new SimpleDateFormat("yyMMdd").format(new Date()));
        } else if (pTagAndLength.getTag() == EmvTags.TRANSACTION_TYPE) {
            val = new byte[]{(byte) TransactionTypeEnum.PURCHASE.getKey()};
        } else if (pTagAndLength.getTag() == EmvTags.AMOUNT_AUTHORISED_NUMERIC) {
            val = BytesUtils.fromString("00");
        } else if (pTagAndLength.getTag() == EmvTags.TERMINAL_TYPE) {
            val = new byte[]{(byte) 34};
        } else if (pTagAndLength.getTag() == EmvTags.TERMINAL_CAPABILITIES) {
            val = new byte[]{(byte) -32, (byte) -96, (byte) 0};
        } else if (pTagAndLength.getTag() == EmvTags.ADDITIONAL_TERMINAL_CAPABILITIES) {
            val = new byte[]{(byte) -114, (byte) 0, (byte) -80, (byte) 80, (byte) 5};
        } else if (pTagAndLength.getTag() == EmvTags.DS_REQUESTED_OPERATOR_ID) {
            val = BytesUtils.fromString("7345123215904501");
        } else if (pTagAndLength.getTag() == EmvTags.UNPREDICTABLE_NUMBER) {
            random.nextBytes(ret);
        }
        if (val != null) {
            System.arraycopy(val, 0, ret, 0, Math.min(val.length, ret.length));
        }
        return ret;
    }

    private EmvTerminal() {
    }
}
