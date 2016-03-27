package com.rhwayfun.sso.server.model;

/**
 * Created by rhwayfun on 16-3-23.
 */
public abstract class Credential {
    //错误信息
    private String errorMsg;

    /**
     * 根据名称获取某个参数的值
     * @param name
     * @return
     * @throws Exception
     */
    public abstract String getParameter(String name) throws  Exception;

    /**
     * 根据名称获取一组参数的值
     * @param name
     * @return
     * @throws Exception
     */
    public abstract String[] getParameters(String name) throws Exception;

    /**
     * 通过preLoginHandler的getSettedSessionValue的方法设置验证码的值
     * @return
     * @throws Exception
     */
    public abstract Object getSettedSessionValue() throws Exception;
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
