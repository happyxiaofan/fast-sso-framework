package com.rhwayfun.sso.server.dao;

import com.rhwayfun.sso.server.model.DemoLoginUser;
import org.springframework.stereotype.Repository;

import java.io.FileOutputStream;

/**
 * Created by rhwayfun on 16-3-29.
 */
@Repository
public class UserDaoObject {

    /**
     * 更新当前登陆用户的lt标识
     * @param loginName
     * @param lt
     * @throws Exception
     */
    public void updateLoginToken(String loginName,String lt) throws Exception{
        //写入文件中，格式为lt=xxx
        //写入文件中
        FileOutputStream fo = new FileOutputStream("/home/rhwayfun/java/notes/user.conf");
        fo.write((lt + "=" + loginName).getBytes());
        fo.close();
    }

    /**
     * 根据登陆账号返回用户信息
     * @param uname
     * @return
     * @throws Exception
     */
    public DemoLoginUser getUserByName(String uname) throws Exception{
        if ("admin".equals(uname)){
            DemoLoginUser loginUser = new DemoLoginUser();
            loginUser.setLoginName("admin");
            return loginUser;
        }
        return null;
    }
}
