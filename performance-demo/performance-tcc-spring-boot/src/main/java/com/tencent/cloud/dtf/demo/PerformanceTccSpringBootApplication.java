package com.tencent.cloud.dtf.demo;

import com.tencent.cloud.dtf.annotation.EnableDtf;
import com.tencent.cloud.dtf.client.tm.context.TransactionManagerQueueHolder;
import com.tencent.cloud.dtf.context.SpringContextAware;
import com.tencent.cloud.dtf.demo.transfer.Account;
import com.tencent.cloud.dtf.demo.transfer.Bank;
import com.tencent.cloud.dtf.demo.transfer.service.IRunTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Random;

@SpringBootApplication
@EnableDtf
public class PerformanceTccSpringBootApplication {

    private static final Logger LOG = LoggerFactory.getLogger(PerformanceTccSpringBootApplication.class);
    /**
     * 测试线程并发数
     */
    private static Integer THREAD_QTY;
    /**
     * 每线程执行次数
     */
    private static Integer TIMES_PER_THREAD;
    /**
     * 每次执行间隔，单位ms
     */
    public static Long INTERVAL;

    private static Account A1 = new Account(1);
    private static Account A2 = new Account(2);

    public static Random RANDOM = new Random();

    public static void main(String[] args) {
        // 设置参数
        ConfigurableApplicationContext ctx = SpringApplication.run(PerformanceTccSpringBootApplication.class, args);
        // 初始化参数
        init(ctx);
        // 执行测试
        test();
    }

    private static void init(ConfigurableApplicationContext ctx) {
        THREAD_QTY = ctx.getEnvironment().getProperty("dtf.perf.test.threadQty", Integer.class,
                Runtime.getRuntime().availableProcessors());
        if (-1 == THREAD_QTY) {
            THREAD_QTY = Runtime.getRuntime().availableProcessors();
        }
        TIMES_PER_THREAD = ctx.getEnvironment().getProperty("dtf.perf.test.timesPerThread", Integer.class, 10);
        INTERVAL = ctx.getEnvironment().getProperty("dtf.perf.test.interval", Long.class, 0L);
    };

    /**
     * 执行测试
     */
    private static void test() {
        LOG.info("Begin test in {} threads, run {} times in each thread, with interval {} ms.", THREAD_QTY,
                TIMES_PER_THREAD, INTERVAL);
        IRunTestService runTestService = SpringContextAware.get().getBean(IRunTestService.class);
        for (int i = 0; i < THREAD_QTY; i++) {
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
            long count = 0L;
            long begin = System.currentTimeMillis();
            for (int i = 0; (TIMES_PER_THREAD == -1 || i < TIMES_PER_THREAD); i++) {
                int amount = RANDOM.nextInt(1000);
                try {
                    if (amount % 2 == 0) {
                        this.runTestService.execute(Bank.transfer().from(A1).to(A2).amount(amount));
                    } else {
                        this.runTestService.execute(Bank.transfer().from(A2).to(A1).amount(amount));
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                }
                count++;
                if (count % 10000 == 0) {
                    LOG.info(">>> {} complete in {}ms.", count, System.currentTimeMillis() - begin);
                    begin = System.currentTimeMillis();
                    LOG.info(">>> RM Queues size: {}", TransactionManagerQueueHolder.queuesSize());
                }
                if (PerformanceTccSpringBootApplication.INTERVAL > 0) {
                    try {
                        Thread.sleep(PerformanceTccSpringBootApplication.INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            LOG.info(">>> Test complete in {}ms.", System.currentTimeMillis() - begin);
        }
    }
}