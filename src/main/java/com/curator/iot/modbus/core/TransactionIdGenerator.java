package com.curator.iot.modbus.core;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jun
 * @date 2025/2/11 
 */
public class TransactionIdGenerator {

    /** 使用AtomicInteger确保线程安全 */
    private static final AtomicInteger TRANSACTION_ID = new AtomicInteger(0);

    /**
     * 获取下一个事务ID
     *
     * @return 下一个事务ID
     */
    public static int get() {
        // 原子性地增加事务ID，并且在达到最大值后重置为0
        return TRANSACTION_ID.updateAndGet(id -> (id + 1) & 0xFFFF);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println("Transaction ID: " + get());
        }
    }
}
