package com.hihexo.epp.common.base;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/12/19
 * @desc 基础控制器 添加一些控制方法
 */
public abstract class BaseController {


    public ResultVo renderError(String message){
        ResultVo resultVo = new ResultVo<>();
        resultVo.setResult(false);
        resultVo.setMessage(message);
        return resultVo;
    }

    public ResultVo renderSuccess(String message,Object object){
        ResultVo resultVo = new ResultVo<>();
        resultVo.setResult(true);
        resultVo.setMessage(message);
        resultVo.setData(object);
        return resultVo;
    }
    public ResultVo renderSuccess(String message){
        ResultVo resultVo = new ResultVo<>();
        resultVo.setResult(true);
        resultVo.setMessage(message);
        return resultVo;
    }

    public ResultVo renderSuccess(Object obj){
        ResultVo resultVo = new ResultVo<>();
        resultVo.setResult(true);
        resultVo.setData(obj);
        return resultVo;
    }
    public ResultVo renderSuccess(){
        ResultVo resultVo = new ResultVo<>();
        resultVo.setResult(true);
        return resultVo;
    }

}
