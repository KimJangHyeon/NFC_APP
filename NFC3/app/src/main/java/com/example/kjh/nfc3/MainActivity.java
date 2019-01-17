package com.example.kjh.nfc3;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils;
import com.example.kjh.nfc3.com.pro100svitlo.nfccardread.C0235R;

public class MainActivity extends AppCompatActivity implements CardNfcAsyncTask.CardNfcInterface {

    private CardNfcAsyncTask mCardNfcAsyncTask;
    private CardNfcUtils mCardNfcUtils;
    private TextView mCardNumberText;
    private LinearLayout mCardReadyContent;
    private String mCardWithLockedNfcMessage;
    private String mDoNotMoveCardMessage;
    private TextView mExpireDateText;
    private boolean mIntentFromCreate;
    private boolean mIsScanNow;
    private NfcAdapter mNfcAdapter;
    private TextView mPutCardContent;
    private TextView mPutTagContent;
    private String mUnknownEmvCardMessage;
    //private ProgressDialog mProgressDialog;
    //private Toolbar mToolbar;
    //private AlertDialog mTurnNfcDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (this.mNfcAdapter == null) {
            this.mPutTagContent = (TextView) findViewById(R.id.txt_tag);
            this.mPutTagContent.setText("no NfcAdapter");
            return;
        }

        this.mCardNfcUtils = new CardNfcUtils(this);
        this.mPutTagContent = (TextView) findViewById(R.id.txt_tag);
        this.mPutCardContent = (TextView) findViewById(R.id.txt_card);
        this.mExpireDateText = (TextView) findViewById(R.id.txt_date);

        onNewIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mIntentFromCreate = false;
        if (this.mNfcAdapter != null && !this.mNfcAdapter.isEnabled()) {
           //showTurnOnNfcDialog();
            //this.mPutCardContent.setVisibility();
        } else if (this.mNfcAdapter != null) {
//            if (!this.mIsScanNow) {
////                //this.mPutCardContent.setVisibility(0);
////                //this.mCardReadyContent.setVisibility(8);
////            }
            this.mCardNfcUtils.enableDispatch();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.mNfcAdapter != null) {
            this.mCardNfcUtils.disableDispatch();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (this.mNfcAdapter != null && this.mNfcAdapter.isEnabled()) {
            this.mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, this.mIntentFromCreate).build();
        }
    }

    @Override
    public void cardIsReadyToRead() {
        Log.e("MainActivity", "enter card is ready to read");
        String card = getPrettyCardNumber(this.mCardNfcAsyncTask.getCardNumber());
        String expiredDate = this.mCardNfcAsyncTask.getCardExpireDate();
        String cardType = this.mCardNfcAsyncTask.getCardType();
        this.mCardNumberText.setText(card);
        this.mExpireDateText.setText(expiredDate);
        parseCardType(cardType);
    }


    @Override
    public void cardWithLockedNfc() {
        Log.e("MainActivity", "card with locked nfc");
    }

    @Override
    public void doNotMoveCardSoFast() {
        Log.e("MainActivity", "do not move card so fast");
    }

    @Override
    public void finishNfcReadCard() {
        this.mCardNfcAsyncTask = null;
        this.mIsScanNow = false;
    }

    @Override
    public void startNfcReadCard() {
        Log.e("MainActivity", "start nfc read card");
    }

    @Override
    public void unknownEmvCard() {
        Log.e("MainActivity", "unknown emv card");
    }

    private String getPrettyCardNumber(String card) {
        String div = " - ";
        return card.substring(0, 4) + div + card.substring(4, 8) + div + card.substring(8, 12) + div + card.substring(12, 16);
    }

    private void parseCardType(String cardType) {
        if (cardType.equals(CardNfcAsyncTask.CARD_UNKNOWN)) {
            //Snackbar.make(this.mToolbar, getString(C0235R.string.snack_unknown_bank_card), 0).setAction((CharSequence) "GO", new C02343());
            Log.e("MainActivity", "parseCardType: unknown card");
        } else if (cardType.equals(CardNfcAsyncTask.CARD_VISA)) {
            Log.e("MainActivity", "parseCardType: VISA");
        } else if (cardType.equals(CardNfcAsyncTask.CARD_MASTER_CARD)) {
            Log.e("MainActivity", "parseCardType: MASTER");
        }
    }

}
