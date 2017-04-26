package com.hihexo.epp.model;

import com.verisign.epp.interfaces.EPPDomain;
import lombok.Data;
import lombok.ToString;

/**
 * @author JohnTang
 * @date 2017/4/26
 */
@Data
@ToString(callSuper=true,includeFieldNames=true)
public class NSDomainCreateParam extends BaseParam{
    private String[] hostNames = {};
    private String  adminContact;
    private String  techContact;
    private String  billContact;
    private int  period = 1;
    private String  periodUnit = EPPDomain.PERIOD_YEAR;

//    private EPPSecDNSExtDsData dsData ;
//    private EPPSecDNSExtKeyData keyData;
//    private EPPSecDNSExtDsData dsData2 ;
//    private EPPSecDNSExtKeyData keyData2;
//
//    private Map<String,String> coaExtKeyValuesMap = new HashMap<String,String>();
}
