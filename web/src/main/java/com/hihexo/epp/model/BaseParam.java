package com.hihexo.epp.model;

/**
 * @author JohnTang
 * @date 2017/4/26
 */
public class BaseParam {
    private String domainName;
    private String authStr;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getAuthStr() {
        return authStr;
    }

    public void setAuthStr(String authStr) {
        this.authStr = authStr;
    }
}
