package com.rhwayfun.sso.server.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by rhwayfun on 16-3-28.
 * 安全模式的拦截器，主要用于拦截非htpps的请求
 */
public class SecureModeInterceptor implements HandlerInterceptor{

    @Autowired
    private Config config;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        boolean secure = !config.isSecureMode() || httpServletRequest.isSecure();
        if (!secure){
            httpServletResponse.getWriter().write("only support https!");
        }
        return secure;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
