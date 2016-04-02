package com.rhwayfun.sso.client;

import javax.servlet.ServletRequest;

/**
 * 供业务系统使用的用户对象获取工具类
 * 
 * @author preach
 *
 */
public class UserHolder {

    // 将当前登录用户信息存放到ThreadLocal中，这样在没有单独开线程的情况下，业务系统任意代码位置都可以取得当前user
    static final ThreadLocal<SSOUser> USER_THREAD_LOCAL = new ThreadLocal<SSOUser>();

    private UserHolder() {
    }

    /**
     * 获取SSOUser实例，此方法从ThreadLocal中获取，当调用处代码与请求主线程不处于同一线程时，此方法无效
     * 
     * @return
     */
    public static SSOUser getUser() {
        return USER_THREAD_LOCAL.get();
    }

    /**
     * 从当前请求属性中获取SSOUser
     * 
     * @param request
     * @return
     */
    public static SSOUser getUser(ServletRequest request) {
        return (SSOUser) request.getAttribute("__current_sso_user");
    }
}
