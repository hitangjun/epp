package com.hihexo.epp.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author JohnTang
 * @date 2017/4/26
 */
@Data
@ToString(callSuper=true,includeFieldNames=true)
public class NSHostUpdateParam extends BaseParam{
    private String internalHostName;
    private String[] addIpv4 = {};
    private String[] addIpv6 = {};
    private String[] removeIpv4 = {};
    private String[] removeIpv6 = {};
    private String[] addHostStatus = {};
    private String[] removeHostStatus = {};

}
