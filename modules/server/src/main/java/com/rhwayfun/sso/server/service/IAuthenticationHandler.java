package com.rhwayfun.sso.server.service;

import com.rhwayfun.sso.server.model.Credential;
import com.rhwayfun.sso.server.model.LoginUser;

import java.util.Set;

/**
 * Created by rhwayfun on 16-3-23.
 * 授权处理器
 */
public interface IAuthenticationHandler {
    LoginUser authenticate(Credential credential) throws Exception;

    Set<String> authedSystemIds(LoginUser loginUser) throws Exception;
}
