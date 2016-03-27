package com.rhwayfun.sso.server.service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by rhwayfun on 16-3-27.
 * 登陆前置处理器
 */
public interface IPreLoginHandler {

    String SESSION_ATTR_NAME = "login_session_attr_name";
    /**
     * 登陆前处理
     * @param session
     * @return
     * @throws Exception
     */
    Map<?,?> handle(HttpSession session, HttpServletResponse response) throws Exception;
}
