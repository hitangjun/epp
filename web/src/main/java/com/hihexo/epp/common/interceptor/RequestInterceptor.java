package com.hihexo.epp.common.interceptor;

import com.hihexo.epp.util.IPutil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * 请求合法性检查拦截器
 * 1. 检查头部是否合法
 * 2. 对于头部合法的请求
 *
 * @author JohnTang 2016.5.2
 */
public class RequestInterceptor implements HandlerInterceptor {


    private Logger logger = LoggerFactory.getLogger(RequestInterceptor.class);

    @SuppressWarnings("unchecked")
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3)
            throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3)
            throws Exception {
    }

    /**
     * CLIENT-TYPE 1|2
     * VERSION
     * APIVERSION 1.0
     * TIME 时间戳
     * POSTFIX : encryptStr = AES_encrypt(clientType+"|"+version+"|"+apiVersion+"|"+TIME,md5(TIME))
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = IPutil.getIpAddr(request);
        String path = request.getServletPath();
        logger.debug("===> " + ip + " --  path ");

 		logParams(request);
//        if (isInvalidHeader(request)) {
//            response.setHeader("Content-type", "text/html;charset=UTF-8");
//            response.setContentType("application/json;charset=UTF-8");
//            response.setCharacterEncoding("UTF-8");
//            response.addHeader("ERROR", genWarnInfo(ip, path));
//            PrintWriter out = response.getWriter();
//            out.print(genWarnInfo(ip, path));
//            return false;
//        }

//        if (isInvalidSecurityStr(request)) {
//            response.setHeader("Content-type", "text/html;charset=UTF-8");
//            response.setContentType("application/json;charset=UTF-8");
//            response.setCharacterEncoding("UTF-8");
//            response.addHeader("ERROR", "SecurityError " + genWarnInfo(ip, path));
//            PrintWriter out = response.getWriter();
//            out.print("SecurityError " + genWarnInfo(ip, path));
//            return false;
//        }

        return true;
    }

    /**
     * @param s 处理字符串，把"|"替换为空格
     */
    public String stringProcess(String s) {
        if (s != null) {
            s = s.replaceAll("\\|", " ");
            return s;
        }
        return "";
    }

    private String genWarnInfo(String ip, String path) {
        StringBuilder builder = new StringBuilder("Invalid request IP:");
        builder.append(ip).append("   path：").append(path);
        return builder.toString();
    }

    private void logParams(HttpServletRequest request) {
        try {
            Enumeration hnames = request.getHeaderNames();
            logger.debug("---" + request.getServletPath() + "-------header--------------");
            for (Enumeration e = hnames; e.hasMoreElements(); ) {
                String thisName = e.nextElement().toString();
                String thisValue = request.getHeader(thisName);
                logger.debug(thisName + "==" + thisValue);
            }
            logger.debug("---" + request.getServletPath() + "-------params--------------\n");
            Enumeration rnames = request.getParameterNames();
            for (Enumeration e = rnames; e.hasMoreElements(); ) {
                String thisName = e.nextElement().toString();
                String thisValue = request.getParameter(thisName);
                logger.debug(thisName + "==" + thisValue);
            }
        } catch (Exception e) {
            logger.error("validateRequest logParams error",e);
        }
    }

}