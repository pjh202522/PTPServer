package com.pjh.pool;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.pjh.anno.YmlValue;
import com.pjh.utils.YmlUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yueyinghaibao
 */
public class MysqlPoolFactory {

    @YmlValue("database.pool.driver")
    private String driver;

    @YmlValue("database.pool.url")
    private String url;

    @YmlValue("database.pool.username")
    private String username;

    @YmlValue("database.pool.password")
    private String password;

    @YmlValue("database.pool.maxActive")
    private Integer maxActive;

    @YmlValue("database.pool.initialSize")
    private Integer initialSize;

    public DataSource initialize() {
        YmlUtils.getYmlValue(this, MysqlPoolFactory.class);

        Map<String, Object> map = new HashMap<>(5);
        map.put("driverClassName", driver);
        map.put("url", url);
        map.put("username", username);
        map.put("password", password);
        map.put("maxActive", "" + maxActive);
        map.put("initialSize", "" + initialSize);
        try {
            return DruidDataSourceFactory.createDataSource(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
