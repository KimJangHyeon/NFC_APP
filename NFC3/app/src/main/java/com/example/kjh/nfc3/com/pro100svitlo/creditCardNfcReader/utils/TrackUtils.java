package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.EmvTags;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.EmvCard;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.Service;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser.EmvParser;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TrackUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackUtils.class);
    private static final Pattern TRACK2_PATTERN = Pattern.compile("([0-9]{1,19})D([0-9]{4})([0-9]{3})?(.*)");

    public static boolean extractTrack2Data(EmvCard pEmvCard, byte[] pData) {
        byte[] track2 = TlvUtil.getValue(pData, EmvTags.TRACK_2_EQV_DATA, EmvTags.TRACK2_DATA);
        if (track2 == null) {
            return false;
        }
        Matcher m = TRACK2_PATTERN.matcher(BytesUtils.bytesToStringNoSpace(track2));
        if (!m.find()) {
            return false;
        }
        pEmvCard.setCardNumber(m.group(1));
        String month = m.group(2).substring(2, 4);
        pEmvCard.setExpireDate(month + EmvParser.CARD_HOLDER_NAME_SEPARATOR + m.group(2).substring(0, 2));
        pEmvCard.setService(new Service(m.group(3)));
        return true;
    }

    private TrackUtils() {
    }
}
