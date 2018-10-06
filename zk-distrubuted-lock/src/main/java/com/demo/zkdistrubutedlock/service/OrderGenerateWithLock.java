package com.demo.zkdistrubutedlock.service;

import com.demo.zkdistrubutedlock.lock.ExclusiveLock;
import com.demo.zkdistrubutedlock.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @author AYL    2018/10/5 19:11
 */
@Component
public class OrderGenerateWithLock {
    @Autowired
    ExclusiveLock exclusiveLock;

    public void generateOrderNo() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i< 10; i++) {
            new Thread(
                    () -> {
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        exclusiveLock.acquire();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                        String orderNo = sdf.format(new Date());
                        LogUtil.print("XLock生产的订单号是：" + orderNo);
                        exclusiveLock.release();
                    }
            ).start();
        }
        countDownLatch.countDown();
    }
}
