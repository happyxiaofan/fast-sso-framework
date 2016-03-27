package com.rhwayfun.sso.server.controller;

import com.rhwayfun.sso.common.CookieUtil;
import com.rhwayfun.sso.common.StringUtil;
import com.rhwayfun.sso.server.model.Credential;
import com.rhwayfun.sso.server.model.LoginUser;
import com.rhwayfun.sso.server.util.Config;
import com.rhwayfun.sso.server.util.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

/**
 * Created by rhwayfun on 16-3-23.
 */
@Controller
public class LoginController {

    @Autowired
    private Config config;

    /**
     * 登陆方法
     *
     * @param backUrl
     * @param map
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(String backUrl, ModelMap map, HttpServletRequest request,
                        HttpServletResponse response) throws Exception {
        //判断授权码VT是否存在
        String vt = CookieUtil.getCookie("VT", request);
        if (vt == null) {//不存在vt
            String lt = CookieUtil.getCookie("LT", request);
            if (lt == null) {//lt不存在，跳转到登陆页面
                return config.getLoginViewName();
            } else {//lt存在，执行自动登陆流程
                return null;
            }
        } else {//存在vt
            //获取登陆的用户
            LoginUser loginUser = TokenManager.validate(vt);
            if (loginUser == null) {//vt失效，转入登陆页面
                return config.getLoginViewName();
            } else {//vt有效，验证vt的有效性
                return validateSuccess(backUrl, vt, loginUser,response, map);
            }
        }
    }

    /**
     * 登陆验证的方法
     * @param backUrl
     * @param rememberMe
     * @param map
     * @param request
     * @param response
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(String backUrl, String rememberMe, ModelMap map,
                                HttpServletRequest request, HttpServletResponse response,
                                HttpSession session) throws Exception {
        //获取request中所有的请求参数
        final Map<String,String[]> params = request.getParameterMap();
        //创建令牌信息
        Credential credential = new Credential() {

            @Override
            public String getParameter(String name) throws Exception {
                String[] s = params.get(name);
                return s != null && s.length > 0 ? s[0] : null;
            }

            @Override
            public String[] getParameters(String name) throws Exception {
                return params.get(name);
            }

        };
        //通过授权处理器来验证令牌的有效性
        LoginUser user = config.getAuthenticationHandler().authenticate(credential);
        if (user == null){//验证失败
            map.put("errorMsg",credential.getErrorMsg());
            return config.getLoginViewName();
        }else {//验证成功
            //得到该用户的vt
            String vt = authSuccess(response, user,rememberMe);
            //跳转页面
            return validateSuccess(backUrl,vt,user,response,map);
        }
    }

    /**
     * 根据是否自动登陆的标识得到vt
     * @param response
     * @param rememberMe
     * @return
     */
    private String authSuccess(HttpServletResponse response, LoginUser user,String rememberMe) throws Exception {
        // 生成VT
        String vt = StringUtil.uniqueKey();
        // 生成LT？
        // TODO: 自动登录标识生成：后面实现
        // 存入Map
        TokenManager.addToken(vt, user);
        // 写 Cookie
        Cookie cookie = new Cookie("VT", vt);
        response.addCookie(cookie);
        return vt;
    }

    /**
     * vt有效后的操作
     *
     * @param backUrl
     * @param vt
     * @param response
     * @param map
     * @return
     * @throws Exception
     */
    private String validateSuccess(String backUrl, String vt, LoginUser loginUser,HttpServletResponse response, ModelMap map) throws Exception {
        if (backUrl != null) {
            response.sendRedirect(StringUtil.appendUrlParameter(backUrl, "VT", vt));
            return null;
        } else {
            map.put("sysList", config.getClientSystems(loginUser));
            map.put("vt", vt);
            map.put("loginUser",loginUser);
            return config.getLoginViewName();
        }
    }
}
