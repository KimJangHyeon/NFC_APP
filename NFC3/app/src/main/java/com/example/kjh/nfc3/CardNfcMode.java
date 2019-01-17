package com.example.kjh.nfc3;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

public class CardNfcMode {
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

    private boolean mException;
    private Tag mTag;
    private String mCardNumber;
    private String mExpireDate;
    private String mCardType;

    private Activity mActivity;
    private Context mContext;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
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
            nfcAdapter.disableForegroundDispatch(this.mActivity);
            return 0;
        }
        //nfcAdapter is null
        return 1;

    }

    public int resume() {
        if (this.nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this.mActivity, this.pendingIntent, null, null);
            return 0;
        }
        //nfcAdapter is null
        return 1;
    }

    public Tag getTag() {
        return mTag;
    }

    public String getmCardNumber() {
        return mCardNumber;
    }

    private void cleanAll() {
        mTag = null;
        mCardNumber = null;
        mExpireDate = null;
        mCardType = null;
    }
}
