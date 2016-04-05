package com.rhwayfun.sso.client;

import javax.servlet.ServletRequest;

/**
 * 供业务系统使用的用户对象获取工具类
 *
 */
public class UserHolder {

    private static final String REQ_USER_ATTR_NAME = "__current_sso_user";

    // 将当前登录用户信息存放到ThreadLocal中，这样在没有单独开线程的情况下，业务系统任意代码位置都可以取得当前user
    private static final ThreadLocal<SSOUser> userThreadLocal = new ThreadLocal<SSOUser>();

    private UserHolder() {
    }

    /**
     * 获取SSOUser实例，此方法从ThreadLocal中获取，当调用处代码与请求主线程不处于同一线程时，此方法无效
     * 
     * @return
     */
    public static SSOUser getUser() {
        return userThreadLocal.get();
    }

    /**
     * 从当前请求属性中获取SSOUser
     * 
     * @param request
     * @return
     */
    public static SSOUser getUser(ServletRequest request) {
        return (SSOUser) request.getAttribute(REQ_USER_ATTR_NAME);
    }

    /**
     * 用户加入到request和threadLocal供业务系统调用<br>
     * 以default为方法作用范围，仅本包内代码可访问，将此方法对用户代码隐藏
     * 
     * @param user
     * @param request
     * @return
     */
    static void set(SSOUser user, ServletRequest request) {
        request.setAttribute(REQ_USER_ATTR_NAME, user);
        userThreadLocal.set(user);
    }
}
