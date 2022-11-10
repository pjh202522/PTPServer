package com.pjh.pool;

import lombok.extern.log4j.Log4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author yueyinghaibao
 */
@Log4j
public class MysqlPoolUtils {

    private static DataSource dataSource;

    public static void newMysqlPool() {
        if(dataSource == null) {
            dataSource = new MysqlPoolFactory().initialize();
        }
    }

    public static boolean existMysqlPool() {
        return dataSource != null;
    }

    public static Connection getConnection() {
        try {
            if(dataSource != null) {
                return dataSource.getConnection();
            } else {
                log.warn("线程池未初始化");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close(ResultSet resultSet, Statement statement, Connection connection) throws SQLException {
        if(resultSet != null) {
            resultSet.close();
        }
        if(statement != null) {
            statement.close();
        }
        if(connection != null) {
            connection.close();
        }
    }

}
