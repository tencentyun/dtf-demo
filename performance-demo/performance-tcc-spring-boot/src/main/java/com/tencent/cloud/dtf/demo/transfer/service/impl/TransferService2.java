package com.tencent.cloud.dtf.demo.transfer.service.impl;

import com.tencent.cloud.dtf.annotation.DtfTcc;
import com.tencent.cloud.dtf.demo.transfer.Account;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 转账服务
 *
 * @author hongweizhu
 */
@Service
@Qualifier("transferService2")
public class TransferService2 extends BaseTransferService {

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
}
