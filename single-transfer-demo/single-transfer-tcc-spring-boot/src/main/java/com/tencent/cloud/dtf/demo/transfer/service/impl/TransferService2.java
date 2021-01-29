package com.tencent.cloud.dtf.demo.transfer.service.impl;

import com.tencent.cloud.dtf.annotation.DtfTcc;
import com.tencent.cloud.dtf.demo.transfer.Account;
import com.tencent.cloud.dtf.demo.transfer.dao.BaseAccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 转账服务
 *
 * @author hongweizhu
 */
@Service
@Qualifier("transferService2")
@Transactional(value = "secondaryTransactionManager", rollbackFor = { Throwable.class })
public class TransferService2 extends BaseTransferService {

    @Autowired
    @Qualifier("accountDao2")
    private BaseAccountDao accountDao;

    @Override
    public BaseAccountDao getAccountDao() {
        return this.accountDao;
    }

    @DtfTcc(name = "debit2", confirmMethod = "confirmDebit", cancelMethod = "cancelDebit")
    @Override
    public void debit(Long txId, Long branchId, Account account, int amount) throws Exception {
        super.debit(txId, branchId, account, amount);
    }

    @DtfTcc(name = "credit2", confirmMethod = "confirmCredit", cancelMethod = "cancelCredit")
    @Override
    public void credit(Long txId, Long branchId, Account account, int amount) throws Exception {
        super.credit(txId, branchId, account, amount);
    }

    @Override
    public boolean confirmCredit(Long txId, Long branchId, Account account, int amount) {
        return super.confirmCredit(txId, branchId, account, amount);
    }

    @Override
    public boolean confirmDebit(Long txId, Long branchId, Account account, int amount) {
        return super.confirmDebit(txId, branchId, account, amount);
    }

    @Override
    public boolean cancelCredit(Long txId, Long branchId, Account account, int amount) {
        return super.cancelCredit(txId, branchId, account, amount);
    }

    @Override
    public boolean cancelDebit(Long txId, Long branchId, Account account, int amount) {
        return super.cancelDebit(txId, branchId, account, amount);
    }
}
