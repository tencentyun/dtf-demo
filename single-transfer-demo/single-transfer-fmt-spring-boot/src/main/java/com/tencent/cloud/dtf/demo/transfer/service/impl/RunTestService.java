package com.tencent.cloud.dtf.demo.transfer.service.impl;

import com.tencent.cloud.dtf.annotation.DtfFmt;
import com.tencent.cloud.dtf.annotation.DtfTransactional;
import com.tencent.cloud.dtf.demo.transfer.Bank.Transfer;
import com.tencent.cloud.dtf.demo.transfer.service.IRunTestService;
import com.tencent.cloud.dtf.demo.transfer.service.ITransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RunTestService implements IRunTestService {

    @Autowired
    @Qualifier("transferService1")
    private ITransferService transferService1;
    @Autowired
    @Qualifier("transferService2")
    private ITransferService transferService2;

    @DtfTransactional
    @DtfFmt
    @Override
    public void execute(Transfer transfer) throws Exception {
        // debit在credit之前，测试回滚
        getTransferService(transfer.getTo().getId()).debit(transfer.getTo(), transfer.getAmount());
        getTransferService(transfer.getFrom().getId()).credit(transfer.getFrom(), transfer.getAmount());
        // 故障注入
        exceptionInject();
    }

    private ITransferService getTransferService(int id) {
        if (id == 1) {
            return this.transferService1;
        } else {
            return this.transferService2;
        }
    }

    private static final Random RAND = new Random();

    private static void exceptionInject() {
        if (RAND.nextInt(100) == 50) {
            throw new RuntimeException("Random exception inject.");
        }
    }
}
