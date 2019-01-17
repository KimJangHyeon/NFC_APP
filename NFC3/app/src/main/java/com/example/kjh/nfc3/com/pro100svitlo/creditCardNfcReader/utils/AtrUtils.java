package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils;

import android.util.Log;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

public final class AtrUtils {
    private static final MultiMap<String, String> MAP = new MultiValueMap();
    private static String TAG = "creditCardNfcReader";

    static {
        Exception e;
        Throwable th;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            is = AtrUtils.class.getResourceAsStream("/smartcard_list.txt");
            InputStreamReader isr2 = new InputStreamReader(is, CharEncoding.UTF_8);
            try {
                BufferedReader br2 = new BufferedReader(isr2);
                int lineNumber = 0;
                String currentATR = null;
                while (true) {
                    try {

                        String line = br2.readLine();
                        if (line != null) {
                            lineNumber++;
                            if (!(line.startsWith("#") || line.trim().length() == 0)) {
                                if (line.startsWith("\t") && currentATR != null) {
                                    MAP.put(currentATR, line.replace("\t", "").trim());
                                } else if (line.startsWith("3")) {
                                    currentATR = StringUtils.deleteWhitespace(line.toUpperCase());
                                } else {
                                    Log.d(TAG, "Encountered unexpected line in atr list: currentATR=" + currentATR + " Line(" + lineNumber + ") = " + line);
                                }
                            }
                        } else {
                            IOUtils.closeQuietly(br2);
                            IOUtils.closeQuietly(isr2);
                            IOUtils.closeQuietly(is);

                        }
                    } catch (IOException e2) {
                        e = e2;
                        br = br2;
                        isr = isr2;
                    } catch (Throwable th2) {
                        th = th2;
                        br = br2;
                        isr = isr2;
                    }
                }
            } catch (Exception e3) {
                e = e3;
                isr = isr2;
                try {
                    throw new RuntimeException(e);
                } catch (Throwable th3) {
                    th = th3;
                    IOUtils.closeQuietly(br);
                    IOUtils.closeQuietly(isr);
                    IOUtils.closeQuietly(is);
                    //throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                isr = isr2;
                IOUtils.closeQuietly(br);
                IOUtils.closeQuietly(isr);
                IOUtils.closeQuietly(is);
                //throw th;
            }
        } catch (IOException e4) {
            e = e4;
            throw new RuntimeException(e);
        }
    }

    public static final Collection<String> getDescription(String pAtr) {
        if (!StringUtils.isNotBlank(pAtr)) {
            return null;
        }
        String val = StringUtils.deleteWhitespace(pAtr);
        for (String key : MAP.keySet()) {
            if (val.matches("^" + key + "$")) {
                return (Collection) MAP.get(key);
            }
        }
        return null;
    }

    public static final Collection<String> getDescriptionFromAts(String pAts) {
        if (!StringUtils.isNotBlank(pAts)) {
            return null;
        }
        String val = StringUtils.deleteWhitespace(pAts);
        for (String key : MAP.keySet()) {
            if (key.contains(val)) {
                return (Collection) MAP.get(key);
            }
        }
        return null;
    }

    private AtrUtils() {
    }
}
