package com.rhwayfun.sso.server.service;

import com.rhwayfun.sso.server.model.Credential;
import com.rhwayfun.sso.server.model.DemoLoginUser;
import com.rhwayfun.sso.server.model.LoginUser;

import java.util.Set;

/**
 * Created by rhwayfun on 16-3-23.
 */
public class CaptchaAuthenticationHandler implements IAuthenticationHandler {
    /**
     * 验证登陆信息的有效性：这里只是验证当用户名和密码都是admin的时候才算合法用户
     *
     * @param credential
     * @return
     * @throws Exception
     */
    @Override
    public LoginUser authenticate(Credential credential) throws Exception {
        //获取session保存的验证码
        String sessionCode = (String) credential.getSettedSessionValue();
        String captcha = credential.getParameter("captcha");
        if (!sessionCode.equals(captcha)) {
            credential.setErrorMsg("验证码错误");
            return null;
        }
        if (!"admin".equals(credential.getParameter("name"))
                || !"admin".equals(credential.getParameter("passwd"))) {
            credential.setErrorMsg("用户名或者密码错误！");
            return null;
        } else {
            //如果验证通过就返回一个LoginUser对象
            DemoLoginUser user = new DemoLoginUser();
            user.setLoginName("admin");
            return user;
        }
    }

    /**
     * 根据用户账号获取关联的业务系统的id
     *
     * @param loginUser
     * @return
     * @throws Exception
     */
    @Override
    public Set<String> authedSystemIds(LoginUser loginUser) throws Exception {
        return null;
    }
}
