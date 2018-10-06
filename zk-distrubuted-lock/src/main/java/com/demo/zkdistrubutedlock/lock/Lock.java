package com.demo.zkdistrubutedlock.lock;

/**
 * @author AYL    2018/10/5 18:43
 */
public abstract class Lock {
    public abstract boolean acquire() throws Exception;
    public abstract boolean release();
}
