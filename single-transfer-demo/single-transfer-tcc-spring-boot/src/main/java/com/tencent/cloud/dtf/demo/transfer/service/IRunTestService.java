package com.tencent.cloud.dtf.demo.transfer.service;

import com.tencent.cloud.dtf.demo.transfer.Bank.Transfer;

public interface IRunTestService {

    void execute(Transfer transfer) throws Exception;
}
