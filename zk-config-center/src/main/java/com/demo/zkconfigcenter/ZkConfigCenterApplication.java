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
            configClient.autoConfig();

            //程序阻塞在此，将会一直运行并监听配置节点的变化
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
