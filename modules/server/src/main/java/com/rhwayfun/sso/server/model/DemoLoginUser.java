package com.rhwayfun.sso.server.model;

/**
 * Created by rhwayfun on 16-3-23.
 */
public class DemoLoginUser extends LoginUser {
    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String toString() {
        return loginName;
    }
}
