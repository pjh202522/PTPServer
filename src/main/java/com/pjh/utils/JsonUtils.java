package com.pjh.utils;

import com.alibaba.fastjson2.JSON;
import lombok.extern.log4j.Log4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author yueyinghaibao
 */
@Log4j
public class JsonUtils {

    public static <T> T jsonToObject(Class<T> clazz, String json){
        T obj = null;
        try {
            Map<String, Object> map = (Map<String, Object>) JSON.parse(json);
            Constructor<T> constructor = clazz.getConstructor();
            obj = constructor.newInstance();
            Field[] fields = clazz.getDeclaredFields();

            for(Field f : fields) {
                f.setAccessible(true);
                if(map.containsKey(f.getName())) {
                    Object o = map.get(f.getName());
                    if(f.getName().contains("status")) {
                        f.set(obj, ((Integer) o).byteValue());
                        continue;
                    }
                    f.set(obj, o);
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return obj;
    }
}
