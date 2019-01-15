package com.example.kjh.nfc2;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;

import com.example.kjh.nfc2.Common.Constants;
import com.example.kjh.nfc2.Enum.EmvCardScheme;
import com.example.kjh.nfc2.Model.EmvCard;
import com.example.kjh.nfc2.Parser.EmvParser;
import com.example.kjh.nfc2.Utils.CardNfcUtils;
import com.example.kjh.nfc2.Utils.Provider;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class CardNfcMode {
    public final static String CARD_UNKNOWN = EmvCardScheme.UNKNOWN.toString();
    public final static String CARD_VISA = EmvCardScheme.VISA.toString();
    public final static String CARD_NAB_VISA = EmvCardScheme.NAB_VISA.toString();
    public final static String CARD_MASTER_CARD = EmvCardScheme.MASTER_CARD.toString();
    public final static String CARD_AMERICAN_EXPRESS = EmvCardScheme.AMERICAN_EXPRESS.toString();
    public final static String CARD_CB = EmvCardScheme.CB.toString();
    public final static String CARD_LINK = EmvCardScheme.LINK.toString();
    public final static String CARD_JCB = EmvCardScheme.JCB.toString();
    public final static String CARD_DANKORT = EmvCardScheme.DANKORT.toString();
    public final static String CARD_COGEBAN = EmvCardScheme.COGEBAN.toString();
    public final static String CARD_DISCOVER = EmvCardScheme.DISCOVER.toString();
    public final static String CARD_BANRISUL = EmvCardScheme.BANRISUL.toString();
    public final static String CARD_SPAN = EmvCardScheme.SPAN.toString();
    public final static String CARD_INTERAC = EmvCardScheme.INTERAC.toString();
    public final static String CARD_ZIP = EmvCardScheme.ZIP.toString();
    public final static String CARD_UNIONPAY = EmvCardScheme.UNIONPAY.toString();
    public final static String CARD_EAPS = EmvCardScheme.EAPS.toString();
    public final static String CARD_VERVE = EmvCardScheme.VERVE.toString();
    public final static String CARD_TENN = EmvCardScheme.TENN.toString();
    public final static String CARD_RUPAY = EmvCardScheme.RUPAY.toString();
    public final static String CARD_ПРО100 = EmvCardScheme.ПРО100.toString();
    public final static String CARD_ZKA = EmvCardScheme.ZKA.toString();
    public final static String CARD_BANKAXEPT = EmvCardScheme.BANKAXEPT.toString();
    public final static String CARD_BRADESCO = EmvCardScheme.BRADESCO.toString();
    public final static String CARD_MIDLAND = EmvCardScheme.MIDLAND.toString();
    public final static String CARD_PBS = EmvCardScheme.PBS.toString();
    public final static String CARD_ETRANZACT = EmvCardScheme.ETRANZACT.toString();
    public final static String CARD_GOOGLE = EmvCardScheme.GOOGLE.toString();
    public final static String CARD_INTER_SWITCH = EmvCardScheme.INTER_SWITCH.toString();

    private final static String NFC_A_TAG = "TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcA]";
    private final static String NFC_B_TAG = "TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcB]";
    private final String UNKNOWN_CARD_MESS =
            "===========================================================================\n\n"+
                    "Hi! This library is not familiar with your credit card. \n " +
                    "Please, write me an email with information of your bank: \n" +
                    "country, bank name, card type, etc) and i will try to do my best, \n" +
                    "to add your bank as a known one into this lib. \n" +
                    "Great thanks for using and reporting!!! \n" +
                    "Here is my email: pro100svitlo@gmail.com. \n\n" +
                    "===========================================================================";

    private Provider mProvider = new Provider();
    private boolean mException;
    private EmvCard mCard;
    private Tag mTag;
    private String mCardNumber;
    private String mExpireDate;
    private String mCardType;

    private Activity mActivity;
    private Context mContext;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private CardNfcUtils mCardNfcUtils;
    private byte[] result;  //test
    private final byte[] SELECT = {
            (byte) 0x80, // CLA Class
            0x04, // INS Instruction
            0x00, // P1  Parameter 1
            0x00, // P2  Parameter 2
            0x10  // LE  maximal number of bytes expected in result
    };

    public CardNfcMode(Activity activity, Context context) {
        this.mActivity = activity;
        this.mContext = context;
    }

    public int create() {
        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this.mContext);
            if (nfcAdapter != null) {
                Intent intent = new Intent(this.mContext, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pendingIntent = PendingIntent.getActivity(this.mContext, 0, intent, 0);
                mCardNfcUtils = new CardNfcUtils(this.mActivity);
                return 0;
            } else {
                return 2;
            }

        } catch(Exception e) {
            return 1;
        }
    }

    public int pause() {
        if(this.nfcAdapter != null) {
            mCardNfcUtils.disableDispatch();
            return 0;
        }
        //nfcAdapter is null
        return 1;

    }

    public int resume() {
        if (this.nfcAdapter != null) {
            mCardNfcUtils.enableDispatch();
            return 0;
        }
        //nfcAdapter is null
        return 1;
    }

    public byte[] setCardData(Intent intent) {
        this.mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.e("TAG", this.mTag.toString());
        //mFromStart = fromCreate
        try {
            if (mTag.toString().equals(NFC_A_TAG) || mTag.toString().equals(NFC_B_TAG)) {
                Log.e("EnterTag", "tag is A or B");
                IsoDep mIsoDep = IsoDep.get(this.mTag);
                if (mIsoDep == null) {
                    //return Constants.IsoDepNull;
                    return null;
                }
                this.mException = false;
                try {
                    Log.e("EnterIsoDep", "isodep ");
                    mIsoDep.connect();
                    //TEST
                    return mIsoDep.transceive(this.SELECT);


//                    mProvider.setmTagCom(mIsoDep);
//                    EmvParser parser = new EmvParser(mProvider, true);
//                    this.mCard = parser.readEmvCard();

                } catch(IOException e){
                    mException = true;
                } finally {
                    IOUtils.closeQuietly(mIsoDep);
                }

                if (mCard == null) {
                    //return Constants.UnknownCard;
                    return null;
                }
                if (mException) {
                    //return Constants.NotRecognize;
                    return null;
                }
                if (StringUtils.isNotBlank(mCard.getCardNumber())) {
                    Log.e("Enter", "isNotBlank");
                    mCardNumber = mCard.getCardNumber();
                    mExpireDate = mCard.getExpireDate();
                    mCardType = mCard.getType().toString();
                    if (mCardType.equals(EmvCardScheme.UNKNOWN.toString())){
                        Log.d("creditCardNfcReader", UNKNOWN_CARD_MESS);
                    }
                    return null;
                    //return Constants.SuccessNfc;
                } else if (mCard.isNfcLocked()) {
                    return null;
                    //return Constants.LockedNfc;
                }
                //return Constants.NotDitected;
                return null;
            } else {
                cleanAll();
                return null;
                //return Constants.UnknownTag;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
            //return Constants.NullPointErr;
        }
    }

    public Tag getTag() {
        return mTag;
    }

    public String getmCardNumber() {
        return mCardNumber;
    }

    private void cleanAll() {
        mCard = null;
        mTag = null;
        mCardNumber = null;
        mExpireDate = null;
        mCardType = null;
    }
}
