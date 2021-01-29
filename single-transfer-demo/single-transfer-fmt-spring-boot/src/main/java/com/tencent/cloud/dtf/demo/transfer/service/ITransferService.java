package com.tencent.cloud.dtf.demo.transfer.service;

import com.tencent.cloud.dtf.demo.transfer.Account;

public interface ITransferService {

    /**
     * 应收
     * 
     * @param account 收款账户
     * @param amount 金额
     * @throws Exception 可能出现的异常
     */
    void debit(Account account, int amount) throws Exception;

    /**
     * 应付
     * 
     * @param account 付款账户
     * @param amount 金额
     * @throws Exception 可能出现的异常
     */
    void credit(Account account, int amount) throws Exception;

}
