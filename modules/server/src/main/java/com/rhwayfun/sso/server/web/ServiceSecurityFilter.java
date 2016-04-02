package com.rhwayfun.sso.server.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统间内网通信安全拦截器，此处执行安全认证
 */
@WebFilter({ "/validate_service", "/other_request" })
public class ServiceSecurityFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ServiceSecurityFilter.class);
    
    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        
        // 此处执行安全认证，常用认证方式
        
        // 1. 从request的paramter,header中获取安全认证凭证，按凭证验证
        //   request.getParameter("credential_name");
        //   ((HttpServletRequest)request).getHeader("credential_name");
        
        // 2. 根据已配置的客户端列表得到客户端IP列表，仅限列表内IP访问
        //   Config config = SpringContextUtil.getBean(Config.class);
        //   config.getClientSystems(); 得到clientList
        //   request.getRemoteAddr() 等到用户地址
        logger.debug("service security filter已执行！");
        chain.doFilter(request, response);
    }

    public void init(FilterConfig fConfig) throws ServletException {
    }

}
