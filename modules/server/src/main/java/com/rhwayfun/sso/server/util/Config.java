package com.rhwayfun.sso.server.util;

import com.rhwayfun.sso.server.model.ClientSystem;
import com.rhwayfun.sso.server.model.LoginUser;
import com.rhwayfun.sso.server.service.IAuthenticationHandler;
import com.rhwayfun.sso.server.service.IPreLoginHandler;
import com.rhwayfun.sso.server.service.UserSerializer;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.comparator.BooleanComparator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by rhwayfun on 16-3-23.
 */
public class Config implements ResourceLoaderAware{
    private static Logger logger = LoggerFactory.getLogger(Config.class);
    //登陆视图名称
    private String loginViewName = "login";
    //授权处理器
    private IAuthenticationHandler authenticationHandler;
    //预登陆处理器
    private IPreLoginHandler preLoginHandler;
    //过期时间，单位是分钟
    private int timeout = 30;
    //业务系统列表
    private List<ClientSystem> clientSystems = new ArrayList<>();
    //加载资源需要使用Spring提供的ResourceLoader
    private ResourceLoader resourceLoader;
    //是否为安全模式
    private boolean secureMode = false;
    //自动登陆过期时间
    private int autoLoginExpiredDays = 30;
    //用户对象序列化器
    private UserSerializer userSerializer;

    /**
     * 从配置文件中加载所有的子系统列表
     * @return
     * @throws Exception
     */
    public List<ClientSystem> loadSystems() throws Exception{
        //加载业务系统的配置文件
        Resource resource = resourceLoader.getResource("classpath:client_system.xml");
        //使用SAX解析
        SAXReader reader = new SAXReader();
        //加载文档对象
        Document document = reader.read(resource.getInputStream());
        //根节点
        Element rootElement = document.getRootElement();
        //子系统列表
        List<Element> systemElement = rootElement.elements();
        //清空原来已添加的系统列表，避免重复添加
        clientSystems.clear();
        //遍历所有的系统元素节点
        for (Element sys : systemElement) {
            ClientSystem client = new ClientSystem();
            client.setId(sys.attributeValue("id"));
            client.setName(sys.attributeValue("name"));
            client.setBaseUrl(sys.elementText("baseUrl"));
            client.setHomeUri(sys.elementText("homeUri"));
            client.setInnerAddress(sys.elementText("innerAddress"));

            clientSystems.add(client);
        }
        return clientSystems;
    }

    /**
     * 根据用户名获取用户关联的系统列表
     * @param loginUser
     * @return
     */
    public List<ClientSystem> getClientSystems(LoginUser loginUser) throws Exception {
        Set<String> authedSysIds = getAuthenticationHandler().authedSystemIds(
                loginUser);
        if (authedSysIds == null){//默认加载全部系统列表
            return clientSystems;
        }
        List<ClientSystem> sys = new ArrayList<>();
        for (ClientSystem s:clientSystems) {
            if (authedSysIds.contains(s.getId())){
                sys.add(s);
            }
        }
        return sys;
    }

    /**
     * 重新加载配置，以支持热部署
     * @throws Exception
     */
    public void refreshConfig() throws  Exception{
        //创建properties
        Properties properties = new Properties();
        //得到config.properties的配置信息
        Resource resource = resourceLoader.getResource("classpath:config.properties");
        try {
            properties.load(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //得到vt的有效期参数
        String tokenTimeout = properties.getProperty("timeout");
        if (tokenTimeout != null){
            timeout = Integer.parseInt(tokenTimeout);
            logger.debug("config.properties设置tokenTimeout={}", tokenTimeout);
        }else{
            logger.warn("timeout参数配置错误");
        }

        //确定是否仅在安全模式下运行
        String configSecureMode = properties.getProperty("secureMode");
        if (configSecureMode != null && configSecureMode.equals("true")){
            this.secureMode = Boolean.parseBoolean(configSecureMode);
            logger.debug("已成功启动安全模式！");
        }

        // 自动登录有效期
        String configAutoLoginExpDays = properties.getProperty("autoLoginExpDays");
        if (configAutoLoginExpDays != null) {
            try {
                autoLoginExpiredDays = Integer.parseInt(configAutoLoginExpDays);
                logger.debug("config.properties设置autoLoginExpDays={}", autoLoginExpiredDays);
            } catch (NumberFormatException e) {
                logger.warn("autoLoginExpDays参数配置不正确");
            }
        }

        //加载子系统列表
        loadSystems();
    }

    /**
     * 在应用关闭的时候通知子系统进行清理工作
     * @throws Exception
     */
    public void destory() throws Exception{
        for (ClientSystem sys:clientSystems) {
            sys.noticeShutdown();
        }
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<ClientSystem> getClientSystems() {
        return clientSystems;
    }

    public void setClientSystems(List<ClientSystem> clientSystems) {
        this.clientSystems = clientSystems;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getLoginViewName() {
        return loginViewName;
    }

    public void setLoginViewName(String loginViewName) {
        this.loginViewName = loginViewName;
    }

    public IAuthenticationHandler getAuthenticationHandler() {
        return authenticationHandler;
    }

    public void setAuthenticationHandler(IAuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }

    public IPreLoginHandler getPreLoginHandler() {
        return preLoginHandler;
    }

    public void setPreLoginHandler(IPreLoginHandler preLoginHandler) {
        this.preLoginHandler = preLoginHandler;
    }

    public boolean isSecureMode() {
        return secureMode;
    }

    public int getAutoLoginExpiredDays() {
        return autoLoginExpiredDays;
    }

    public UserSerializer getUserSerializer() {
        return userSerializer;
    }

    public void setUserSerializer(UserSerializer userSerializer) {
        this.userSerializer = userSerializer;
    }
}
