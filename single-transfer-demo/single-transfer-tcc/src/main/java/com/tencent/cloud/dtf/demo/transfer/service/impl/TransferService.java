package com.tencent.cloud.dtf.demo.transfer.service.impl;

import com.tencent.cloud.dtf.demo.transfer.Account;
import com.tencent.cloud.dtf.demo.transfer.service.ITransferService;
import com.tencent.cloud.dtf.demo.transfer.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 转账服务
 *
 * @author hongweizhu
 */
public class TransferService implements ITransferService {

    private static final Logger LOG = LoggerFactory.getLogger(TransferService.class);

    private static final String CREATE_TMP =
            "INSERT INTO dtf_demo_account_tmp (tx_id, branch_id, account_id, amount) VALUES (?, ?, ?, ?)";

    private static final String QUERY_TMP =
            "SELECT account_id, amount FROM dtf_demo_account_tmp WHERE tx_id = ? AND branch_id = ? FOR UPDATE";

    private static final String CLEAR_TMP =
            "DELETE FROM dtf_demo_account_tmp WHERE tx_id = ? AND branch_id = ?";

    private static final String TRY_DEBIT = "UPDATE dtf_demo_account SET incoming = incoming + ? WHERE id = ?";

    /**
     * 应收
     * 
     * @param txId 主事务ID
     * @param branchId 分支事务ID
     * @param account 收款账户
     * @param amount 金额
     * @throws Exception 可能出现的异常
     */
    public void debit(Long txId, Long branchId, Account account, int amount) throws Exception {
        Connection conn = DBUtil.get(account.getId());
        try {
            PreparedStatement createTmpPs = conn.prepareStatement(CREATE_TMP);
            createTmpPs.setLong(1, txId);
            createTmpPs.setLong(2, branchId);
            createTmpPs.setInt(3, account.getId());
            createTmpPs.setInt(4, amount);

            PreparedStatement tryPs = conn.prepareStatement(TRY_DEBIT);
            tryPs.setInt(1, amount);
            tryPs.setInt(2, account.getId());

            if (createTmpPs.executeUpdate() == 1 && tryPs.executeUpdate() == 1) {
                conn.commit();
                LOG.info("Debit tryed.");
            } else {
                LOG.warn("Debit try failed.");
                throw new RuntimeException("Account not found.");
            }
        } catch (Exception e) {
            LOG.error("Debit failed.", e);
            throw e;
        } finally {
            DBUtil.close(conn);
        }
    }

    private static final String CONFIRM_DEBIT =
            "UPDATE dtf_demo_account SET balance = balance + ?, incoming = incoming - ? WHERE id = ?";

    public boolean confirmDebit(Long txId, Long branchId, Account account, int amount) {
        Connection conn = DBUtil.get(account.getId());
        try {
            PreparedStatement queryTmpPs = conn.prepareStatement(QUERY_TMP);
            queryTmpPs.setLong(1, txId);
            queryTmpPs.setLong(2, branchId);
            ResultSet rs = queryTmpPs.executeQuery();
            if (null != rs && rs.next()) {
                // 有数据，进行后续处理
            } else {
                // tmp表中没有记录，进行幂等处理
                return true;
            }

            PreparedStatement confirmPs = conn.prepareStatement(CONFIRM_DEBIT);
            confirmPs.setInt(1, amount);
            confirmPs.setInt(2, amount);
            confirmPs.setInt(3, account.getId());

            PreparedStatement clearTmpPs = conn.prepareStatement(CLEAR_TMP);
            clearTmpPs.setLong(1, txId);
            clearTmpPs.setLong(2, branchId);

            if (confirmPs.executeUpdate() == 1 && clearTmpPs.executeUpdate() == 1) {
                conn.commit();
                LOG.info("Debit confirmed.");
                return true;
            } else {
                conn.rollback();
                LOG.warn("Debit confirm failed.");
                return false;
            }
        } catch (Exception e) {
            LOG.error("Debit confirm failed.", e);
            return false;
        } finally {
            DBUtil.close(conn);
        }
    }

    private static final String CANCEL_DEBIT =
            "UPDATE dtf_demo_account SET incoming = incoming - ? WHERE id = ?";

    public boolean cancelDebit(Long txId, Long branchId, Account account, int amount) {
        Connection conn = DBUtil.get(account.getId());
        try {
            PreparedStatement queryTmpPs = conn.prepareStatement(QUERY_TMP);
            queryTmpPs.setLong(1, txId);
            queryTmpPs.setLong(2, branchId);
            ResultSet rs = queryTmpPs.executeQuery();
            if (null != rs && rs.next()) {
                // 有数据，进行后续处理
            } else {
                // tmp表中没有记录，进行幂等处理
                return true;
            }

            PreparedStatement cancelPs = conn.prepareStatement(CANCEL_DEBIT);
            cancelPs.setInt(1, amount);
            cancelPs.setInt(2, account.getId());

            PreparedStatement clearTmpPs = conn.prepareStatement(CLEAR_TMP);
            clearTmpPs.setLong(1, txId);
            clearTmpPs.setLong(2, branchId);

            if (cancelPs.executeUpdate() == 1 && clearTmpPs.executeUpdate() == 1) {
                conn.commit();
                LOG.info("Debit canceled.");
                return true;
            } else {
                conn.rollback();
                LOG.warn("Debit cancel failed.");
                return false;
            }
        } catch (Exception e) {
            LOG.error("Debit cancel failed.", e);
            return false;
        } finally {
            DBUtil.close(conn);
        }
    }

    private static final String TRY_CREDIT =
            "UPDATE dtf_demo_account SET frozen = frozen + ? WHERE id = ? AND balance >= frozen + ?";

    /**
     * 应付
     * 
     * @param txId 主事务ID
     * @param branchId 分支事务ID
     * @param account 付款账户
     * @param amount 金额
     * @throws Exception 可能出现的异常
     */
    public void credit(Long txId, Long branchId, Account account, int amount) throws Exception {
        Connection conn = DBUtil.get(account.getId());
        try {
            PreparedStatement createTmpPs = conn.prepareStatement(CREATE_TMP);
            createTmpPs.setLong(1, txId);
            createTmpPs.setLong(2, branchId);
            createTmpPs.setInt(3, account.getId());
            createTmpPs.setInt(4, 0 - amount);

            PreparedStatement tryCreditPs = conn.prepareStatement(TRY_CREDIT);
            tryCreditPs.setInt(1, amount);
            tryCreditPs.setInt(2, account.getId());
            tryCreditPs.setInt(3, amount);

            if (createTmpPs.executeUpdate() == 1 && tryCreditPs.executeUpdate() == 1) {
                conn.commit();
                LOG.info("Credit tryed.");
            } else {
                throw new RuntimeException("Not enough balance.");
            }
        } catch (Exception e) {
            LOG.error("Credit try failed.", e);
            throw e;
        } finally {
            DBUtil.close(conn);
        }
    }

    private static final String CONFIRM_CREDIT =
            "UPDATE dtf_demo_account SET balance = balance - ?, frozen = frozen - ? WHERE id = ?";

    public boolean confirmCredit(Long txId, Long branchId, Account account, int amount) {
        Connection conn = DBUtil.get(account.getId());
        try {
            PreparedStatement queryTmpPs = conn.prepareStatement(QUERY_TMP);
            queryTmpPs.setLong(1, txId);
            queryTmpPs.setLong(2, branchId);
            ResultSet rs = queryTmpPs.executeQuery();
            if (null != rs && rs.next()) {
                // 有数据，进行后续处理
            } else {
                // tmp表中没有记录，进行幂等处理
                return true;
            }

            PreparedStatement confirmCreditPs = conn.prepareStatement(CONFIRM_CREDIT);
            confirmCreditPs.setInt(1, amount);
            confirmCreditPs.setInt(2, amount);
            confirmCreditPs.setInt(3, account.getId());

            PreparedStatement clearTmpPs = conn.prepareStatement(CLEAR_TMP);
            clearTmpPs.setLong(1, txId);
            clearTmpPs.setLong(2, branchId);

            if (confirmCreditPs.executeUpdate() == 1 && clearTmpPs.executeUpdate() == 1) {
                conn.commit();
                LOG.info("Credit confirmed.");
                return true;
            } else {
                conn.rollback();
                LOG.info("Credit confirm failed.");
                return false;
            }
        } catch (Exception e) {
            LOG.error("Credit confirm failed.", e);
            return false;
        } finally {
            DBUtil.close(conn);
        }
    }

    private static final String CANCEL_CREDIT = "UPDATE dtf_demo_account SET frozen = frozen - ? WHERE id = ?";

    public boolean cancelCredit(Long txId, Long branchId, Account account, int amount) {
        Connection conn = DBUtil.get(account.getId());
        try {
            PreparedStatement queryTmpPs = conn.prepareStatement(QUERY_TMP);
            queryTmpPs.setLong(1, txId);
            queryTmpPs.setLong(2, branchId);
            ResultSet rs = queryTmpPs.executeQuery();
            if (null != rs && rs.next()) {
                // 有数据，进行后续处理
            } else {
                // tmp表中没有记录，进行幂等处理
                return true;
            }

            PreparedStatement cancelCreditPs = conn.prepareStatement(CANCEL_CREDIT);
            cancelCreditPs.setInt(1, amount);
            cancelCreditPs.setInt(2, account.getId());

            PreparedStatement clearTmpPs = conn.prepareStatement(CLEAR_TMP);
            clearTmpPs.setLong(1, txId);
            clearTmpPs.setLong(2, branchId);

            if (cancelCreditPs.executeUpdate() == 1 && clearTmpPs.executeUpdate() == 1) {
                conn.commit();
                LOG.info("Credit canceled.");
                return true;
            } else {
                conn.rollback();
                LOG.info("Credit cancel failed.");
                return false;
            }
        } catch (Exception e) {
            LOG.error("Credit cancel failed.", e);
            return false;
        } finally {
            DBUtil.close(conn);
        }
    }
}
