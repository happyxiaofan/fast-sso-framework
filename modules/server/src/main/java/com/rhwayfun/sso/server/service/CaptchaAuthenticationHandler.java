package com.rhwayfun.sso.server.service;

import com.rhwayfun.sso.common.MD5;
import com.rhwayfun.sso.common.StringUtil;
import com.rhwayfun.sso.server.dao.UserDaoObject;
import com.rhwayfun.sso.server.model.Credential;
import com.rhwayfun.sso.server.model.DemoLoginUser;
import com.rhwayfun.sso.server.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.util.Set;

/**
 * Created by rhwayfun on 16-3-23.
 */
public class CaptchaAuthenticationHandler implements IAuthenticationHandler {

    @Autowired
    private UserDaoObject userDaoObject;
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

        //用户名与密码验证
        String passwd = MD5.encode(MD5.encode(credential.getParameter("passwd")) + sessionCode);
        String passwd2 = MD5.encode(MD5.encode("admin") + sessionCode);
        if (!"admin".equals(credential.getParameter("name"))
                || !passwd.equals(passwd2)) {
            credential.setErrorMsg("用户名或者密码错误！");
            return null;
        }

        //如果验证通过就返回一个LoginUser对象
        DemoLoginUser user = new DemoLoginUser();
        user.setLoginName("admin");
        return user;
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

    @Override
    public LoginUser autoLogin(String lt) throws Exception {
        /*String code = DES.decrypt(lt,"test==");
        String[] tmp = code.split(",");
        if (tmp.length == 2){
            String username = tmp[0];
            String passwd = tmp[1];
            if ("admin".equals(username) && passwd.equals(MD5.encode("password"))){
                DemoLoginUser loginUser = new DemoLoginUser();
                loginUser.setLoginName(username);
                return loginUser;
            }
        }*/

        //从持久化存储中根据lt获取user对象
        FileInputStream fi = new FileInputStream("/home/rhwayfun/java/notes/user.conf");
        byte[] buf = new byte[fi.available()];
        fi.read(buf);
        fi.close();

        //获取loginToken
        String tmp = new String(buf);
        String[] tmps = tmp.split("=");
        if (tmps[0].equals(lt)){
            DemoLoginUser loginUser = userDaoObject.getUserByName(tmps[1]);
            return loginUser;
        }

        return null;
    }

    @Override
    public String loginToken(LoginUser loginUser) throws Exception {
        DemoLoginUser user = (DemoLoginUser) loginUser;
        //生成一个随机的自动登陆标识
        String lt = StringUtil.uniqueKey();
        //更新user对象对应的字段
        userDaoObject.updateLoginToken(user.getLoginName(),lt);
        return lt;
    }

    @Override
    public void clearLoginToken(LoginUser user) throws Exception {
        DemoLoginUser demoLoginUser = (DemoLoginUser) user;
        userDaoObject.updateLoginToken(demoLoginUser.getLoginName(),null);
    }
}
