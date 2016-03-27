package com.rhwayfun.sso.common;

import java.net.URLEncoder;
import java.util.UUID;

/**
 * Created by rhwayfun on 16-3-23.
 */
public class StringUtil {
    private StringUtil(){}

    /**
     * 根据参数拼接url
     * @param originUrl
     * @param parameterName
     * @param parameterValue
     * @return
     * @throws Exception
     */
    public static String appendUrlParameter(String originUrl,String parameterName,String parameterValue) throws Exception{
        if (originUrl == null) return null;
        //判断原来的url中是否有？连接符
        String bound = originUrl.contains("?") ? "&" : "?";
        return originUrl + bound + parameterName + "=" + URLEncoder.encode(parameterValue,"utf-8");
    }

    /**
     * 生成32位长度的随机字符串作为key
     * @return
     * @throws Exception
     */
    public static String uniqueKey() throws Exception{
        return UUID.randomUUID().toString().replace("-","").toLowerCase();
    }
}
