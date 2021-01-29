package com.tencent.cloud.dtf.demo.transfer.service.impl;

import com.tencent.cloud.dtf.annotation.DtfTransactional;
import com.tencent.cloud.dtf.client.tm.context.DtfContextHolder;
import com.tencent.cloud.dtf.demo.transfer.Bank.Transfer;
import com.tencent.cloud.dtf.demo.transfer.service.IRunTestService;
import com.tencent.cloud.dtf.demo.transfer.service.ITransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RunTestService implements IRunTestService {

    private static final Logger LOG = LoggerFactory.getLogger(RunTestService.class);

    @Autowired
    @Qualifier("transferService1")
    private ITransferService transferService1;
    @Autowired
    @Qualifier("transferService2")
    private ITransferService transferService2;

    @DtfTransactional(timeout = 60 * 1000)
    @Override
    public void execute(Transfer transfer) throws Exception {
        LOG.info(DtfContextHolder.get().getTxId().toString());
        // debit在credit之前，测试回滚
        // try方法的 txId 和 branchId 两个参数传入任何值均可，框架会自动补充对应的值
        getTransferService(transfer.getTo().getId()).debit(null, null, transfer.getTo(), transfer.getAmount());
        getTransferService(transfer.getFrom().getId()).credit(null, null, transfer.getFrom(), transfer.getAmount());
    }

    private ITransferService getTransferService(int id) {
        if (id == 1) {
            return this.transferService1;
        } else {
            return this.transferService2;
        }
    }
}
