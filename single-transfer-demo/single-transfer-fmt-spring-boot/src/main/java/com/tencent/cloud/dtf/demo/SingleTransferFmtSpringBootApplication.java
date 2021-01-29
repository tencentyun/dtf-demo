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

/**
 * 如果前一个出现异常，cancel获取后象语句可能与下一个主事务的try阶段获取全局锁语句死锁
 */
@SpringBootApplication
@EnableDtf
@EnableTransactionManagement
public class SingleTransferFmtSpringBootApplication {
    /**
     * 每线程执行次数
     */
    private static final int TEST_TIMES_PER_THREAD = 2;

    private static final Account A1 = new Account(1);
    private static final Account A2 = new Account(2);

    public static Random RANDOM = new Random();

    public static void main(String[] args) {
        // 设置参数
        SpringApplication.run(SingleTransferFmtSpringBootApplication.class, args);
        // try {
        // Thread.sleep(30 * 1000L);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // 执行测试
        test();
    }

    /**
     * 执行测试
     */
    private static void test() {
        IRunTestService runTestService = SpringContextAware.get().getBean(IRunTestService.class);
        Thread t = new Thread(new TestCase(runTestService));
        t.start();
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
                int amount = RANDOM.nextInt(100);
                try {
                    if (amount % 2 == 0) {
                        this.runTestService.execute(Bank.transfer().from(A1).to(A2).amount(amount));
                    } else {
                        this.runTestService.execute(Bank.transfer().from(A2).to(A1).amount(amount));
                    }
                } catch (Exception e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.error("", e);
                    } else {
                        LOG.info(e.getMessage());
                    }
                }
            }
            LOG.info(">>> Test complete in {}ms.", System.currentTimeMillis() - begin);
        }
    }
}