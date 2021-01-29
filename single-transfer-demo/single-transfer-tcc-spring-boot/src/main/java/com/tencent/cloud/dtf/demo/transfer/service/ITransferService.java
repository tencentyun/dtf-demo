package com.tencent.cloud.dtf.demo.transfer.service;

import com.tencent.cloud.dtf.demo.transfer.Account;

public interface ITransferService {

    /**
     * 应收
     * 
     * @param txId 主事务ID
     * @param branchId 分支事务ID
     * @param account 收款账户
     * @param amount 金额
     * @throws Exception 可能出现的异常
     */
    void debit(Long txId, Long branchId, Account account, int amount) throws Exception;

    boolean confirmDebit(Long txId, Long branchId, Account account, int amount);

    boolean cancelDebit(Long txId, Long branchId, Account account, int amount);

    /**
     * 应付
     * 
     * @param txId 主事务ID
     * @param branchId 分支事务ID
     * @param account 付款账户
     * @param amount 金额
     * @throws Exception 可能出现的异常
     */
    void credit(Long txId, Long branchId, Account account, int amount) throws Exception;

    boolean confirmCredit(Long txId, Long branchId, Account account, int amount);

    boolean cancelCredit(Long txId, Long branchId, Account account, int amount);

}
