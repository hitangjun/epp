package com.hihexo.epp.model;

import com.verisign.epp.codec.secdnsext.v11.EPPSecDNSExtDsData;
import com.verisign.epp.codec.secdnsext.v11.EPPSecDNSExtKeyData;
import lombok.Data;
import lombok.ToString;

/**
 * @author JohnTang
 * @date 2017/4/26
 */
@Data
@ToString(callSuper=true,includeFieldNames=true)
public class NSDomainDsDataCreateParam extends BaseParam{
    private EPPSecDNSExtDsData dsData ;
    private EPPSecDNSExtKeyData keyData;
}
