package com.rhwayfun.sso.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    /**
     * 根据vt删除cookie
     * @param vt
     * @param response
     */
    public static void deleteCookie(String vt, HttpServletResponse response) {
        if (vt != null){
            Cookie cookie = new Cookie("VT",null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
