package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser;

import android.util.Log;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums.CommandEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums.EmvCardScheme;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums.SwEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.exception.CommunicationException;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.EmvTags;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.EmvTerminal;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.TLV;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.TagAndLength;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.Afl;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.EmvCard;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.EmvTransactionRecord;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.enums.CurrencyEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils.CommandApdu;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils.ResponseUtils;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils.TlvUtil;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils.TrackUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import fr.devnied.bitlib.BytesUtils;

public class EmvParser {
    public static final String CARD_HOLDER_NAME_SEPARATOR = "/";
    private static final Logger LOGGER = LoggerFactory.getLogger(EmvParser.class);
    private static final byte[] PPSE = "2PAY.SYS.DDF01".getBytes();
    private static final byte[] PSE = "1PAY.SYS.DDF01".getBytes();
    public static final int UNKNOW = -1;
    private EmvCard card = new EmvCard();
    private boolean contactLess;
    private IProvider provider;
    private static final String utf = "UTF-8";

    public EmvParser(IProvider pProvider, boolean pContactLess) {
        this.provider = pProvider;
        this.contactLess = pContactLess;
    }

    public EmvCard readEmvCard() throws CommunicationException {
        try {
            if (!readWithPSE()) {
                Log.e("EmvParser", "<readEmvCard> enter readWithPSE true");
                readWithAID();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this.card;
    }

    protected byte[] selectPaymentEnvironment() throws CommunicationException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Select " + (this.contactLess ? "PPSE" : "PSE") + " Application");
        }
        return this.provider.transceive(new CommandApdu(CommandEnum.SELECT, this.contactLess ? PPSE : PSE, 0).toBytes());
    }

    protected int getLeftPinTry() throws CommunicationException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Get Left PIN try");
        }
        byte[] data = this.provider.transceive(new CommandApdu(CommandEnum.GET_DATA, 159, 23, 0).toBytes());
        if (!ResponseUtils.isSucceed(data)) {
            return -1;
        }
        byte[] val = TlvUtil.getValue(data, EmvTags.PIN_TRY_COUNTER);
        if (val != null) {
            return BytesUtils.byteArrayToInt(val);
        }
        return -1;
    }

    protected byte[] parseFCIProprietaryTemplate(byte[] pData) throws CommunicationException {
        byte[] data = TlvUtil.getValue(pData, EmvTags.SFI);
        if (data != null) {
            int sfi = BytesUtils.byteArrayToInt(data);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("SFI found:" + sfi);
            }
            data = this.provider.transceive(new CommandApdu(CommandEnum.READ_RECORD, sfi, (sfi << 3) | 4, 0).toBytes());
            if (ResponseUtils.isEquals(data, SwEnum.SW_6C)) {
                data = this.provider.transceive(new CommandApdu(CommandEnum.READ_RECORD, sfi, (sfi << 3) | 4, data[data.length - 1]).toBytes());
            }
            return data;
        } else if (!LOGGER.isDebugEnabled()) {
            return pData;
        } else {
            LOGGER.debug("(FCI) Issuer Discretionary Data is already present");
            return pData;
        }
    }

    protected String extractApplicationLabel(byte[] pData) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Extract Application label");
        }
        byte[] labelByte = TlvUtil.getValue(pData, EmvTags.APPLICATION_LABEL);
        if (labelByte != null) {
            return new String(labelByte);
        }
        return null;
    }

    protected boolean readWithPSE() throws CommunicationException, UnsupportedEncodingException {
        boolean ret = false;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Try to read card with Payment System Environment");
        }
        byte[] data = selectPaymentEnvironment();
        Log.e("EmvParser", "<readWithPSE> after select payment environment : " + new String(data, utf));
        if (ResponseUtils.isSucceed(data)) {
            Log.e("EmvParser", "<readWithPSE> is success ResponseUtils.isSucceed(data) 1");
            data = parseFCIProprietaryTemplate(data);
            Log.e("EmvParser", "<readWithPSE> is data paserFCI : " + new String(data, utf));
            if (ResponseUtils.isSucceed(data)) {
                Log.e("EmvParser", "<readWithPSE> is success ResponseUtils.isSucceed(data) 2");
                for (byte[] aid : getAids(data)) {
                    Log.e("EmvParser<readWithPSE>", "aid : " + new String(aid, utf));
                    ret = extractPublicData(aid, extractApplicationLabel(data));
                    ret = true;
                    if (ret) {
                        break;
                    }
                }
                if (!ret) {
                    this.card.setNfcLocked(true);
                }
            }
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug((this.contactLess ? "PPSE" : "PSE") + " not found -> Use kown AID");
        }
        return ret;
    }

    protected List<byte[]> getAids(byte[] pData) {
        List<byte[]> ret = new ArrayList();
        for (TLV tlv : TlvUtil.getlistTLV(pData, EmvTags.AID_CARD, EmvTags.KERNEL_IDENTIFIER)) {
            if (tlv.getTag() != EmvTags.KERNEL_IDENTIFIER || ret.size() == 0) {
                ret.add(tlv.getValueBytes());
            } else {
                ret.add(ArrayUtils.addAll((byte[]) ret.get(ret.size() - 1), tlv.getValueBytes()));
            }
        }
        return ret;
    }

    protected void readWithAID() throws CommunicationException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Try to read card with AID");
        }
        for (EmvCardScheme type : EmvCardScheme.values()) {
            byte[][] aidByte = type.getAidByte();
            int length = aidByte.length;
            int i = 0;
            while (i < length) {
                try {
                    Log.e("EmvParser", "<readWithAID> " + new String(aidByte[i], utf));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (!extractPublicData(aidByte[i], type.getName())) {
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    protected byte[] selectAID(byte[] pAid) throws CommunicationException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Select AID: " + BytesUtils.bytesToString(pAid));
        }
        return this.provider.transceive(new CommandApdu(CommandEnum.SELECT, pAid, 0).toBytes());
    }

    protected boolean extractPublicData(byte[] pAid, String pApplicationLabel) throws CommunicationException {
        boolean ret = false;
        byte[] data = selectAID(pAid);
        try {
            Log.e("EmvParser<extractPubD>", "data : " + new String(data, "UTF-16"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (ResponseUtils.isSucceed(data)) {
            ret = parse(data, this.provider);
            Log.e("EmvParser<extractPubD>", ret+"");
            if (ret) {
                String aid = BytesUtils.bytesToStringNoSpace(TlvUtil.getValue(data, EmvTags.DEDICATED_FILE_NAME));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Application label:" + pApplicationLabel + " with Aid:" + aid);
                }
                this.card.setAid(aid);
                this.card.setType(findCardScheme(aid, this.card.getCardNumber()));
                this.card.setApplicationLabel(pApplicationLabel);
                this.card.setLeftPinTry(getLeftPinTry());
            }
        } else {
            Log.e("EmvParser<extractPubD>", "else");
        }
        return ret;
    }

    protected EmvCardScheme findCardScheme(String pAid, String pCardNumber) {
        EmvCardScheme type = EmvCardScheme.getCardTypeByAid(pAid);
        if (type == EmvCardScheme.CB) {
            type = EmvCardScheme.getCardTypeByCardNumber(pCardNumber);
            if (type != null) {
                LOGGER.debug("Real type:" + type.getName());
            }
        }
        return type;
    }

    protected byte[] getLogEntry(byte[] pSelectResponse) {
        return TlvUtil.getValue(pSelectResponse, EmvTags.LOG_ENTRY, EmvTags.VISA_LOG_ENTRY);
    }

    protected boolean parse(byte[] pSelectResponse, IProvider pProvider) throws CommunicationException {
        boolean ret = false;
        byte[] logEntry = getLogEntry(pSelectResponse);
        byte[] gpo = getGetProcessingOptions(TlvUtil.getValue(pSelectResponse, EmvTags.PDOL), pProvider);
        if (!ResponseUtils.isSucceed(gpo)) {
            gpo = getGetProcessingOptions(null, pProvider);
            if (!ResponseUtils.isSucceed(gpo)) {
                return false;
            }
        }
        if (extractCommonsCardData(gpo)) {
            this.card.setListTransactions(extractLogEntry(logEntry));
            ret = true;
        }
        return ret;
    }

    protected boolean extractCommonsCardData(byte[] pGpo) throws CommunicationException {
        boolean ret = false;
        byte[] data = TlvUtil.getValue(pGpo, EmvTags.RESPONSE_MESSAGE_TEMPLATE_1);
        if (data != null) {
            data = ArrayUtils.subarray(data, 2, data.length);
        } else {
            ret = TrackUtils.extractTrack2Data(this.card, pGpo);
            if (ret) {
                extractCardHolderName(pGpo);
            } else {
                data = TlvUtil.getValue(pGpo, EmvTags.APPLICATION_FILE_LOCATOR);
            }
        }
        if (data == null) {
            return ret;
        }
        for (Afl afl : extractAfl(data)) {
            for (int index = afl.getFirstRecord(); index <= afl.getLastRecord(); index++) {
                byte[] info = this.provider.transceive(new CommandApdu(CommandEnum.READ_RECORD, index, (afl.getSfi() << 3) | 4, 0).toBytes());
                if (ResponseUtils.isEquals(info, SwEnum.SW_6C)) {
                    info = this.provider.transceive(new CommandApdu(CommandEnum.READ_RECORD, index, (afl.getSfi() << 3) | 4, info[info.length - 1]).toBytes());
                }
                if (ResponseUtils.isSucceed(info)) {
                    extractCardHolderName(info);
                    if (TrackUtils.extractTrack2Data(this.card, info)) {
                        return true;
                    }
                }
            }
        }
        return ret;
    }

    protected List<TagAndLength> getLogFormat() throws CommunicationException {
        List<TagAndLength> ret = new ArrayList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("GET log format");
        }
        byte[] data = this.provider.transceive(new CommandApdu(CommandEnum.GET_DATA, 159, 79, 0).toBytes());
        if (!ResponseUtils.isSucceed(data)) {
            return ret;
        }
        return TlvUtil.parseTagAndLength(TlvUtil.getValue(data, EmvTags.LOG_FORMAT));
    }

    protected List<EmvTransactionRecord> extractLogEntry(byte[] pLogEntry) throws CommunicationException {
        List<EmvTransactionRecord> listRecord = new ArrayList();
        if (pLogEntry != null) {
            List<TagAndLength> tals = getLogFormat();
            for (byte rec = (byte) 1; rec <= pLogEntry[1]; rec++) {
                byte[] response = this.provider.transceive(new CommandApdu(CommandEnum.READ_RECORD, rec, (pLogEntry[0] << 3) | 4, 0).toBytes());
                if (!ResponseUtils.isSucceed(response)) {
                    break;
                }
                EmvTransactionRecord record = new EmvTransactionRecord();
                record.parse(response, tals);
                if (record.getAmount().floatValue() >= 1.5E9f) {
                    record.setAmount(Float.valueOf(record.getAmount().floatValue() - 1.5E9f));
                }
                if (!(record.getAmount() == null || record.getAmount().floatValue() == 0.0f || record == null)) {
                    if (record.getCurrency() == null) {
                        record.setCurrency(CurrencyEnum.XXX);
                    }
                    listRecord.add(record);
                }
            }
        }
        return listRecord;
    }

    protected List<Afl> extractAfl(byte[] pAfl) {
        List<Afl> list = new ArrayList();
        ByteArrayInputStream bai = new ByteArrayInputStream(pAfl);
        while (bai.available() >= 4) {
            Afl afl = new Afl();
            afl.setSfi(bai.read() >> 3);
            afl.setFirstRecord(bai.read());
            afl.setLastRecord(bai.read());
            afl.setOfflineAuthentication(bai.read() == 1);
            list.add(afl);
        }
        return list;
    }

    protected void extractCardHolderName(byte[] pData) {
        byte[] cardHolderByte = TlvUtil.getValue(pData, EmvTags.CARDHOLDER_NAME);
        if (cardHolderByte != null) {
            String[] name = StringUtils.split(new String(cardHolderByte).trim(), CARD_HOLDER_NAME_SEPARATOR);
            if (name != null && name.length == 2) {
                this.card.setHolderFirstname(StringUtils.trimToNull(name[0]));
                this.card.setHolderLastname(StringUtils.trimToNull(name[1]));
            }
        }
    }

    protected byte[] getGetProcessingOptions(byte[] pPdol, IProvider pProvider) throws CommunicationException {
        List<TagAndLength> list = TlvUtil.parseTagAndLength(pPdol);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(EmvTags.COMMAND_TEMPLATE.getTagBytes());
            out.write(TlvUtil.getLength(list));
            if (list != null) {
                for (TagAndLength tl : list) {
                    out.write(EmvTerminal.constructValue(tl));
                }
            }
        } catch (Throwable ioe) {
            LOGGER.error("Construct GPO Command:" + ioe.getMessage(), ioe);
        }
        return pProvider.transceive(new CommandApdu(CommandEnum.GPO, out.toByteArray(), 0).toBytes());
    }

    public EmvCard getCard() {
        return this.card;
    }
}
