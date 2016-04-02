package com.rhwayfun.sso.client;

/**
 * 将服务端传来的user数据反序列化 created by rhwayfun
 */
public interface UserDeserializer {

    /**
     * 反序列化
     * 
     * @param userDate
     * @return
     * @throws Exception
     */
    SSOUser deserail(String userDate) throws Exception;
}
