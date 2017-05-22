package com.hihexo.epp.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author JohnTang
 * @date 2017/4/26
 */
@Data
@ToString(callSuper=true,includeFieldNames=true)
public class NSDomainRestoreReportParam extends BaseParam{
    private String preData;//"Pre-delete whois data goes here. Both XML and free text are allowed"
    private String postData;//"Post-delete whois data goes here. Both XML and free text are allowed"
    private String deleteTime;
    private String restoreTime;
    private String restoreReason;
    private String restoreStatement1;
    private String restoreStatement2;
    private String otherStuff;
}
