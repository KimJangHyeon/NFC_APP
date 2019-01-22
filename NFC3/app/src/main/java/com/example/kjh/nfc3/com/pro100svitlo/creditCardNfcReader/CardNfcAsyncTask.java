package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader;

import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.util.Log;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums.EmvCardScheme;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.EmvCard;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser.EmvParser;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils.Provider;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class CardNfcAsyncTask extends AsyncTask<Void, Void, Object> {
    public static final String CARD_AMERICAN_EXPRESS = EmvCardScheme.AMERICAN_EXPRESS.toString();
    public static final String CARD_BANKAXEPT = EmvCardScheme.BANKAXEPT.toString();
    public static final String CARD_BANRISUL = EmvCardScheme.BANRISUL.toString();
    public static final String CARD_BRADESCO = EmvCardScheme.BRADESCO.toString();
    public static final String CARD_CB = EmvCardScheme.CB.toString();
    public static final String CARD_COGEBAN = EmvCardScheme.COGEBAN.toString();
    public static final String CARD_DANKORT = EmvCardScheme.DANKORT.toString();
    public static final String CARD_DISCOVER = EmvCardScheme.DISCOVER.toString();
    public static final String CARD_EAPS = EmvCardScheme.EAPS.toString();
    public static final String CARD_ETRANZACT = EmvCardScheme.ETRANZACT.toString();
    public static final String CARD_GOOGLE = EmvCardScheme.GOOGLE.toString();
    public static final String CARD_INTERAC = EmvCardScheme.INTERAC.toString();
    public static final String CARD_INTER_SWITCH = EmvCardScheme.INTER_SWITCH.toString();
    public static final String CARD_JCB = EmvCardScheme.JCB.toString();
    public static final String CARD_LINK = EmvCardScheme.LINK.toString();
    public static final String CARD_MASTER_CARD = EmvCardScheme.MASTER_CARD.toString();
    public static final String CARD_MIDLAND = EmvCardScheme.MIDLAND.toString();
    public static final String CARD_NAB_VISA = EmvCardScheme.NAB_VISA.toString();
    public static final String CARD_PBS = EmvCardScheme.PBS.toString();
    public static final String CARD_RUPAY = EmvCardScheme.RUPAY.toString();
    public static final String CARD_SPAN = EmvCardScheme.SPAN.toString();
    public static final String CARD_TENN = EmvCardScheme.TENN.toString();
    public static final String CARD_UNIONPAY = EmvCardScheme.UNIONPAY.toString();
    public static final String CARD_UNKNOWN = EmvCardScheme.UNKNOWN.toString();
    public static final String CARD_VERVE = EmvCardScheme.VERVE.toString();
    public static final String CARD_VISA = EmvCardScheme.VISA.toString();
    public static final String CARD_ZIP = EmvCardScheme.ZIP.toString();
    public static final String CARD_ZKA = EmvCardScheme.ZKA.toString();
    /* renamed from: CARD_ПРО100 */
    public static final String f4CARD_100 = EmvCardScheme.f5100.toString();
    private static final String NFC_A_TAG = "TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcA]";
    private static final String NFC_B_TAG = "TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcB]";
    private final String UNKNOWN_CARD_MESS;
    private EmvCard mCard;
    private String mCardNumber;
    private String mCardType;
    private boolean mException;
    private String mExpireDate;
    private CardNfcInterface mInterface;
    private Provider mProvider;
    private Tag mTag;

    public static class Builder {
        private boolean mFromStart;
        private CardNfcInterface mInterface;
        private Tag mTag;

        public Builder(CardNfcInterface i, Intent intent, boolean fromCreate) {
            this.mInterface = i;
            this.mTag = (Tag) intent.getParcelableExtra("android.nfc.extra.TAG");
            this.mFromStart = fromCreate;
        }

        public CardNfcAsyncTask build() {
            return new CardNfcAsyncTask(this);
        }
    }

    public interface CardNfcInterface {
        void cardIsReadyToRead();

        void cardWithLockedNfc();

        void doNotMoveCardSoFast();

        void finishNfcReadCard();

        void startNfcReadCard();

        void unknownEmvCard();
    }

    private CardNfcAsyncTask(Builder b) {
        this.UNKNOWN_CARD_MESS = "===========================================================================\n\nHi! This library is not familiar with your credit card. \n Please, write me an email with information of your bank: \ncountry, bank name, card type, etc) and i will try to do my best, \nto add your bank as a known one into this lib. \nGreat thanks for using and reporting!!! \nHere is my email: pro100svitlo@gmail.com. \n\n===========================================================================";
        this.mProvider = new Provider();
        this.mTag = b.mTag;
        if (this.mTag != null) {
            this.mInterface = b.mInterface;
            try {
                if (this.mTag.toString().equals(NFC_A_TAG) || this.mTag.toString().equals(NFC_B_TAG)) {
                    execute();
                    return;
                }
                if (!b.mFromStart) {
                    this.mInterface.unknownEmvCard();
                }
                clearAll();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public String getCardNumber() {
        return this.mCardNumber;
    }

    public String getCardExpireDate() {
        return this.mExpireDate;
    }

    public String getCardType() {
        return this.mCardType;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        this.mInterface.startNfcReadCard();
        this.mProvider.getLog().setLength(0);
    }

    protected Object doInBackground(Void... params) {
        try {
            doInBackground();
            return null;
        } catch (Exception e) {
            Exception result = e;
            Log.e(CardNfcAsyncTask.class.getName(), e.getMessage(), e);
            return result;
        }
    }

    protected void onPostExecute(Object result) {
        if (this.mException) {
            this.mInterface.doNotMoveCardSoFast();
        } else if (this.mCard == null) {
            this.mInterface.unknownEmvCard();
        } else if (StringUtils.isNotBlank(this.mCard.getCardNumber())) {
            this.mCardNumber = this.mCard.getCardNumber();
            this.mExpireDate = this.mCard.getExpireDate();
            this.mCardType = this.mCard.getType().toString();
            if (this.mCardType.equals(EmvCardScheme.UNKNOWN.toString())) {
                Log.d("creditCardNfcReader", "===========================================================================\n\nHi! This library is not familiar with your credit card. \n Please, write me an email with information of your bank: \ncountry, bank name, card type, etc) and i will try to do my best, \nto add your bank as a known one into this lib. \nGreat thanks for using and reporting!!! \nHere is my email: pro100svitlo@gmail.com. \n\n===========================================================================");
            }
            this.mInterface.cardIsReadyToRead();
        } else if (this.mCard.isNfcLocked()) {
            this.mInterface.cardWithLockedNfc();
        }
        this.mInterface.finishNfcReadCard();
        clearAll();
    }

    private void doInBackground() {
        IsoDep mIsoDep = IsoDep.get(this.mTag);
        if (mIsoDep == null) {
            this.mInterface.doNotMoveCardSoFast();
            return;
        }
        this.mException = false;
        try {
            mIsoDep.connect();
            this.mProvider.setmTagCom(mIsoDep);
            this.mCard = new EmvParser(this.mProvider, true).readEmvCard();
        } catch (IOException e) {
            this.mException = true;
        } finally {
            IOUtils.closeQuietly(mIsoDep);
        }
    }

    private void clearAll() {
        this.mInterface = null;
        this.mProvider = null;
        this.mCard = null;
        this.mTag = null;
        this.mCardNumber = null;
        this.mExpireDate = null;
        this.mCardType = null;
    }
}
