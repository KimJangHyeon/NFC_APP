package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public abstract class AbstractData implements Serializable {
    private static final long serialVersionUID = -456811026151402726L;

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
