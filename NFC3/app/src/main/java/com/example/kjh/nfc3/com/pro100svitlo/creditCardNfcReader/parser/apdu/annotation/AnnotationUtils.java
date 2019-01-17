package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser.apdu.annotation;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.iso7816emv.ITag;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.EmvTransactionRecord;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.parser.apdu.IFile;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class AnnotationUtils {
    private static final AnnotationUtils INSTANCE = new AnnotationUtils();
    private static final Class<? extends IFile>[] LISTE_CLASS = new Class[]{EmvTransactionRecord.class};
    private final Map<String, Map<ITag, AnnotationData>> map = new HashMap();
    private final Map<String, Set<AnnotationData>> mapSet = new HashMap();

    public static AnnotationUtils getInstance() {
        return INSTANCE;
    }

    private AnnotationUtils() {
        extractAnnotation();
    }

    private void extractAnnotation() {
        for (Class<? extends IFile> clazz : LISTE_CLASS) {
            Map<ITag, AnnotationData> maps = new HashMap();
            Set<AnnotationData> set = new TreeSet();
            for (Field field : clazz.getDeclaredFields()) {
                AnnotationData param = new AnnotationData();
                field.setAccessible(true);
                param.setField(field);
                Data annotation = (Data) field.getAnnotation(Data.class);
                if (annotation != null) {
                    param.initFromAnnotation(annotation);
                    maps.put(param.getTag(), param);
                    try {
                        set.add((AnnotationData) param.clone());
                    } catch (CloneNotSupportedException e) {
                    }
                }
            }
            this.mapSet.put(clazz.getName(), set);
            this.map.put(clazz.getName(), maps);
        }
    }

    public Map<String, Set<AnnotationData>> getMapSet() {
        return this.mapSet;
    }

    public Map<String, Map<ITag, AnnotationData>> getMap() {
        return this.map;
    }
}
