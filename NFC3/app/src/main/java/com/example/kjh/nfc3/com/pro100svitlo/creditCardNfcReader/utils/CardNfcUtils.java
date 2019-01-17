package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;

public class CardNfcUtils {
    private static final IntentFilter[] INTENT_FILTER = new IntentFilter[]{new IntentFilter("android.nfc.action.TECH_DISCOVERED"), new IntentFilter("android.nfc.action.TAG_DISCOVERED")};
    private static final String[][] TECH_LIST;
    private final Activity mActivity;
    private final NfcAdapter mNfcAdapter;
    private final PendingIntent mPendingIntent;

    static {
        String[][] strArr = new String[1][];
        strArr[0] = new String[]{NfcA.class.getName(), IsoDep.class.getName()};
        TECH_LIST = strArr;
    }

    public CardNfcUtils(Activity pActivity) {
        this.mActivity = pActivity;
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this.mActivity);
        this.mPendingIntent = PendingIntent.getActivity(this.mActivity, 0, new Intent(this.mActivity, this.mActivity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    public void disableDispatch() {
        if (this.mNfcAdapter != null) {
            this.mNfcAdapter.disableForegroundDispatch(this.mActivity);
        }
    }

    public void enableDispatch() {
        if (this.mNfcAdapter != null) {
            this.mNfcAdapter.enableForegroundDispatch(this.mActivity, this.mPendingIntent, INTENT_FILTER, TECH_LIST);
        }
    }
}
