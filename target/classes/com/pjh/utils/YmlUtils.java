package com.pjh.utils;

import com.pjh.anno.YmlValue;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @author yueyinghaibao
 */

public class YmlUtils {

    private static final String YML_PATH = "src/main/resources/application.yml";

    public static HashMap<String, Object> ymlMap;

    static {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(YML_PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ymlMap = new Yaml().load(inputStream);
    }

    public static <T> void getYmlValue(Object obj, Class<T> clazz){
        Field[] declaredFields = clazz.getDeclaredFields();

        for(Field f : declaredFields) {
            f.setAccessible(true);
            if(f.isAnnotationPresent(YmlValue.class)) {
                YmlValue ymlValue = f.getAnnotation(YmlValue.class);
                String[] split = ymlValue.value().split("\\.");

                Object res = ymlMap.get(split[0]);
                for (int i = 1;i < split.length;i++) {
                    if(res instanceof HashMap) {
                        res = ((HashMap<String, Object>) res).get(split[i]);
                    }
                }

                try {
                    f.set(obj, res);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
