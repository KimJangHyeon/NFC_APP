package com.example.kjh.nfc3;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils;
import com.example.kjh.nfc3.com.pro100svitlo.nfccardread.C0235R;

public class MainActivity extends AppCompatActivity implements CardNfcAsyncTask.CardNfcInterface {
    //private ImageView mCardLogoIcon;
    private CardNfcAsyncTask mCardNfcAsyncTask;
    private CardNfcUtils mCardNfcUtils;
    private TextView mCardNumberText;
    //private LinearLayout mCardReadyContent;
    private String mCardWithLockedNfcMessage;
    private String mDoNotMoveCardMessage;
    private TextView mExpireDateText;
    private boolean mIntentFromCreate;
    private boolean mIsScanNow;
    private NfcAdapter mNfcAdapter;
    private ProgressDialog mProgressDialog;
    //private TextView mPutCardContent;
    private Toolbar mToolbar;
    private AlertDialog mTurnNfcDialog;
    private String mUnknownEmvCardMessage;

    /* renamed from: com.pro100svitlo.nfccardread.MainActivity$1 */
    class C02321 implements DialogInterface.OnClickListener {
        C02321() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            MainActivity.this.onBackPressed();
        }
    }

    /* renamed from: com.pro100svitlo.nfccardread.MainActivity$2 */
    class C02332 implements DialogInterface.OnClickListener {
        C02332() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (Build.VERSION.SDK_INT >= 16) {
                MainActivity.this.startActivity(new Intent("android.settings.NFC_SETTINGS"));
            } else {
                MainActivity.this.startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
            }
        }
    }

    /* renamed from: com.pro100svitlo.nfccardread.MainActivity$3 */
    class C02343 implements View.OnClickListener {
        C02343() {
        }

        public void onClick(View v) {
            MainActivity.this.goToRepo();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView((int) C0235R.layout.activity_main);
//        this.mToolbar = (Toolbar) findViewById(C0235R.id.toolbar);
//        setSupportActionBar(this.mToolbar);
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (this.mNfcAdapter == null) {
            return;
        }
        this.mCardNfcUtils = new CardNfcUtils(this);
//        this.mPutCardContent = (TextView) findViewById(C0235R.id.content_putCard);
//        this.mCardReadyContent = (LinearLayout) findViewById(C0235R.id.content_cardReady);
//        this.mCardNumberText = (TextView) findViewById(16908308);
//        this.mExpireDateText = (TextView) findViewById(16908309);
//        this.mCardLogoIcon = (ImageView) findViewById(16908294);
        createProgressDialog();
        initNfcMessages();
        this.mIntentFromCreate = true;
        onNewIntent(getIntent());
    }

    protected void onResume() {
        super.onResume();
        this.mIntentFromCreate = false;
        if (this.mNfcAdapter != null && !this.mNfcAdapter.isEnabled()) {
            showTurnOnNfcDialog();
            //this.mPutCardContent.setVisibility(8);
        } else if (this.mNfcAdapter != null) {
            if (!this.mIsScanNow) {
                //this.mPutCardContent.setVisibility(0);
                //this.mCardReadyContent.setVisibility(8);
            }
            this.mCardNfcUtils.enableDispatch();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.mNfcAdapter != null) {
            this.mCardNfcUtils.disableDispatch();
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (this.mNfcAdapter != null && this.mNfcAdapter.isEnabled()) {
            this.mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, this.mIntentFromCreate).build();
        }
    }

    public void startNfcReadCard() {
        this.mIsScanNow = true;
        Log.e("MainActivity", "startNfcReadCard");
        this.mProgressDialog.show();
    }

    public void cardIsReadyToRead() {
        //this.mPutCardContent.setVisibility(8);
        //this.mCardReadyContent.setVisibility(0);
        String card = getPrettyCardNumber(this.mCardNfcAsyncTask.getCardNumber());
        String expiredDate = this.mCardNfcAsyncTask.getCardExpireDate();
        String cardType = this.mCardNfcAsyncTask.getCardType();
        Log.e("MainActivity<cardIRTR>", card);
        Toast.makeText(getApplicationContext(), card, Toast.LENGTH_LONG).show();
        //this.mCardNumberText.setText(card);
        //this.mExpireDateText.setText(expiredDate);
        parseCardType(cardType);
    }

    public void doNotMoveCardSoFast() {
        Log.e("MainActivity<doNMCSF>", "do not move card so fast");
        showSnackBar(this.mDoNotMoveCardMessage);
    }

    public void unknownEmvCard() {
        Log.e("MainActivity<unknownEC>", "unknownEmvCard");
        showSnackBar(this.mUnknownEmvCardMessage);
    }

    public void cardWithLockedNfc() {
        Log.e("MainActivity<cardWLN>", "cardWithLockedNfc");
        showSnackBar(this.mCardWithLockedNfcMessage);
    }

    public void finishNfcReadCard() {
        Log.e("MainActivity<finishNRC>", "finishNfcReadCard");
        this.mProgressDialog.dismiss();
        this.mCardNfcAsyncTask = null;
        this.mIsScanNow = false;
    }

    private void createProgressDialog() {
        String title = getString(R.string.ad_progressBar_title);
        String mess = getString(R.string.ad_progressBar_mess);
        this.mProgressDialog = new ProgressDialog(this);
        this.mProgressDialog.setTitle(title);
        this.mProgressDialog.setMessage(mess);
        this.mProgressDialog.setIndeterminate(true);
        this.mProgressDialog.setCancelable(false);
    }

    private void showSnackBar(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showTurnOnNfcDialog() {
        if (this.mTurnNfcDialog == null) {
            CharSequence title = getString(R.string.ad_nfcTurnOn_title);
            CharSequence mess = getString(R.string.ad_nfcTurnOn_message);
            CharSequence pos = getString(R.string.ad_nfcTurnOn_pos);
            this.mTurnNfcDialog = new AlertDialog.Builder(this).setTitle(title).setMessage(mess).setPositiveButton(pos, new C02332()).setNegativeButton(getString(R.string.ad_nfcTurnOn_neg), new C02321()).create();
        }
        this.mTurnNfcDialog.show();
    }

    private void initNfcMessages() {
        this.mDoNotMoveCardMessage = getString(R.string.snack_doNotMoveCard);
        this.mCardWithLockedNfcMessage = getString(R.string.snack_lockedNfcCard);
        this.mUnknownEmvCardMessage = getString(R.string.snack_unknownEmv);
    }

    private void parseCardType(String cardType) {
        if (cardType.equals(CardNfcAsyncTask.CARD_UNKNOWN)) {
            Toast.makeText(getApplicationContext(), getString(R.string.snack_unknown_bank_card), Toast.LENGTH_LONG).show();
            //Snackbar.make(this.mToolbar, getString(R.string.snack_unknown_bank_card), 0).setAction((CharSequence) "GO", new C02343());
        } else if (cardType.equals(CardNfcAsyncTask.CARD_VISA)) {
            //this.mCardLogoIcon.setImageResource(C0235R.mipmap.visa_logo);
        } else if (cardType.equals(CardNfcAsyncTask.CARD_MASTER_CARD)) {
            //this.mCardLogoIcon.setImageResource(C0235R.mipmap.master_logo);
        }
    }

    private String getPrettyCardNumber(String card) {
        String div = " - ";
        return card.substring(0, 4) + div + card.substring(4, 8) + div + card.substring(8, 12) + div + card.substring(12, 16);
    }

    private void goToRepo() {
        Intent i = new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.repoUrl)));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setPackage("com.android.chrome");
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            i.setPackage(null);
            startActivity(i);
        }
    }
}
