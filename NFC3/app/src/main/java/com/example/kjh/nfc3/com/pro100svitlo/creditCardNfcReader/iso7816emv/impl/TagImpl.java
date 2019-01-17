package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.impl;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums.TagTypeEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.enums.TagValueTypeEnum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.ITag;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils.BytesUtils;

import java.util.Arrays;

public final class TagImpl implements ITag {
    private final String description;
    private final byte[] idBytes;
    public final String name;
    private final Class tagClass;
    private final TagValueTypeEnum tagValueType;
    private final TagTypeEnum type;

    public TagImpl(String id, TagValueTypeEnum tagValueType, String name, String description) {
        this(BytesUtils.fromString(id), tagValueType, name, description);
    }

    public TagImpl(byte[] idBytes, TagValueTypeEnum tagValueType, String name, String description) {
        if (idBytes == null) {
            throw new IllegalArgumentException("Param id cannot be null");
        } else if (idBytes.length == 0) {
            throw new IllegalArgumentException("Param id cannot be empty");
        } else if (tagValueType == null) {
            throw new IllegalArgumentException("Param tagValueType cannot be null");
        } else {
            this.idBytes = idBytes;
            this.name = name;
            this.description = description;
            this.tagValueType = tagValueType;
            if (BytesUtils.matchBitByBitIndex(this.idBytes[0], 5)) {
                this.type = TagTypeEnum.CONSTRUCTED;
            } else {
                this.type = TagTypeEnum.PRIMITIVE;
            }
            switch ((byte) ((this.idBytes[0] >>> 6) & 3)) {
                case (byte) 1:
                    this.tagClass = Class.APPLICATION;
                    return;
                case (byte) 2:
                    this.tagClass = Class.CONTEXT_SPECIFIC;
                    return;
                case (byte) 3:
                    this.tagClass = Class.PRIVATE;
                    return;
                default:
                    this.tagClass = Class.UNIVERSAL;
                    return;
            }
        }
    }

    public boolean isConstructed() {
        return this.type == TagTypeEnum.CONSTRUCTED;
    }

    public byte[] getTagBytes() {
        return this.idBytes;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public TagValueTypeEnum getTagValueType() {
        return this.tagValueType;
    }

    public TagTypeEnum getType() {
        return this.type;
    }

    public Class getTagClass() {
        return this.tagClass;
    }

    public boolean equals(Object other) {
        if (!(other instanceof ITag)) {
            return false;
        }
        ITag that = (ITag) other;
        if (getTagBytes().length == that.getTagBytes().length) {
            return Arrays.equals(getTagBytes(), that.getTagBytes());
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.idBytes) + 177;
    }

    public int getNumTagBytes() {
        return this.idBytes.length;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tag[");
        sb.append(BytesUtils.bytesToString(getTagBytes()));
        sb.append("] Name=");
        sb.append(getName());
        sb.append(", TagType=");
        sb.append(getType());
        sb.append(", ValueType=");
        sb.append(getTagValueType());
        sb.append(", Class=");
        sb.append(this.tagClass);
        return sb.toString();
    }
}
