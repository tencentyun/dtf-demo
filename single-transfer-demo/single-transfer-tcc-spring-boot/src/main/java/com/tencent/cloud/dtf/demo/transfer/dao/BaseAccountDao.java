package com.tencent.cloud.dtf.demo.transfer.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 双数据源，写法比较复杂
 * 
 * @author hongweizhu
 */
public abstract class BaseAccountDao {

    private static final Logger LOG = LoggerFactory.getLogger(BaseAccountDao.class);

    public int createTmp(final Long txId, final Long branchId, final int accountId, final int amount) {
        String sql = "INSERT INTO dtf_demo_account_tmp (tx_id, branch_id, account_id, amount) VALUES (?, ?, ?, ?)";
        try {
            return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setLong(1, txId);
                    ps.setLong(2, branchId);
                    ps.setInt(3, accountId);
                    ps.setInt(4, amount);
                }
            });
        } catch (Exception e) {
            LOG.error("Create tmp failed.", e);
            throw new RuntimeException("Create tmp failed.");
        }
    }

    public Boolean queryTmp(final Long txId, final Long branchId, int accountId) {
        String sql = "SELECT account_id, amount FROM dtf_demo_account_tmp WHERE tx_id = ? AND branch_id = ? FOR UPDATE";
        try {
            return getJdbcTemplate().query(sql, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setLong(1, txId);
                    ps.setLong(2, branchId);
                }
            }, new ResultSetExtractor<Boolean>() {
                public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
                    return rs != null && rs.next();
                }
            });
        } catch (Exception e) {
            LOG.error("Query tmp failed.", e);
            throw new RuntimeException("Query tmp failed.");
        }
    }

    public int deleteTmp(final Long txId, final Long branchId, int accountId) {
        String sql = "DELETE FROM dtf_demo_account_tmp WHERE tx_id = ? AND branch_id = ?";
        try {
            return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setLong(1, txId);
                    ps.setLong(2, branchId);
                }
            });
        } catch (Exception e) {
            LOG.error("Delete tmp failed.", e);
            throw new RuntimeException("Delete tmp failed.");
        }
    }

    public int tryDebit(final int accountId, final int amount) {
        String sql = "UPDATE dtf_demo_account SET incoming = incoming + ? WHERE id = ?";
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

    public int confirmDebit(final int accountId, final int amount) {
        String sql = "UPDATE dtf_demo_account SET balance = balance + ?, incoming = incoming - ? WHERE id = ?";
        try {
            return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setInt(1, amount);
                    ps.setInt(2, amount);
                    ps.setInt(3, accountId);
                }
            });
        } catch (Exception e) {
            LOG.error("Confirm debit failed.", e);
            throw new RuntimeException("Confirm debit failed.");
        }
    }

    public int cancelDebit(final int accountId, final int amount) {
        String sql = "UPDATE dtf_demo_account SET incoming = incoming - ? WHERE id = ?";
        try {
            return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setInt(1, amount);
                    ps.setInt(2, accountId);
                }
            });
        } catch (Exception e) {
            LOG.error("Cancel debit failed.", e);
            throw new RuntimeException("Cancel debit failed.");
        }
    }

    public int tryCredit(final int accountId, final int amount) {
        String sql = "UPDATE dtf_demo_account SET frozen = frozen + ? WHERE id = ? AND balance >= frozen + ?";
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

    public int confirmCredit(final int accountId, final int amount) {
        String sql = "UPDATE dtf_demo_account SET balance = balance - ?, frozen = frozen - ? WHERE id = ?";
        try {
            return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setInt(1, amount);
                    ps.setInt(2, amount);
                    ps.setInt(3, accountId);
                }
            });
        } catch (Exception e) {
            LOG.error("Confirm credit failed.", e);
            throw new RuntimeException("Confirm credit failed.");
        }
    }

    public int cancelCredit(final int accountId, final int amount) {
        String sql = "UPDATE dtf_demo_account SET frozen = frozen - ? WHERE id = ?";
        try {
            return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setInt(1, amount);
                    ps.setInt(2, accountId);
                }
            });
        } catch (Exception e) {
            LOG.error("Cancel credit failed.", e);
            throw new RuntimeException("Cancel credit failed.");
        }
    }

    public abstract JdbcTemplate getJdbcTemplate();
}
