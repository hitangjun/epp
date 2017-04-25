package com.hihexo.epp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author JohnTang
 * @date 2017/4/25
 */
public class DateUtil {
    public final static String getNowYYYYMMDD() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        return sdf.format(new Date());
    }
}
