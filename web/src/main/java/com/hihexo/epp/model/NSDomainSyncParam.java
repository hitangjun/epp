package com.hihexo.epp.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author JohnTang
 * @date 2017/4/26
 */
@Data
@ToString(callSuper=true,includeFieldNames=true)
public class NSDomainSyncParam extends BaseParam{
    private int expirateMonth = 11;
    private int expirateDay = 1;
}
