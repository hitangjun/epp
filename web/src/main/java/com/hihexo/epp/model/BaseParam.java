package com.hihexo.epp.model;

import com.verisign.epp.namestore.interfaces.NSSubProduct;
import lombok.Data;

/**
 * @author JohnTang
 * @date 2017/4/26
 */
@Data
public class BaseParam {
    private String domainName;
    private String authStr;
    private String domainProductID = NSSubProduct.COM;
    private String allocationToken = "";
}
