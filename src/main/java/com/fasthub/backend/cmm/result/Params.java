package com.fasthub.backend.cmm.result;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class Params extends HashMap<String, Object> {

    // 기본 생성자
    public Params() {
        super();
    }

    // 초기 용량 지정 생성자
    public Params(int initialCapacity) {
        super(initialCapacity);
    }

    // 편의 메서드 추가 가능

    /**
     * String 타입으로 값 가져오기
     */
    public String getString(String key) {
        Object value = get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Integer 타입으로 값 가져오기
     */
    public Integer getInt(String key) {
        Object value = get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof String) return Integer.parseInt((String) value);
        return null;
    }

    /**
     * Long 타입으로 값 가져오기
     */
    public Long getLong(String key) {
        Object value = get(key);
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof String) return Long.parseLong((String) value);
        return null;
    }

    /**
     * Boolean 타입으로 값 가져오기
     */
    public Boolean getBoolean(String key) {
        Object value = get(key);
        if (value == null) return null;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) return Boolean.parseBoolean((String) value);
        return null;
    }

    /**
     * 기본값과 함께 값 가져오기
     */
    public String getString(String key, String defaultValue) {
        String value = getString(key);
        return value != null ? value : defaultValue;
    }

    public Integer getInt(String key, Integer defaultValue) {
        Integer value = getInt(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 체이닝을 위한 put 메서드
     */
    public Params add(String key, Object value) {
        put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "Params" + super.toString();
    }
}
