package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser.apdu.impl;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.ITag;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.TagAndLength;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.AbstractData;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser.apdu.IFile;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser.apdu.annotation.AnnotationData;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser.apdu.annotation.AnnotationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import fr.devnied.bitlib.BitUtils;

public abstract class AbstractByteBean<T> extends AbstractData implements IFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractByteBean.class.getName());
    private static final long serialVersionUID = -2016039522844322383L;

    private Collection<AnnotationData> getAnnotationSet(List<TagAndLength> pTags) {
        if (pTags == null) {
            return (Collection) AnnotationUtils.getInstance().getMapSet().get(getClass().getName());
        }
        Map<ITag, AnnotationData> data = (Map) AnnotationUtils.getInstance().getMap().get(getClass().getName());
        Collection<AnnotationData> ret = new ArrayList(data.size());
        for (TagAndLength tal : pTags) {
            AnnotationData ann = (AnnotationData) data.get(tal.getTag());
            if (ann != null) {
                ann.setSize(tal.getLength() * 8);
            } else {
                ann = new AnnotationData();
                ann.setSkip(true);
                ann.setSize(tal.getLength() * 8);
            }
            ret.add(ann);
        }
        return ret;
    }

    public void parse(byte[] pData, List<TagAndLength> pTags) {
        Collection<AnnotationData> set = getAnnotationSet(pTags);
        BitUtils bit = new BitUtils(pData);
        for (AnnotationData data : set) {
            if (data.isSkip()) {
                bit.addCurrentBitIndex(data.getSize());
            } else {
                setField(data.getField(), this, DataFactory.getObject(data, bit));
            }
        }
    }

    protected void setField(Field field, IFile pData, Object pValue) {
        if (field != null) {
            try {
                field.set(pData, pValue);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Parameters of fied.set are not valid", e);
            } catch (IllegalAccessException e2) {
                LOGGER.error("Impossible to set the Field :" + field.getName(), e2);
            }
        }
    }
}
