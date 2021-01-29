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
    public void debit(Account account, int amount) throws Exception {
        if (getAccountDao().debit(account.getId(), amount) == 1) {
            LOG.debug("Debit tried.");
        } else {
            throw new RuntimeException("Debit try failed.");
        }
    }

    @Override
    public void credit(Account account, int amount) throws Exception {
        if (getAccountDao().credit(account.getId(), amount) == 1) {
            LOG.debug("Credit tried.");
        } else {
            throw new RuntimeException("Credit try failed. Balance not enough.");
        }
    }
}
