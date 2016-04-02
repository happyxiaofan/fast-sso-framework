package com.rhwayfun.sso.client;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 令牌管理工具
 */
public class TokenManager {

    // 复合结构体，含SSOUser与最后访问时间lastAccessTime两个成员
    private static class Token {
        private SSOUser user;
        private Date lastAccessTime;
    }

    // 缓存Map
    private final static Map<String, Token> LOCAL_CACHE = new HashMap<String, Token>();

    static String serverIndderAddress; // 服务端内网通信地址

    private TokenManager() {
    }

    /**
     * 验证vt有效性
     * 
     * @param vt
     * @return
     * @throws Exception
     */
    public static SSOUser validate(String vt) throws Exception {

        SSOUser user = localValidate(vt);

        if (user == null) {
            user = remoteValidate(vt);
        }

        return user;
    }

    /**
     * 在本地缓存验证有效性
     * @param vt
     * @return
     */
    private static SSOUser localValidate(String vt) {

        // 从缓存中查找数据
        Token token = LOCAL_CACHE.get(vt);

        if (token != null) { // 用户数据存在
            // 更新最后访问时间
            token.lastAccessTime = new Date();
            // 返回结果
            return token.user;
        }

        return null;
    }

    /**
     * 远程验证成功后写入本地缓存中
     * @param vt
     * @param user
     */
    private static void cacheUser(String vt, SSOUser user) {
        Token token = new Token();
        token.user = user;
        token.lastAccessTime = new Date();
        LOCAL_CACHE.put(vt, token);
    }

    /**
     * 验证远程vt的有效性
     * @param vt
     * @return
     * @throws Exception
     */
    private static SSOUser remoteValidate(String vt) throws Exception {

        URL url = new URL(serverIndderAddress + "/validate_service?vt=" + vt);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        InputStream is = conn.getInputStream();
        conn.connect();

        byte[] buff = new byte[is.available()];
        is.read(buff);
        String ret = new String(buff, "utf-8");

        conn.disconnect();
        is.close();

        UserDeserializer userDeserializer = UserDeserailizerFactory.create();
        SSOUser user = userDeserializer.deserail(ret);

        if (user != null) {
            // 处理本地缓存
            cacheUser(vt, user);
        }

        return user;
    }

    /**
     * 处理服务端发送的timeout通知
     * 
     * @param vt
     * @param tokenTimeout
     * @return
     */
    public static Date timeout(String vt, int tokenTimeout) {

        Token token = LOCAL_CACHE.get(vt);

        if (token != null) {
            Date lastAccessTime = token.lastAccessTime;
            // 最终过期时间
            Date expires = new Date(lastAccessTime.getTime() + tokenTimeout * 60 * 1000);
            Date now = new Date();

            if (expires.compareTo(now) < 0) { // 已过期
                // 从本地缓存移除
                LOCAL_CACHE.remove(vt);
                // 返回null表示此客户端缓存已过期
                return null;
            } else {
                return expires;
            }
        } else {
            return null;
        }
    }

    /**
     * 用户退出时失效对应缓存
     * 
     * @param vt
     */
    public static void invalidate(String vt) {
        // 从本地缓存移除
        LOCAL_CACHE.remove(vt);
    }

    /**
     * 服务端应用关闭时清空本地缓存，失效所有信息
     */
    public static void destroy() {
        LOCAL_CACHE.clear();
    }

}
