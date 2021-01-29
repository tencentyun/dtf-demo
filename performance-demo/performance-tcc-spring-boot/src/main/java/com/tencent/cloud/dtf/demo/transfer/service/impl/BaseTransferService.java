package com.tencent.cloud.dtf.demo.transfer.service.impl;

import com.tencent.cloud.dtf.demo.PerformanceTccSpringBootApplication;
import com.tencent.cloud.dtf.demo.transfer.Account;
import com.tencent.cloud.dtf.demo.transfer.service.ITransferService;

/**
 * 转账服务
 *
 * @author hongweizhu
 */
public abstract class BaseTransferService implements ITransferService {

    /**
     * 异常
     * 
     * @return true：异常；false：正常
     */
    private boolean injectException() {
        // 注入 二分之一
        return PerformanceTccSpringBootApplication.RANDOM.nextInt(1000) == 500;
    }

    @Override
    public void debit(Long txId, Long branchId, Account account, int amount) throws Exception {
        if (injectException()) {
            throw new RuntimeException("Inject exception occurred.");
        }
    }

    @Override
    public boolean confirmDebit(Long txId, Long branchId, Account account, int amount) {
        return !injectException();
        // return false;
    }

    @Override
    public boolean cancelDebit(Long txId, Long branchId, Account account, int amount) {
        return !injectException();
    }

    @Override
    public void credit(Long txId, Long branchId, Account account, int amount) throws Exception {
        if (injectException()) {
            throw new RuntimeException("Not enough balance.");
        }
    }

    @Override
    public boolean confirmCredit(Long txId, Long branchId, Account account, int amount) {
        return !injectException();
    }

    @Override
    public boolean cancelCredit(Long txId, Long branchId, Account account, int amount) {
        return !injectException();
    }
}
