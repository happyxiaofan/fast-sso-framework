package com.rhwayfun.sso.server.model;

import java.io.Serializable;

/**
 * Created by rhwayfun on 16-3-23.
 */
public abstract class LoginUser implements Serializable{

    /**
     * 自动登陆凭证
     * @return
     * @throws Exception
     */
    public abstract String loginToken() throws Exception;
}
