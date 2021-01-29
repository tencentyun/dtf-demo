package com.tencent.cloud.dtf.demo.transfer;

import com.tencent.cloud.dtf.client.tm.context.DtfTccBranch;
import com.tencent.cloud.dtf.client.tm.context.DtfTransaction;
import com.tencent.cloud.dtf.demo.transfer.service.ITransferService;
import com.tencent.cloud.dtf.demo.transfer.service.impl.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 虚拟BANK
 *
 * @author hongweizhu
 */
public class Bank {

    public static Transfer transfer() {
        return new Transfer();
    }

    public static class Transfer {

        private static final Logger LOG = LoggerFactory.getLogger(Transfer.class);

        private Account from;
        private Account to;
        private int amount;

        private ITransferService transferService = new TransferService();

        public Transfer from(Account from) {
            this.from = from;
            return this;
        }

        public Transfer to(Account to) {
            this.to = to;
            return this;
        }

        public Transfer amount(int amount) {
            this.amount = amount;
            return this;
        }

        public boolean execute() {
            if (null == this.from || null == this.to || this.amount <= 0) {
                throw new RuntimeException("Invalid arguments.");
            }
            Long txId = DtfTransaction.begin(10000);
            try {
                // 记录应收，此处只上报业务参数，不上报系统参数。
                Long branchId1 = DtfTccBranch.begin("com.tencent.cloud.dtf.demo.transfer.TransferService.debit",
                        new Object[] { null, null, this.to, this.amount });
                transferService.debit(txId, branchId1, this.to, this.amount);
                DtfTccBranch.checkTxTrying();
                DtfTccBranch.end();
                // 记录应付，此处只上报业务参数，不上报系统参数。
                Long branchId2 = DtfTccBranch.begin("com.tencent.cloud.dtf.demo.transfer.TransferService.credit",
                        new Object[] { null, null, this.from, this.amount });
                transferService.credit(txId, branchId2, this.from, this.amount);
                DtfTccBranch.checkTxTrying();
                DtfTccBranch.end();
                // 提交主事务
                DtfTransaction.commit();
                return true;
            } catch (Throwable e) {
                // 回滚主事务
                DtfTransaction.rollback();
                LOG.error("Bank transfer failed.", e);
                return false;
            } finally {
                DtfTransaction.end();
            }
        }
    }
}
