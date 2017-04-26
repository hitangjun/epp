package com.hihexo.epp.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author JohnTang
 * @date 2017/4/26
 */
@Data
@ToString(callSuper=true,includeFieldNames=true)
public class NSHostParam extends BaseParam{
    private String internalHostName;
    private String externalHostName;
    private String[] ipv4;
    private String[] ipv6;
    private String[] multiHost;
}
