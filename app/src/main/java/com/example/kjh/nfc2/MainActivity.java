package com.example.kjh.nfc2;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private static String tagNum = null;
    private TextView tagDesc1;
    private TextView tagDesc2;

    private Tag mTag;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tagDesc1 = (TextView) findViewById(R.id.tagDesc1);
        tagDesc2 = (TextView) findViewById(R.id.tagDesc2);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }

    @Override
    protected void onPause() {
        if(nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        String msg = "";

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    msg = "" + messages[i].toString();
                }
                // Process the messages array.
            }
        }

        if (mTag != null) {
            byte[] tagId = mTag.getId();
            tagDesc1.setText("TagId: " + toHexString(tagId));
            tagNum = toHexString(tagId);    //https://stackoverflow.com/questions/42963329/attempt-to-invoke-virtual-method-void-android-nfc-tech-mifareclassic-connect 이 이유로 patchTag추가
        }
        if (mTag != null) {
            Log.e("BEFORE PATCHTAG", mTag.toString());
            mTag = patchTag(mTag);
            Log.e("PATCHTAG", mTag.toString());
            String content = readTag(mTag);

            tagDesc2.setText("Tag Content: " + content);
        }



    }

    public Tag patchTag(Tag oTag)
    {
        if (oTag == null)
            return null;

        String[] sTechList = oTag.getTechList();

        Parcel oParcel, nParcel;

        oParcel = Parcel.obtain();
        oTag.writeToParcel(oParcel, 0);
        oParcel.setDataPosition(0);

        int len = oParcel.readInt();
        byte[] id = null;
        if (len >= 0)
        {
            id = new byte[len];
            oParcel.readByteArray(id);
        }
        int[] oTechList = new int[oParcel.readInt()];
        oParcel.readIntArray(oTechList);
        Bundle[] oTechExtras = oParcel.createTypedArray(Bundle.CREATOR);
        int serviceHandle = oParcel.readInt();
        int isMock = oParcel.readInt();
        IBinder tagService;
        if (isMock == 0)
        {
            tagService = oParcel.readStrongBinder();
        }
        else
        {
            tagService = null;
        }
        oParcel.recycle();

        int nfca_idx=-1;
        int mc_idx=-1;

        for(int idx = 0; idx < sTechList.length; idx++)
        {
            if(sTechList[idx] == NfcA.class.getName())
            {
                Log.e("NFCA", "nfca.class getName enter");
                nfca_idx = idx;
            }
            else if(sTechList[idx] == MifareClassic.class.getName())
            {
                Log.e("ELIF NFCA", "MifareClassic.class.getName enter");
                mc_idx = idx;
            }
        }

        if(nfca_idx>=0&&mc_idx>=0&&oTechExtras[mc_idx]==null)
        {
            Log.e("oTECHEXTRAS", "enter");
            oTechExtras[mc_idx] = oTechExtras[nfca_idx];
        }
        else
        {
            Log.e("oTECHEXTRAS ELSE", "enter");
            return oTag;
        }

        nParcel = Parcel.obtain();
        nParcel.writeInt(id.length);
        nParcel.writeByteArray(id);
        nParcel.writeInt(oTechList.length);
        nParcel.writeIntArray(oTechList);
        nParcel.writeTypedArray(oTechExtras,0);
        nParcel.writeInt(serviceHandle);
        nParcel.writeInt(isMock);
        if(isMock==0)
        {
            nParcel.writeStrongBinder(tagService);
        }
        nParcel.setDataPosition(0);

        Tag nTag = Tag.CREATOR.createFromParcel(nParcel);

        nParcel.recycle();
        Log.e("PATCH LAST RET", "ret");
        return nTag;
    }
    public String readTag(Tag tag) {
        MifareUltralight mifare = MifareUltralight.get(tag);
        Log.e("MIFARE", mifare.toString());
        try {
            mifare.connect();
            byte[] payload = mifare.readPages(4);
            return new String(payload, Charset.forName("US-ASCII"));
        } catch (IOException e) {
            Log.e(TAG, "IOException while reading MifareUltralight message...", e);
        } finally {
            if (mifare != null) {
                try {
                    mifare.close();
                }
                catch (IOException e) {
                    Log.e(TAG, "Error closing tag...", e);
                }
            }
        }
        return null;
    }


    public static final String CHARS = "01233456789ABCDEF";
    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            sb.append(CHARS.charAt((data[i] >> 4) & 0x0F)).append(
                    CHARS.charAt(data[i] & 0x0F));
        }
        return sb.toString();
    }


}
