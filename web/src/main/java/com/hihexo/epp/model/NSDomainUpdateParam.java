package com.hihexo.epp.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author JohnTang
 * @date 2017/4/26
 */
@Data
@ToString(callSuper=true,includeFieldNames=true)
public class NSDomainUpdateParam extends BaseParam{
    private String newAuthStr;
    private String[] addBillContact = {};
    private String[] removeBillContact = {};
    private String[] addHostName = {};
    private String[] removeHostName = {};
    private String[] addDomainStatus = {};
    private String[] removeDomainStatus = {};
    private String registararShortName ;

}
