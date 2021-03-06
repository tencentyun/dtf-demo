package com.tencent.cloud.dtf.demo.transfer.service.impl;

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

    @Override
    public void debit(Account account, int amount) throws Exception {
        super.debit(account, amount);
    }

    @Override
    public void credit(Account account, int amount) throws Exception {
        super.credit(account, amount);
    }
}
