package com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model;

import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.enums.ServiceCode1Enum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.enums.ServiceCode2Enum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.model.enums.ServiceCode3Enum;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils.BytesUtils;
import com.example.kjh.nfc3.com.pro100svitlo.creditCardNfcReader.utils.EnumUtils;

import org.apache.commons.lang3.StringUtils;

import fr.devnied.bitlib.BitUtils;

public class Service extends AbstractData {
    private static final long serialVersionUID = 5154895810563519768L;
    private ServiceCode1Enum serviceCode1;
    private ServiceCode2Enum serviceCode2;
    private ServiceCode3Enum serviceCode3;

    public Service(String pData) {
        if (pData != null && pData.length() == 3) {
            BitUtils bit = new BitUtils(BytesUtils.fromString(StringUtils.rightPad(pData, 4, "0")));
            this.serviceCode1 = (ServiceCode1Enum) EnumUtils.getValue(bit.getNextInteger(4), ServiceCode1Enum.class);
            this.serviceCode2 = (ServiceCode2Enum) EnumUtils.getValue(bit.getNextInteger(4), ServiceCode2Enum.class);
            this.serviceCode3 = (ServiceCode3Enum) EnumUtils.getValue(bit.getNextInteger(4), ServiceCode3Enum.class);
        }
    }

    public ServiceCode1Enum getServiceCode1() {
        return this.serviceCode1;
    }

    public ServiceCode2Enum getServiceCode2() {
        return this.serviceCode2;
    }

    public ServiceCode3Enum getServiceCode3() {
        return this.serviceCode3;
    }
}
