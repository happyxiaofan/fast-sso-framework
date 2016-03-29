package com.rhwayfun.sso.server.model;

import com.rhwayfun.sso.common.DES;
import com.rhwayfun.sso.common.MD5;

/**
 * Created by rhwayfun on 16-3-23.
 */
public class DemoLoginUser extends LoginUser {

    //用户名
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

    @Override
    public String loginToken() throws Exception {
        final String password = "admin";
        return DES.encrypt(loginName + "," + MD5.encode(password),"test==");
    }
}
