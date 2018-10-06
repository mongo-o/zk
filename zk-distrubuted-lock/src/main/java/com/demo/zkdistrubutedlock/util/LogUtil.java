package com.demo.zkdistrubutedlock.util;

/**
 * @author AYL    2018/10/6 11:15
 */
public class LogUtil {
    public static void print(String message) {
        System.out.println(Thread.currentThread().getName() + ":" + message);
    }
}
