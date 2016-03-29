package com.rhwayfun.sso.server.service;

import com.rhwayfun.sso.server.model.Credential;
import com.rhwayfun.sso.server.model.DemoLoginUser;
import com.rhwayfun.sso.server.model.LoginUser;

import java.util.Set;

/**
 * Created by rhwayfun on 16-3-23.
 */
public class DemoAuthenticationHandler implements IAuthenticationHandler {
    /**
     * 验证登陆信息的有效性：这里只是验证当用户名和密码都是admin的时候才算合法用户
     * @param credential
     * @return
     * @throws Exception
     */
    @Override
    public LoginUser authenticate(Credential credential) throws Exception {
        if ("admin".equals(credential.getParameter("name"))
                && "admin".equals(credential.getParameter("passwd"))){
            //如果验证通过就返回一个LoginUser对象
            DemoLoginUser user = new DemoLoginUser();
            user.setLoginName("admin");
            return user;
        }else{
            //验证不通过显示错误信息
            credential.setErrorMsg("用户名或者密码错误！");
            return null;
        }
    }

    /**
     * 根据用户账号获取关联的业务系统的id
     * @param loginUser
     * @return
     * @throws Exception
     */
    @Override
    public Set<String> authedSystemIds(LoginUser loginUser) throws Exception {
        return null;
    }

    @Override
    public LoginUser autoLogin(String lt) throws Exception {
        return null;
    }

    @Override
    public String loginToken(LoginUser loginUser) throws Exception {
        return null;
    }

    @Override
    public void clearLoginToken(LoginUser user) throws Exception {

    }
}
