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
    public static String getCookie(String cookieName, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 根据vt删除cookie
     * @param response
     */
    public static void deleteCookie(String cookieName, HttpServletResponse response, String path) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        if (path != null) {
            cookie.setPath("/");
        }
        response.addCookie(cookie);
    }
}
