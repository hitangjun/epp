package com.hihexo.epp.common.base;

import com.hihexo.epp.common.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 异常处理，对ajax类型的异常返回ajax错误，避免页面问题
 */
public class ExceptionResolver implements HandlerExceptionResolver {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response, Object handler, Exception e) {
        e.printStackTrace();
        // log记录异常
        logger.error(e.getMessage(), e);
        // 非控制器请求照成的异常
        if (!(handler instanceof HandlerMethod)) {
            return new ModelAndView("error/500");
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        if (isAjax(handlerMethod)) {
            ResultVo result = new ResultVo();
            result.setMessage(e.getMessage());
            result.setResult(false);
            return new ModelAndView(new MappingJackson2JsonView(), BeanUtils.toMap(result));
        }

        //其他异常 跳转到500页面
        return new ModelAndView("error/500");
    }


    /**
     * 判断是否ajax json返回
     *
     * @param handlerMethod
     * @return
     */
    public boolean isAjax(HandlerMethod handlerMethod) {
        ResponseBody responseBody = handlerMethod.getMethodAnnotation(ResponseBody.class);
        if (null != responseBody) {
            return true;
        }
        RestController restAnnotation = handlerMethod.getBean().getClass().getAnnotation(RestController.class);
        if (null != restAnnotation) {
            return true;
        }
        return false;
    }
}

