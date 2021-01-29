package com.tencent.cloud.dtf.demo.transfer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DBUtil.class);

    private static final String URL1 =
            "jdbc:mysql://127.0.0.1:3306/dtf_demo_account_1?useSSL=false&characterEncoding=utf8&serverTimezone=GMT";
    private static final String USER1 = "";
    private static final String PASSWORD1 = "";

    private static final String URL2 =
            "jdbc:mysql://127.0.0.1:3306/dtf_demo_account_2?useSSL=false&characterEncoding=utf8&serverTimezone=GMT";
    private static final String USER2 = "";
    private static final String PASSWORD2 = "";

    public static Connection get(int dbIndex) {
        Connection conn = null;
        try {
            switch (dbIndex) {
            case 1:
                conn = DriverManager.getConnection(URL1, USER1, PASSWORD1);
                conn.setAutoCommit(false);
                break;
            case 2:
                conn = DriverManager.getConnection(URL2, USER2, PASSWORD2);
                conn.setAutoCommit(false);
                break;
            default:
                break;
            }
            return conn;
        } catch (SQLException e) {
            LOG.error("Failed create connection.", e);
            return null;
        }
    }

    public static void close(Connection conn) {
        if (null == conn) {
            return;
        }
        // 强制回滚
        try {
            conn.rollback();
        } catch (SQLException e) {
            LOG.error("Failed close connection.", e);
        }
        try {
            conn.close();
        } catch (SQLException e) {
            LOG.error("Failed close connection.", e);
        }
    }
}
