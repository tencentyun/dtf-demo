package com.tencent.cloud.dtf.demo;

import com.tencent.cloud.dtf.annotation.EnableDtf;
import com.tencent.cloud.dtf.context.SpringContextAware;
import com.tencent.cloud.dtf.demo.transfer.Account;
import com.tencent.cloud.dtf.demo.transfer.Bank;
import com.tencent.cloud.dtf.demo.transfer.service.IRunTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Random;

@SpringBootApplication
@EnableDtf
@EnableTransactionManagement
public class SingleTransferTccSpringBootApplication {
    /**
     * 测试线程并发数
     */
    private static final int TEST_THREAD_QTY = 5;
    /**
     * 每线程执行次数
     */
    private static final int TEST_TIMES_PER_THREAD = 100;

    private static Account A1 = new Account(1);
    private static Account A2 = new Account(2);

    public static Random RANDOM = new Random();

    public static void main(String[] args) {
        // 设置参数
        SpringApplication.run(SingleTransferTccSpringBootApplication.class, args);
        try {
            Thread.sleep(30 * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 执行测试
        test();
    }

    /**
     * 执行测试
     */
    private static void test() {
        IRunTestService runTestService = SpringContextAware.get().getBean(IRunTestService.class);
        for (int i = 0; i < TEST_THREAD_QTY; i++) {
            Thread t = new Thread(new TestCase(runTestService));
            t.start();
        }
    }

    /**
     * 单次测试用例<br>
     * 选取[0,10000]随机金额在A1和A2之间随机互转
     */
    public static class TestCase implements Runnable {

        private static final Logger LOG = LoggerFactory.getLogger(TestCase.class);

        private final IRunTestService runTestService;

        public TestCase(IRunTestService runTestService) {
            this.runTestService = runTestService;
        }

        @Override
        public void run() {
            long begin = System.currentTimeMillis();
            for (int i = 0; i < TEST_TIMES_PER_THREAD; i++) {
                int amount = RANDOM.nextInt(1000);
                try {
                    if (amount % 2 == 0) {
                        this.runTestService.execute(Bank.transfer().from(A1).to(A2).amount(amount));
                    } else {
                        this.runTestService.execute(Bank.transfer().from(A2).to(A1).amount(amount));
                    }
                } catch (Exception e) {
                    LOG.debug(e.getMessage());
                }
            }
            LOG.info(">>> Test complete in {}ms.", System.currentTimeMillis() - begin);
        }
    }
}