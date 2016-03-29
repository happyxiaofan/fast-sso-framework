package com.rhwayfun.sso.server.service;

import com.rhwayfun.sso.server.model.Credential;
import com.rhwayfun.sso.server.model.LoginUser;

import java.util.Set;

/**
 * Created by rhwayfun on 16-3-23.
 * 授权处理器
 */
public interface IAuthenticationHandler {

    /**
     * 验证登陆用户信息
     * @param credential
     * @return
     * @throws Exception
     */
    LoginUser authenticate(Credential credential) throws Exception;

    /**
     * 获取用户具有权限的业务系统id
     * @param loginUser
     * @return
     * @throws Exception
     */
    Set<String> authedSystemIds(LoginUser loginUser) throws Exception;

    /**
     * 根据自动登陆标志返回用户信息
     * @param lt
     * @return
     * @throws Exception
     */
    LoginUser autoLogin(String lt) throws Exception;

    /**
     * 根据用户名生成随机的自动登陆表示
     * @param loginUser
     * @return
     * @throws Exception
     */
    String loginToken(LoginUser loginUser) throws Exception;

    /**
     * 清除服务端的自动登陆标志
     * @param user
     */
    void clearLoginToken(LoginUser user) throws Exception;
}
