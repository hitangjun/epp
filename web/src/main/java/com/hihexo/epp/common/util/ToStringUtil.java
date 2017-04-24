package com.hihexo.epp.common.util;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ToStringUtil  {

    public static final String toString(Object obj){
       return toString(obj,ToStringStyle.DEFAULT_STYLE);
    }

    public static final String toStringMultiStyle(Object obj){
        return toString(obj,ToStringStyle.MULTI_LINE_STYLE);
    }

    private static final String  toString(Object obj,ToStringStyle toStringStyle){
        if(null != obj){
            return ToStringBuilder.reflectionToString(obj, toStringStyle);
        }
        return null;
    }
}
