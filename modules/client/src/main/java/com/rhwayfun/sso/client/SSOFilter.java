package com.rhwayfun.sso.client;

import com.rhwayfun.sso.common.CookieUtil;
import com.rhwayfun.sso.common.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by rhwayfun on 16-3-30.
 */
public class SSOFilter implements Filter {
    //日志记录器
    private Logger logger = LoggerFactory.getLogger(SSOFilter.class);

    //需要排除拦截的url请求
    private String excludes;
    //服务端公网访问地址
    private String serverBaseUrl;
    //服务端内网通信地址
    private String serverInnerAddress;

    public void init(FilterConfig filterConfig) throws ServletException {
        excludes = filterConfig.getInitParameter("excludes");
        serverBaseUrl = filterConfig.getInitParameter("serverBaseUrl");
        serverInnerAddress = filterConfig.getInitParameter("serverInnerAddress");
        if (serverBaseUrl == null || serverInnerAddress == null) {
            throw new ServletException("必须配置服务端通信地址");
        }
        TokenManager.serverIndderAddress = serverInnerAddress;
    }

    /**
     * 拦截主体执行方法
     * @param req
     * @param resp
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // 如果是不需要拦截的请求，直接通过
        if (requestIsExclude(request)) {
            chain.doFilter(request, response);
            return;
        }

        logger.debug("进入SSOFilter,当前请求url: {}", request.getRequestURL());

        // 进行登录状态验证
        String vt = null;
        try {
            vt = CookieUtil.getCookie("VT", request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vt != null) {
            SSOUser user = null;

            try {
                user = TokenManager.validate(vt);
            } catch (Exception e) {
                throw new ServletException(e);
            }

            if (user != null) {
                holdUser(user, request); // 将user存放，供业务系统使用
                chain.doFilter(request, response); // 请求继续向下执行
            } else {
                // 删除无效的VT cookie
                CookieUtil.deleteCookie("VT", response);
                // 引导浏览器重定向到服务端执行登录校验
                loginCheck(request, response);
            }
        } else {
            String vtParam = pasreVtParam(request); // 从请求中
            if (vtParam == null) {
                // url中没有vtParam，引导浏览器重定向到服务端执行登录校验
                loginCheck(request, response);
            } else if (vtParam.length() == 0) {
                // 有vtParam，但内容为空，表示到服务端loginCheck后，得到的结果是未登录
                response.sendError(403);
            } else {
                // 让浏览器向本链接发起一次重定向，此过程去除vtParam，将vt写入cookie
                redirectToSelf(vtParam, request, response);
            }
        }
    }

    /**
     * 从参数中获取服务端传来的vt后，执行一个到本链接的重定向，将vt写入cookie,重定向后再发来的请求就存在有效vt参数了
     * @param vt
     * @param request
     * @param response
     * @throws IOException
     */
    private void redirectToSelf(String vt, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 此处拼接redirect的url
        String location = "";

        Cookie cookie = new Cookie("VT", vt);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        response.sendRedirect(location);
    }

    /**
     * 从请求参数中解析vt
     * @param request
     * @return
     */
    private String pasreVtParam(HttpServletRequest request) {
        return null;
    }

    /**
     * 引导浏览器重定向到服务端执行登录校验
     *
     * @param request
     * @param response
     * @throws IOException
     */
    private void loginCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String location = "server_login_url?query_str";
        response.sendRedirect(location);
    }

    /**
     * 将user存入threadLocal和request，供业务系统使用
     * @param user
     * @param request
     */
    private void holdUser(SSOUser user, ServletRequest request) {
        UserHolder.USER_THREAD_LOCAL.set(user);
        request.setAttribute("__current_sso_user", user);
    }

    /**
     * 判断请求是否不需要拦截
     *
     * @param request
     * @return
     */
    private boolean requestIsExclude(ServletRequest request) {

        // 没有设定excludes时，所以经过filter的请求都需要被处理
        if (StringUtil.isEmpty(excludes)) {
            return false;
        }

        // 获取去除context path后的请求路径
        String contextPath = request.getServletContext().getContextPath();
        String uri = ((HttpServletRequest) request).getRequestURI();
        uri = uri.substring(contextPath.length());

        // 正则模式匹配的uri被排除，不需要拦截
        boolean isExcluded = uri.matches(excludes);

        if (isExcluded) {
            logger.debug("request path: {} is excluded!", uri);
        }

        return isExcluded;
    }

    @Override
    public void destroy() {
        // DO nothing
    }
}
