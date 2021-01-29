package com.tencent.cloud.dtf.demo.transfer.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 双数据源，写法比较复杂
 * 
 * @author hongweizhu
 */
public abstract class BaseAccountDao {

    private static final Logger LOG = LoggerFactory.getLogger(BaseAccountDao.class);

    public int debit(final int accountId, final int amount) {
        String sql = "UPDATE dtf_demo_account SET balance = balance + ? WHERE id = ?";
        try {
            return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setInt(1, amount);
                    ps.setInt(2, accountId);
                }
            });
        } catch (Exception e) {
            LOG.error("Try debit failed.", e);
            throw new RuntimeException("Try debit failed.");
        }
    }

    public int credit(final int accountId, final int amount) {
        String sql = "UPDATE dtf_demo_account SET balance = balance - ? WHERE id = ? AND balance >= ?";
        try {
            return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setInt(1, amount);
                    ps.setInt(2, accountId);
                    ps.setInt(3, amount);
                }
            });
        } catch (Exception e) {
            LOG.error("Try credit failed.", e);
            throw new RuntimeException("Try credit failed.");
        }
    }

    public abstract JdbcTemplate getJdbcTemplate();
}
