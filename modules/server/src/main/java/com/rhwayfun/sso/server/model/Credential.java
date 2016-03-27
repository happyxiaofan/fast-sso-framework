package com.rhwayfun.sso.server.model;

/**
 * Created by rhwayfun on 16-3-23.
 */
public abstract class Credential {
    private String errorMsg;

    public abstract String getParameter(String name) throws  Exception;

    public abstract String[] getParameters(String name) throws Exception;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
