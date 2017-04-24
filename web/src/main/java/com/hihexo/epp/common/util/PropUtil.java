package com.hihexo.epp.common.util;


import com.hihexo.epp.common.base.SpringContextHolder;

import java.util.Properties;

/**
 * @desc 配置文件自定义读取
 */
public final class PropUtil {


    /**
     * 获取 config.properties 配置信息
     * @param key
     * @return
     */
    public static final String getValue(String key){
        Properties properties = SpringContextHolder.getBean(Properties.class,"cfgProperties");
        return (String) properties.get(key);
    }

}
