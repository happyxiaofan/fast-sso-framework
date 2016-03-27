package com.rhwayfun.sso.server.util;

import com.rhwayfun.sso.server.model.ClientSystem;
import com.rhwayfun.sso.server.model.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rhwayfun on 16-3-23.
 */
public class TokenManager {
    //创建一个日志记录器
    private static Logger logger = LoggerFactory.getLogger(Config.class);
    private static final Map<String,Token> DATA_MAP = new ConcurrentHashMap<>();
    private static final Config config = SpringContextUtil.getBean(Config.class);
    //创建一个定时任务,true表示该定时任务是作为后台线程或者说守护线程执行的
    private static final Timer timer = new Timer(true);
    //在初始化的一分钟后启动定时任务
    static {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (Map.Entry<String,Token> entry:DATA_MAP.entrySet()) {
                    //vt
                    String vt = entry.getKey();
                    //token
                    Token token = entry.getValue();
                    //登陆用户
                    LoginUser loginUser = token.loginUser;
                    //过期时间
                    Date expired = token.expired;
                    //当前的时间
                    Date now = new Date();
                    if (now.compareTo(expired) > 0){//说明token过期了
                        /**
                         * 因为令牌是支持自动延期的，在采用客户端缓存机制后，每个客户端都会
                         * 把最后的访问时间记录在客户端自己的缓存中。在服务端发现当前的令牌
                         * 过期后会想客户端发送一个通知，客户端收到通知后，会将客户端自己的
                         * 最后访问时间加上过期时间与当前时间进行比较，如果过期了那么就清除
                         * 过期的令牌；如果没有过期那么就把所有客户端的过期时间设为所有客户
                         * 端过期时间最久的时间作为最终的过期时间
                         */
                        //获得所有的业务子系统
                        List<ClientSystem> clientSystems = config.getClientSystems();
                        //定义所有客户端系统的最大过期时间
                        Date maxExpired = expired;
                        for (ClientSystem client : clientSystems) {
                            //得到客户端的过期时间
                            Date clientExpired = client.noticeTimeout(vt,config.getTimeout());
                            if (clientExpired != null && clientExpired.compareTo(now) > 0){
                                maxExpired = clientExpired.compareTo(maxExpired) > 0 ? clientExpired : maxExpired;
                            }
                        }
                        //把所有客户端最长的过期时间与当前时间进行比较
                        if (maxExpired.compareTo(now) > 0){//说明没有过期
                            logger.debug("更新过期时间为：" + maxExpired);
                            token.expired = maxExpired;
                        }else{//说明过期了
                            logger.debug("清除过期的令牌：" + token);
                            DATA_MAP.remove(vt);
                        }
                    }
                }
            }
        }, 60 * 1000, 60 * 1000);//第二个参数表示TokenManager实例创建后一分钟开始执行定时任务，以后每隔一分钟执行一次
    }

    /**
     * 令牌对象：还有LoginUser和过期时间
     */
    private static class Token{
        private LoginUser loginUser;
        private Date expired;
    }

    private TokenManager(){}
    /**
     * 从全局map中取得vt对应的登陆用户
     * @param vt
     * @return
     */
    public static LoginUser validate(String vt) {
        Token token = DATA_MAP.get(vt);
        return token == null ? null : token.loginUser;
    }

    /**
     * 向客户端生成一个令牌
     * @param vt
     * @param user
     */
    public static void addToken(String vt, LoginUser user) {
        Token token = new Token();
        token.loginUser = user;
        token.expired = new Date(new Date().getTime() + config.getTimeout() * 60 * 1000);
        DATA_MAP.put(vt,token);
    }

    /**
     * 让一个vt失效
     * @param vt
     */
    public static void invalid(String vt){
        if (vt != null){
            DATA_MAP.remove(vt);
        }
    }
}
