package com.demo.zkconfigcenter;

import com.demo.zkconfigcenter.client.ConfigClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class ZkConfigCenterApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ZkConfigCenterApplication.class, args);
        ConfigClient configClient = context.getBean(ConfigClient.class);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            configClient.autoConfig(countDownLatch);

            //main线程阻塞在此，将处于sleeping状态
            // 同时一个新的线程将会被创建，由这个新线程运行并监听配置节点的变化
            //本来想优雅停止程序，将{"type":"stop"}设定成配置节点redisConfig的值，但是事与愿违，这里并不会唤醒main线程。
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}
