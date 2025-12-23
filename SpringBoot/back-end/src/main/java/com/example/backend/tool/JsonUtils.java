package com.example.backend.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    //  List 转 JSON
    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    //  JSON 转 List
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        try {
            if (json == null || json.trim().isEmpty()) {
                return new ArrayList<>(); //空字符串时返回空列表
            }
            return mapper.readValue(json,
                    mapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }
}