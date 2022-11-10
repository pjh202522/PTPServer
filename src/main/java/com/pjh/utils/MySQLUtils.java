package com.pjh.utils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLUtils {

    public static PreparedStatement getPs(Connection connection, String sql, Object [] args) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for(int i = 0;i < args.length;i++) {
            preparedStatement.setObject(i + 1, args[i]);
        }
        return preparedStatement;
    }

    public static <T> List<T> resultSetToObject(ResultSet resultSet, Class<T> clazz) throws SQLException, InstantiationException, IllegalAccessException {
        List<T> outputs = new ArrayList<>();
        ResultSetMetaData metaData = null;
        if(resultSet != null) {
            metaData = resultSet.getMetaData();
        }
        Map<String, Integer> map = new HashMap<>();
        for(int i = 1;i <= metaData.getColumnCount();i++) {
            map.put(metaData.getColumnName(i), i);
        }
        while(resultSet.next()) {
            T t = clazz.newInstance();
            Field[] declaredFields = clazz.getDeclaredFields();
            for(Field f : declaredFields) {
                f.setAccessible(true);
                if(map.containsKey(f.getName())) {
                    f.set(t, resultSet.getObject(f.getName()));
                }
            }
            outputs.add(t);
        }
        return outputs;
    }

}
