package com.rhwayfun.sso.client;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 反序列化JSON格式数据 created by rhwayfun
 */
public class JsonUserDeserailizer implements UserDeserializer {

    private final ObjectMapper mapper = new ObjectMapper();

    // 反序列化
    @SuppressWarnings("unchecked")
    @Override
    public SSOUser deserail(String userData) throws Exception {
        JsonNode rootNode = mapper.readTree(userData);

        String id = rootNode.get("id").textValue();
        if (id == null) {
            return null;
        } else {
            SSOUserImpl user = new SSOUserImpl(id);
            JsonNode properties = rootNode.get("properties");
            Map<String, Object> propertyMap = mapper.readValue(properties.toString(), HashMap.class);
            user.setProperties(propertyMap);
            return user;
        }
    }

}
