package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils;

import android.nfc.tech.IsoDep;
import android.util.Log;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums.SwEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.exception.CommunicationException;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser.IProvider;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class Provider implements IProvider {
    private static final String TAG = Provider.class.getName();
    private StringBuffer log = new StringBuffer();
    private IsoDep mTagCom;

    public void setmTagCom(IsoDep mTagCom) {
        this.mTagCom = mTagCom;
    }

    public StringBuffer getLog() {
        return this.log;
    }

    public byte[] transceive(byte[] pCommand) throws CommunicationException {
        this.log.append("=================<br/>");
        this.log.append("<font color='green'><b>send:</b> " + BytesUtils.bytesToString(pCommand)).append("</font><br/>");
        try {
            byte[] response = this.mTagCom.transceive(pCommand);
            this.log.append("<font color='blue'><b>resp:</b> " + BytesUtils.bytesToString(response)).append("</font><br/>");
            Log.d(TAG, "resp: " + BytesUtils.bytesToString(response));
            try {
                Log.d(TAG, "resp: " + TlvUtil.prettyPrintAPDUResponse(response));
                SwEnum val = SwEnum.getSW(response);
                if (val != null) {
                    Log.d(TAG, "resp: " + val.getDetail());
                }
                this.log.append("<pre>").append(TlvUtil.prettyPrintAPDUResponse(response).replace(IOUtils.LINE_SEPARATOR_UNIX, "<br/>").replace(" ", "&nbsp;")).append("</pre><br/>");
            } catch (Exception e) {
            }
            return response;
        } catch (IOException e2) {
            throw new CommunicationException(e2.getMessage());
        }
    }
}
