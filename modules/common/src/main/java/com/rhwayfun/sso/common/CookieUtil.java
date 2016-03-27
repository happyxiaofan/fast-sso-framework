package com.rhwayfun.sso.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by rhwayfun on 16-3-23.
 */
public class CookieUtil {
    private CookieUtil(){}

    /**
     * 查找给定的cookie
     * @param cookieName
     * @param request
     * @return
     * @throws Exception
     */
    public static String getCookie(String cookieName, HttpServletRequest request) throws Exception{
        //获取所有的cookie
        Cookie[] cookies = request.getCookies();
        //遍历所有的cookie查找与给定cookie相同cookieName的cookie
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (cookieName.equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
