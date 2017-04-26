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
public class NSRelatedDomainCreateParam extends BaseParam{
    private String  relatedDomainPwd;
    private int relatedPeriod = 1;
    private String[] relatedDomain = {};
    private String  periodUnit = EPPDomain.PERIOD_YEAR;
}
