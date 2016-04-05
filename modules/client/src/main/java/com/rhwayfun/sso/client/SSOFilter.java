package com.rhwayfun.sso.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rhwayfun.sso.common.CookieUtil;
import com.rhwayfun.sso.common.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 登录状态验证拦截器
 */
public class SSOFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(SSOFilter.class);

    private String excludes; // 不需要拦截的URI模式，以正则表达式表示
    private String serverBaseUrl; // 服务端公网访问地址
    private String serverInnerAddress; // 服务端系统间通信用内网地址

    private boolean notLoginOnFail; // 当授权失败时是否让浏览器跳转到服务端登录页

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        excludes = filterConfig.getInitParameter("excludes");
        serverBaseUrl = filterConfig.getInitParameter("serverBaseUrl");
        serverInnerAddress = filterConfig.getInitParameter("serverInnerAddress");

        notLoginOnFail = Boolean.parseBoolean(filterConfig.getInitParameter("notLoginOnFail"));

        if (serverBaseUrl == null || serverInnerAddress == null) {
            throw new ServletException("SSOFilter配置错误，必须设置serverBaseUrl和serverInnerAddress参数!");
        }

        TokenManager.serverIndderAddress = serverInnerAddress;
    }

    /**
     * 拦截器方法
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
                CookieUtil.deleteCookie("VT", response, "/");
                // 引导浏览器重定向到服务端执行登录校验
                loginCheck(request, response);
            }
        } else {
            String vtParam = pasreVtParam(request); // 从请求中
            if (vtParam == null) {
                // 请求中中没有vtParam，引导浏览器重定向到服务端执行登录校验
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
     * 从参数中获取服务端传来的vt后，执行一个到本链接的重定向，
     * 将vt写入cookie重定向后再发来的请求就存在有效vt参数了
     * @param vt
     * @param request
     * @param response
     * @throws IOException
     */
    private void redirectToSelf(String vt, HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String PARANAME = "__vt_param__="; 
        // 此处拼接redirect的url，去除vt参数部分
        StringBuffer location = request.getRequestURL();
       
        String qstr = request.getQueryString();
        int index = qstr.indexOf(PARANAME);
      //http://www.sys1.com:8081/test/tt?a=2&b=xxx
        if (index > 0) { // 还有其它参数，para1=param1&param2=param2&__vt_param__=xxx是最后一个参数
            qstr = "?" + qstr.substring(0, qstr.indexOf(PARANAME) - 1);
        } else { // 没有其它参数 qstr = __vt_param__=xxx
            qstr = "";
        }
        
        
        location.append(qstr);

        Cookie cookie = new Cookie("VT", vt);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        response.sendRedirect(location.toString());
    }

    /**
     * 从请求参数中解析vt
     * @param request
     * @return
     */
    private String pasreVtParam(HttpServletRequest request) {
        
        final String PARANAME = "__vt_param__=";
        
        String qstr = request.getQueryString();
      // a=2&b=xxx&__vt_param__=xxxxxxx
        if (qstr == null) {
            return null;
        }
        
        int index = qstr.indexOf(PARANAME);
        if (index > -1) {
            return qstr.substring(index + PARANAME.length());
        } else {
            return null;
        }
    }

    /**
     * 引导浏览器重定向到服务端执行登录校验
     * @param request
     * @param response
     * @throws IOException
     */
    private void loginCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // ajax类型请求涉及跨域问题
        // CORS方案解决跨域操作时，无法携带Cookie，所以无法完成验证，此处不适合
        // jsonp方案可以处理Cookie问题，但jsonp方式对后端代码有影响，能实现但复杂不理想，大家可以课后练习实现
        // 所以ajax请求前建议先让业务系统获取到vt，这样发起ajax请求时就不会执行跳转验证操作，避免跨域操作产生
        if ("XMLHttpRequest".equals(request.getHeader("x-requested-with"))) {
            // 400 状态表示请求格式错误，服务器没有理解请求，此处返回400状态表示未登录时服务器拒绝此ajax请求
            response.sendError(400);
        } else {
            // redirect只能是get请求，所以如果当前是post请求，会将post过来的请求参数变成url querystring，即get形式参数
            // 这种情况，此处实现就会有一个局限性 —— 请求参数长度的限制，因为浏览器对get请求的长度都会有所限制。
            // 如果post过来的内容过大，就会造成请求参数丢失
            // 解决这个问题，只能是让用户系统去避免这种情况发生.
            // 可以在发送这类请求前任意时间点发起一次任意get类型请求，这个get请求通过loginCheck
            // 的引导从服务端获取到vt，当再发起post请求时，vt已存在并有效，就不会进入到这个过程，从而避免了问题出现
// http://www.sys1.com:8081/test/tt?a=2&b=xxx&__vt_param__=
          
            String qstr = makeQueryString(request); // 将所有请求参数重新拼接成queryString
            String backUrl = request.getRequestURL() + qstr; // 回调url
            String location = serverBaseUrl + "/login?backUrl=" + URLEncoder.encode(backUrl, "utf-8");
            // 普通类型请求     http://www.ca.com:8080/login?backUrl=
            if (notLoginOnFail) {
                location += "&notLogin=true";
            }
            response.sendRedirect(location);
        }

    }

    /**
     * 将所有请求参数重新拼接成queryString
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    private String makeQueryString(HttpServletRequest request) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
// ? a= 1&a=2&b=xx [1,2][] ?a=1&a=2&b=xxx
        Enumeration<String> paraNames = request.getParameterNames();
        while (paraNames.hasMoreElements()) {
            String paraName = paraNames.nextElement();
            String[] paraVals = request.getParameterValues(paraName);
            for (String paraVal : paraVals) {
                builder.append("&").append(paraName).append("=").append(URLEncoder.encode(paraVal, "utf-8"));
            }
        }

        if (builder.length() > 0) {
            builder.replace(0, 1, "?");
        }

        return builder.toString();
    }

    /**
     * 将user存入threadLocal和request，供业务系统使用
     * @param user
     * @param request
     */
    private void holdUser(SSOUser user, ServletRequest request) {
        UserHolder.set(user, request);
    }

    /**
     * 判断请求是否不需要拦截
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
