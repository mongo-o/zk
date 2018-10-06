package com.demo.zkdistrubutedlock.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @author AYL    2018/10/5 19:07
 */
public class OrderGenerate {
    public void generateOrderNo() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i< 10; i++) {
            new Thread(
                    () -> {
                        try {
                            countDownLatch.await();
                        } catch (Exception e) {

                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                        String orderNo = sdf.format(new Date());
                        System.out.println("生产的订单号是：" + orderNo);
                    }
            ).start();
        }
        countDownLatch.countDown();
    }
}
