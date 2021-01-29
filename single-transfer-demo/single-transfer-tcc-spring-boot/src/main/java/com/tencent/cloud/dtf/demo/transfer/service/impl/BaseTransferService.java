package com.tencent.cloud.dtf.demo.transfer.service.impl;

import com.tencent.cloud.dtf.demo.transfer.Account;
import com.tencent.cloud.dtf.demo.transfer.dao.BaseAccountDao;
import com.tencent.cloud.dtf.demo.transfer.service.ITransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 转账服务
 *
 * @author hongweizhu
 */
public abstract class BaseTransferService implements ITransferService {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTransferService.class);

    public abstract BaseAccountDao getAccountDao();

    @Override
    public void debit(Long txId, Long branchId, Account account, int amount) throws Exception {
        int createTmp = getAccountDao().createTmp(txId, branchId, account.getId(), amount);
        int tryDebit = getAccountDao().tryDebit(account.getId(), amount);

        if (createTmp == 1 && tryDebit == 1) {
            LOG.debug("Debit tried.");
        } else {
            throw new RuntimeException("Debit try failed.");
        }
    }

    @Override
    public boolean confirmDebit(Long txId, Long branchId, Account account, int amount) {
        Boolean queryTmp = getAccountDao().queryTmp(txId, branchId, account.getId());
        if (queryTmp) {
            // 有数据，进行后续处理
        } else {
            // tmp表中没有记录，进行幂等处理
            return true;
        }
        int confirmDebit = getAccountDao().confirmDebit(account.getId(), amount);
        int clearTmp = getAccountDao().deleteTmp(txId, branchId, account.getId());

        if (confirmDebit == 1 && clearTmp == 1) {
            LOG.debug("Debit confirmed.");
            return true;
        } else {
            throw new RuntimeException("Debit confirm failed.");
        }
    }

    @Override
    public boolean cancelDebit(Long txId, Long branchId, Account account, int amount) {
        Boolean queryTmp = getAccountDao().queryTmp(txId, branchId, account.getId());
        if (queryTmp) {
            // 有数据，进行后续处理
        } else {
            // tmp表中没有记录，进行幂等处理
            return true;
        }
        int cancelDebit = getAccountDao().cancelDebit(account.getId(), amount);
        int clearTmp = getAccountDao().deleteTmp(txId, branchId, account.getId());

        if (cancelDebit == 1 && clearTmp == 1) {
            LOG.debug("Debit canceled.");
            return true;
        } else {
            throw new RuntimeException("Debit cancel failed.");
        }
    }

    @Override
    public void credit(Long txId, Long branchId, Account account, int amount) throws Exception {
        int createTmp = getAccountDao().createTmp(txId, branchId, account.getId(), -amount);
        int tryCredit = getAccountDao().tryCredit(account.getId(), amount);

        if (createTmp == 1 && tryCredit == 1) {
            LOG.debug("Credit tryed.");
        } else {
            throw new RuntimeException("Credit try failed. Balance not enough.");
        }
    }

    @Override
    public boolean confirmCredit(Long txId, Long branchId, Account account, int amount) {
        Boolean queryTmp = getAccountDao().queryTmp(txId, branchId, account.getId());
        if (queryTmp) {
            // 有数据，进行后续处理
        } else {
            // tmp表中没有记录，进行幂等处理
            return true;
        }

        int confirmCredit = getAccountDao().confirmCredit(account.getId(), amount);
        int clearTmp = getAccountDao().deleteTmp(txId, branchId, account.getId());

        if (confirmCredit == 1 && clearTmp == 1) {
            LOG.debug("Credit confirmed.");
            return true;
        } else {
            throw new RuntimeException("Credit confirm failed.");
        }
    }

    @Override
    public boolean cancelCredit(Long txId, Long branchId, Account account, int amount) {
        Boolean queryTmp = getAccountDao().queryTmp(txId, branchId, account.getId());
        if (queryTmp) {
            // 有数据，进行后续处理
        } else {
            // tmp表中没有记录，进行幂等处理
            return true;
        }
        int cancelCredit = getAccountDao().cancelCredit(account.getId(), amount);
        int clearTmp = getAccountDao().deleteTmp(txId, branchId, account.getId());

        if (cancelCredit == 1 && clearTmp == 1) {
            LOG.debug("Credit canceled.");
            return true;
        } else {
            throw new RuntimeException("Credit cancel failed.");
        }
    }
}
