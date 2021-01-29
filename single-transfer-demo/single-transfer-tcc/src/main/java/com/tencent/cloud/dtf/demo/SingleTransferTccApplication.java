package com.tencent.cloud.dtf.demo;

import com.tencent.cloud.dtf.client.DtfClient;
import com.tencent.cloud.dtf.client.config.DtfEnv;
import com.tencent.cloud.dtf.client.rm.tcc.TccRegistry;
import com.tencent.cloud.dtf.client.rm.tcc.TccRegistry.TCC;
import com.tencent.cloud.dtf.demo.transfer.Account;
import com.tencent.cloud.dtf.demo.transfer.Bank;
import com.tencent.cloud.dtf.demo.transfer.service.ITransferService;
import com.tencent.cloud.dtf.demo.transfer.service.impl.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class SingleTransferTccApplication {

    /**
     * 测试线程并发数
     */
    private static final int TEST_THREAD_QTY = 5;
    /**
     * 每线程执行次数
     */
    private static final int TEST_TIMES_PER_THREAD = 100;

    private static final Account A1 = new Account(1);
    private static final Account A2 = new Account(2);

    public static Random RANDOM = new Random();

    public static void main(String[] args) {
        // 设置参数
        initEnv();
        // 注册TCC
        registerTcc();
        // 启动分布式事务客户端
        DtfClient.start();
        // 执行测试
        test();
    }

    /**
     * 设置启动参数
     */
    private static void initEnv() {
        // 设置参数
        DtfEnv.setServer("single-transfer-tcc");
        DtfEnv.setSecretId("sid");
        DtfEnv.setSecretKey("skey");
        DtfEnv.addTxmBroker("group-xxxxxxxx", "");
    }

    /**
     * 注册TCC信息
     */
    private static void registerTcc() {
        ITransferService transferService = new TransferService();
        // debit的confirm和cancel
        TccRegistry.register("com.tencent.cloud.dtf.demo.transfer.TransferService.debit",
                TCC.getInstance(transferService, "confirmDebit", transferService, "cancelDebit"));
        // credit的confirm和cancel
        TccRegistry.register("com.tencent.cloud.dtf.demo.transfer.TransferService.credit",
                TCC.getInstance(transferService, "confirmCredit", transferService, "cancelCredit"));
    }

    /**
     * 执行测试
     */
    public static void test() {
        for (int i = 0; i < TEST_THREAD_QTY; i++) {
            Thread t = new Thread(new TestCase());
            t.start();
        }
    }

    /**
     * 单次测试用例
     * 选取[0,10000]随机金额在A1和A2之间随机互转
     */
    public static class TestCase implements Runnable {

        private static final Logger LOG = LoggerFactory.getLogger(TestCase.class);

        @Override
        public void run() {
            long begin = System.currentTimeMillis();
            for (int i = 0; i < TEST_TIMES_PER_THREAD; i++) {
                int amount = RANDOM.nextInt(1000);
                try {
                    if (amount % 2 == 0) {
                        Bank.transfer().from(A1).to(A2).amount(amount).execute();
                    } else {
                        Bank.transfer().from(A2).to(A1).amount(amount).execute();
                    }
                } catch (Exception e) {
                    LOG.debug(e.getMessage());
                }
            }
            LOG.info(">>> Test complete in {}ms.", System.currentTimeMillis() - begin);
        }
    }
}