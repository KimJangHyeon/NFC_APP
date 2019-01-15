package com.example.kjh.nfc2;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
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

import com.example.kjh.nfc2.Common.Constants;
import com.example.kjh.nfc2.Enum.CommandEnum;
import com.example.kjh.nfc2.Parser.EmvParser;
import com.example.kjh.nfc2.Utils.CommandApdu;
import com.example.kjh.nfc2.Utils.Provider;
import com.example.kjh.nfc2.Utils.ResponseUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private static String tagNum = null;
    private TextView tagDesc1;
    private TextView tagDesc2;
    private CardNfcMode cm = new CardNfcMode(this, this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tagDesc1 = (TextView) findViewById(R.id.tagDesc1);
        tagDesc2 = (TextView) findViewById(R.id.tagDesc2);
        cm.create();
//        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }

    @Override
    protected void onPause() {
        if (cm.pause() != 0){
            Log.e("CMPAUSE", "error");
        };
//        if(nfcAdapter != null) {
//            nfcAdapter.disableForegroundDispatch(this);
//        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cm.resume() != 0) {
            Log.e("CMRESUME", "error");
        };
//        if (nfcAdapter != null) {
//            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("Before SETCARD", "howhow");
        //int ret = cm.setCardData(intent);
        byte[] ret = cm.setCardData(intent);
        String retStr = bytesToHex(ret);
        tagDesc2.setText(retStr);
//        Tag tag = cm.getTag();
//        tagDesc1.setText(tag.toString());
//        tagDesc2.setText(cm.getmCardNumber());
//        if (ret != Constants.SuccessNfc && ret != Constants.NotDitected) {
//            Log.e("Error", ret+"");
//        }



//        Tag mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//
//        String msg = "";
//
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
//            Parcelable[] rawMessages =
//                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//            if (rawMessages != null) {
//                NdefMessage[] messages = new NdefMessage[rawMessages.length];
//                for (int i = 0; i < rawMessages.length; i++) {
//                    msg = "" + messages[i].toString();
//                }
//                // Process the messages array.
//            }
//        }
//
//        if (mTag != null) {
//            byte[] tagId = mTag.getId();
//            tagDesc1.setText("TagId: " + toHexString(tagId));
//            tagNum = toHexString(tagId);
//        }
//        if (mTag != null) {
//            Log.e("BEFORE PATCHTAG", mTag.toString());
//            mTag = patchTag(mTag);
//            Log.e("PATCHTAG", mTag.toString());
//            //String content = readTag(mTag);
//            byte[] content = isoDepTag(mTag);
//            if (content == null) {
//                Log.e("ISODEP", "result is null!!");
//            }
//
//
//            tagDesc2.setText("Tag Content: " + content);
//        }
    }

//    public Tag patchTag(Tag oTag) {
//        if (oTag == null)
//            return null;
//
//        String[] sTechList = oTag.getTechList();
//
//        Parcel oParcel, nParcel;
//
//        oParcel = Parcel.obtain();
//        oTag.writeToParcel(oParcel, 0);
//        oParcel.setDataPosition(0);
//
//        int len = oParcel.readInt();
//        byte[] id = null;
//        if (len >= 0)
//        {
//            id = new byte[len];
//            oParcel.readByteArray(id);
//        }
//        int[] oTechList = new int[oParcel.readInt()];
//        oParcel.readIntArray(oTechList);
//        Bundle[] oTechExtras = oParcel.createTypedArray(Bundle.CREATOR);
//        int serviceHandle = oParcel.readInt();
//        int isMock = oParcel.readInt();
//        IBinder tagService;
//        if (isMock == 0)
//        {
//            tagService = oParcel.readStrongBinder();
//        }
//        else
//        {
//            tagService = null;
//        }
//        oParcel.recycle();
//
//        int nfca_idx=-1;
//        int mc_idx=-1;
//
//        for(int idx = 0; idx < sTechList.length; idx++)
//        {
//            if(sTechList[idx] == NfcA.class.getName())
//            {
//                Log.e("NFCA", "nfca.class getName enter");
//                nfca_idx = idx;
//            }
//            else if(sTechList[idx] == MifareClassic.class.getName())
//            {
//                Log.e("ELIF NFCA", "MifareClassic.class.getName enter");
//                mc_idx = idx;
//            }
//        }
//
//        if(nfca_idx>=0&&mc_idx>=0&&oTechExtras[mc_idx]==null)
//        {
//            Log.e("oTECHEXTRAS", "enter");
//            oTechExtras[mc_idx] = oTechExtras[nfca_idx];
//        }
//        else
//        {
//            Log.e("oTECHEXTRAS ELSE", "enter");
//            return oTag;
//        }
//
//        nParcel = Parcel.obtain();
//        nParcel.writeInt(id.length);
//        nParcel.writeByteArray(id);
//        nParcel.writeInt(oTechList.length);
//        nParcel.writeIntArray(oTechList);
//        nParcel.writeTypedArray(oTechExtras,0);
//        nParcel.writeInt(serviceHandle);
//        nParcel.writeInt(isMock);
//        if(isMock==0)
//        {
//            nParcel.writeStrongBinder(tagService);
//        }
//        nParcel.setDataPosition(0);
//
//        Tag nTag = Tag.CREATOR.createFromParcel(nParcel);
//
//        nParcel.recycle();
//        Log.e("PATCH LAST RET", "ret");
//        return nTag;
//    }
//    public String readTag(Tag tag) {
//        MifareUltralight mifare = MifareUltralight.get(tag);
//        Log.e("MIFARE", mifare.toString());
//        try {
//            mifare.connect();
//            byte[] payload = mifare.readPages(4);
//            return new String(payload, Charset.forName("US-ASCII"));
//        } catch (IOException e) {
//            Log.e(TAG, "IOException while reading MifareUltralight message...", e);
//        } finally {
//            if (mifare != null) {
//                try {
//                    mifare.close();
//                }
//                catch (IOException e) {
//                    Log.e(TAG, "Error closing tag...", e);
//                }
//            }
//        }
//        return null;
//    }
//
//    public byte[] isoDepTag(Tag tag) {
//        byte[] result;
//        mProvider.getLog().setLength(0);
//        IsoDep mIsoDep = IsoDep.get(tag);
//
//        if (mIsoDep == null) {
//            Log.e("ISODEP", "IsoDep is null");
//            return null;
//        }
//
//
//        try {
//            contactLess = true;
//            mIsoDep.connect();
//            mProvider.setmTagCom(mIsoDep);
//            EmvParser parser = new EmvParser(mProvider, true);
//
//            result = mIsoDep.transceive(new CommandApdu(CommandEnum.SELECT, contactLess ? PPSE : PSE, 0).toBytes());
//            Log.e("ISODEP RESULT LEN", result.length + "");
//            Log.e("ISODEP RESULT", result.toString());
//            return result;
//        } catch(IOException e) {
//            Log.e("ISODEP ERR", e.getMessage());
//        }
//        return null;
//    }


    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        Log.e("BYTELEN", bytes.length+"");
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            Log.e("ONEBYTE", v+"");

            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
