package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums.SwEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums.TagValueTypeEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.exception.TlvException;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.EmvTags;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.ITag;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.TLV;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.TagAndLength;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class TlvUtil {
    private static final int KEYCODE_MEDIA_PAUSE = 127;  //import com.example.kjh.nfc3.android.support.v4.media.TransportMediator;

    private static ITag searchTagById(byte[] tagIdBytes) {
        return EmvTags.getNotNull(tagIdBytes);
    }

    private static ITag searchTagById(ByteArrayInputStream stream) {
        return searchTagById(readTagIdBytes(stream));
    }

    public static String getFormattedTagAndLength(byte[] data, int indentLength) {
        StringBuilder buf = new StringBuilder();
        String indent = getSpaces(indentLength);
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        boolean firstLine = true;
        while (stream.available() > 0) {
            if (firstLine) {
                firstLine = false;
            } else {
                buf.append(IOUtils.LINE_SEPARATOR_UNIX);
            }
            buf.append(indent);
            ITag tag = searchTagById(stream);
            int length = readTagLength(stream);
            buf.append(prettyPrintHex(tag.getTagBytes()));
            buf.append(" ");
            buf.append(String.format("%02x", new Object[]{Integer.valueOf(length)}));
            buf.append(" -- ");
            buf.append(tag.getName());
        }
        return buf.toString();
    }

    public static byte[] readTagIdBytes(ByteArrayInputStream stream) {
        ByteArrayOutputStream tagBAOS = new ByteArrayOutputStream();
        byte tagFirstOctet = (byte) stream.read();
        tagBAOS.write(tagFirstOctet);
        if ((tagFirstOctet & 31) == 31) {
            while (true) {
                int nextOctet = stream.read();
                if (nextOctet >= 0) {
                    byte tlvIdNextOctet = (byte) nextOctet;
                    tagBAOS.write(tlvIdNextOctet);
                    if (BytesUtils.matchBitByBitIndex(tlvIdNextOctet, 7)) {
                        if (BytesUtils.matchBitByBitIndex(tlvIdNextOctet, 7) && (tlvIdNextOctet & KEYCODE_MEDIA_PAUSE) == 0) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                break;
            }
        }
        return tagBAOS.toByteArray();
    }

    public static int readTagLength(ByteArrayInputStream stream) {
        int tmpLength = stream.read();
        if (tmpLength < 0) {
            throw new TlvException("Negative length: " + tmpLength);
        } else if (tmpLength <= KEYCODE_MEDIA_PAUSE) {
            return tmpLength;
        } else {
            if (tmpLength == 128) {
                return tmpLength;
            }
            int numberOfLengthOctets = tmpLength & KEYCODE_MEDIA_PAUSE;
            tmpLength = 0;
            for (int i = 0; i < numberOfLengthOctets; i++) {
                int nextLengthOctet = stream.read();
                if (nextLengthOctet < 0) {
                    throw new TlvException("EOS when reading length bytes");
                }
                tmpLength = (tmpLength << 8) | nextLengthOctet;
            }
            return tmpLength;
        }
    }

    public static TLV getNextTLV(ByteArrayInputStream stream) {
        if (stream.available() < 2) {
            throw new TlvException("Error parsing data. Available bytes < 2 . Length=" + stream.available());
        }
        stream.mark(0);
        int peekInt = stream.read();
        byte peekByte = (byte) peekInt;
        while (peekInt != -1 && (peekByte == (byte) -1 || peekByte == (byte) 0)) {
            stream.mark(0);
            peekInt = stream.read();
            peekByte = (byte) peekInt;
        }
        stream.reset();
        if (stream.available() < 2) {
            throw new TlvException("Error parsing data. Available bytes < 2 . Length=" + stream.available());
        }
        byte[] tagIdBytes = readTagIdBytes(stream);
        stream.mark(0);
        int posBefore = stream.available();
        int length = readTagLength(stream);
        int posAfter = stream.available();
        stream.reset();
        byte[] lengthBytes = new byte[(posBefore - posAfter)];
        if (lengthBytes.length < 1 || lengthBytes.length > 4) {
            throw new TlvException("Number of length bytes must be from 1 to 4. Found " + lengthBytes.length);
        }
        byte[] valueBytes;
        stream.read(lengthBytes, 0, lengthBytes.length);
        int rawLength = BytesUtils.byteArrayToInt(lengthBytes);
        ITag tag = searchTagById(tagIdBytes);
        if (rawLength == 128) {
            stream.mark(0);
            int prevOctet = 1;
            int len = 0;
            while (true) {
                len++;
                int curOctet = stream.read();
                if (curOctet >= 0) {
                    if (prevOctet == 0 && curOctet == 0) {
                        break;
                    }
                    prevOctet = curOctet;
                } else {
                    throw new TlvException("Error parsing data. TLV length byte indicated indefinite length, but EOS was reached before 0x0000 was found" + stream.available());
                }
            }
            len -= 2;
            valueBytes = new byte[len];
            stream.reset();
            stream.read(valueBytes, 0, len);
            length = len;
        } else if (stream.available() < length) {
            throw new TlvException("Length byte(s) indicated " + length + " value bytes, but only " + stream.available() + " " + (stream.available() > 1 ? "are" : "is") + " available");
        } else {
            valueBytes = new byte[length];
            stream.read(valueBytes, 0, length);
        }
        stream.mark(0);
        peekInt = stream.read();
        peekByte = (byte) peekInt;
        while (peekInt != -1 && (peekByte == (byte) -1 || peekByte == (byte) 0)) {
            stream.mark(0);
            peekInt = stream.read();
            peekByte = (byte) peekInt;
        }
        stream.reset();
        return new TLV(tag, length, lengthBytes, valueBytes);
    }

    private static String getTagValueAsString(ITag tag, byte[] value) {
        StringBuilder buf = new StringBuilder();
        switch (tag.getTagValueType()) {
            case TEXT:
                buf.append("=");
                buf.append(new String(value));
                break;
            case NUMERIC:
                buf.append("NUMERIC");
                break;
            case BINARY:
                buf.append("BINARY");
                break;
            case MIXED:
                buf.append("=");
                buf.append(getSafePrintChars(value));
                break;
            case DOL:
                buf.append("");
                break;
        }
        return buf.toString();
    }

    public static List<TagAndLength> parseTagAndLength(byte[] data) {
        List<TagAndLength> tagAndLengthList = new ArrayList();
        if (data != null) {
            ByteArrayInputStream stream = new ByteArrayInputStream(data);
            while (stream.available() > 0) {
                if (stream.available() < 2) {
                    throw new TlvException("Data length < 2 : " + stream.available());
                }
                tagAndLengthList.add(new TagAndLength(searchTagById(readTagIdBytes(stream)), readTagLength(stream)));
            }
        }
        return tagAndLengthList;
    }

    public static String prettyPrintAPDUResponse(byte[] data) {
        return prettyPrintAPDUResponse(data, 0);
    }

    public static String prettyPrintAPDUResponse(byte[] data, int startPos, int length) {
        byte[] tmp = new byte[(length - startPos)];
        System.arraycopy(data, startPos, tmp, 0, length);
        return prettyPrintAPDUResponse(tmp, 0);
    }

    public static List<TLV> getlistTLV(byte[] pData, ITag pTag, boolean pAdd) {
        List<TLV> list = new ArrayList();
        ByteArrayInputStream stream = new ByteArrayInputStream(pData);
        while (stream.available() > 0) {
            TLV tlv = getNextTLV(stream);
            if (pAdd) {
                list.add(tlv);
            } else if (tlv.getTag().isConstructed()) {
                list.addAll(getlistTLV(tlv.getValueBytes(), pTag, tlv.getTag() == pTag));
            }
        }
        return list;
    }

    public static List<TLV> getlistTLV(byte[] pData, ITag... pTag) {
        List<TLV> list = new ArrayList();
        ByteArrayInputStream stream = new ByteArrayInputStream(pData);
        while (stream.available() > 0) {
            TLV tlv = getNextTLV(stream);
            if (ArrayUtils.contains((Object[]) pTag, tlv.getTag())) {
                list.add(tlv);
            } else if (tlv.getTag().isConstructed()) {
                list.addAll(getlistTLV(tlv.getValueBytes(), pTag));
            }
        }
        return list;
    }

    public static byte[] getValue(byte[] pData, ITag... pTag) {
        byte[] ret = null;
        if (pData != null) {
            ByteArrayInputStream stream = new ByteArrayInputStream(pData);
            while (stream.available() > 0) {
                TLV tlv = getNextTLV(stream);
                if (ArrayUtils.contains((Object[]) pTag, tlv.getTag())) {
                    return tlv.getValueBytes();
                }
                if (tlv.getTag().isConstructed()) {
                    ret = getValue(tlv.getValueBytes(), pTag);
                    if (ret != null) {
                        break;
                    }
                }
            }
        }
        return ret;
    }

    public static String prettyPrintAPDUResponse(byte[] data, int indentLength) {
        StringBuilder buf = new StringBuilder();
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        while (stream.available() > 0) {
            buf.append(IOUtils.LINE_SEPARATOR_UNIX);
            if (stream.available() == 2) {
                stream.mark(0);
                byte[] value = new byte[2];
                try {
                    stream.read(value);
                } catch (IOException e) {
                }
                SwEnum sw = SwEnum.getSW(value);
                if (sw != null) {
                    buf.append(getSpaces(0));
                    buf.append(BytesUtils.bytesToString(value)).append(" -- ");
                    buf.append(sw.getDetail());
                } else {
                    stream.reset();
                }
            }
            buf.append(getSpaces(indentLength));
            TLV tlv = getNextTLV(stream);
            byte[] tagBytes = tlv.getTagBytes();
            byte[] lengthBytes = tlv.getRawEncodedLengthBytes();
            byte[] valueBytes = tlv.getValueBytes();
            ITag tag = tlv.getTag();
            buf.append(prettyPrintHex(tagBytes));
            buf.append(" ");
            buf.append(prettyPrintHex(lengthBytes));
            buf.append(" -- ");
            buf.append(tag.getName());
            int extraIndent = (lengthBytes.length + tagBytes.length) * 3;
            if (tag.isConstructed()) {
                buf.append(prettyPrintAPDUResponse(valueBytes, indentLength + extraIndent));
            } else {
                buf.append(IOUtils.LINE_SEPARATOR_UNIX);
                if (tag.getTagValueType() == TagValueTypeEnum.DOL) {
                    buf.append(getFormattedTagAndLength(valueBytes, indentLength + extraIndent));
                } else {
                    buf.append(getSpaces(indentLength + extraIndent));
                    buf.append(prettyPrintHex(BytesUtils.bytesToStringNoSpace(valueBytes), indentLength + extraIndent));
                    buf.append(" (");
                    buf.append(getTagValueAsString(tag, valueBytes));
                    buf.append(")");
                }
            }
        }
        return buf.toString();
    }

    public static String getSpaces(int length) {
        return StringUtils.leftPad("", length);
    }

    public static String prettyPrintHex(String in, int indent) {
        return prettyPrintHex(in, indent, true);
    }

    public static String prettyPrintHex(byte[] data, int indent) {
        return prettyPrintHex(BytesUtils.bytesToStringNoSpace(data), indent, true);
    }

    public static String prettyPrintHex(String in) {
        return prettyPrintHex(in, 0, true);
    }

    public static String prettyPrintHex(byte[] data) {
        return prettyPrintHex(BytesUtils.bytesToStringNoSpace(data), 0, true);
    }

    public static String prettyPrintHex(String in, int indent, boolean wrapLines) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < in.length(); i++) {
            buf.append(in.charAt(i));
            int nextPos = i + 1;
            if (wrapLines && nextPos % 32 == 0 && nextPos != in.length()) {
                buf.append(IOUtils.LINE_SEPARATOR_UNIX).append(getSpaces(indent));
            } else if (nextPos % 2 == 0 && nextPos != in.length()) {
                buf.append(" ");
            }
        }
        return buf.toString();
    }

    public static String getSafePrintChars(byte[] byteArray) {
        if (byteArray == null) {
            return "";
        }
        return getSafePrintChars(byteArray, 0, byteArray.length);
    }

    public static String getSafePrintChars(byte[] byteArray, int startPos, int length) {
        if (byteArray == null) {
            return "";
        }
        if (byteArray.length < startPos + length) {
            throw new IllegalArgumentException("startPos(" + startPos + ")+length(" + length + ") > byteArray.length(" + byteArray.length + ")");
        }
        StringBuilder buf = new StringBuilder();
        int i = startPos;
        while (i < startPos + length) {
            if (byteArray[i] < (byte) 32 || byteArray[i] >= Byte.MAX_VALUE) {
                buf.append(".");
            } else {
                buf.append((char) byteArray[i]);
            }
            i++;
        }
        return buf.toString();
    }

    public static int getLength(List<TagAndLength> pList) {
        int ret = 0;
        if (pList != null) {
            for (TagAndLength tl : pList) {
                ret += tl.getLength();
            }
        }
        return ret;
    }

    private TlvUtil() {
    }
}
