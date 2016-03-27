package com.rhwayfun.sso.server.controller;

import com.rhwayfun.sso.server.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by rhwayfun on 16-3-27.
 * 作为一个演示性的配置管理器
 */
@Controller
public class ConfigManageController {

    @Autowired
    private Config config;

    @RequestMapping(value = "/config")
    public void configPage(){}

    @RequestMapping(value = "/config/refresh")
    public @ResponseBody String config(String code) throws Exception{
        if ("test".equals(code)){
            config.refreshConfig();
            return "true";
        }else {
            return "false";
        }
    }
}
