package com.oyh.common.util;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hang.ouyang on 2017/8/23 20:48.
 */
public class AccountLock {

    private static ConcurrentHashMap<String, Lock> accounts = new ConcurrentHashMap<String, Lock>();
    private static ConcurrentHashMap<String, AtomicInteger> accountsCount = new ConcurrentHashMap<String, AtomicInteger>();
    private static ConcurrentHashMap<KeyType, Lock> keyLockMap = new ConcurrentHashMap<>();

    static {
        keyLockMap.put(KeyType.MEMBER,new ReentrantLock());
        keyLockMap.put(KeyType.PRODUCT_GROUP,new ReentrantLock());
        keyLockMap.put(KeyType.ORDER_BATCH,new ReentrantLock());
        keyLockMap.put(KeyType.PRODUCT,new ReentrantLock());
        keyLockMap.put(KeyType.AREA,new ReentrantLock());
    }

    private AccountLock() {
    }

    public static Lock lockMember(Integer uid) {
        return lockObject(KeyType.MEMBER ,uid);
    }

    public static Lock lockProductGroup(Integer groupId) {
        return lockObject(KeyType.PRODUCT_GROUP , groupId);
    }

    public static Lock lockOrderBatch(Integer batchId) {
        return lockObject(KeyType.ORDER_BATCH , batchId);
    }

    public static Lock lockOrderBatch(String batchNo) {
        return lockObject(KeyType.ORDER_BATCH , batchNo);
    }

    public static Lock lockProduct(Integer productId) {
        return lockObject(KeyType.PRODUCT , productId);
    }

    public static Lock lockArea(Integer areaId) {
        return lockObject(KeyType.AREA, areaId);
    }


    public static void releaseLockMember(Integer uid) {
        releaseLockObject(KeyType.MEMBER , uid);
    }

    public static void releaseLockProductGroup(Integer groupId) {
        releaseLockObject(KeyType.PRODUCT_GROUP , groupId);
    }

    public static void releaseLockOrderBatch(Integer batchId) {
        releaseLockObject(KeyType.ORDER_BATCH , batchId);
    }

    public static void releaseLockOrderBatch(String batchNo) {
        releaseLockObject(KeyType.ORDER_BATCH , batchNo);
    }

    public static void releaseLockProduct(Integer productId) {
        releaseLockObject(KeyType.PRODUCT , productId);
    }

    public static void releaseLockArea(Integer areaId) {
        releaseLockObject(KeyType.AREA , areaId);
    }

    /**
     * 通过传对象ID,来对该对象进行同步锁
     *
     * @param key 对象ID
     * @return 用来进行同步的对象
     */
    private static Lock lockObject(KeyType type,Object key) {
        Lock lock = keyLockMap.get(type);
        try{
            lock.lock();
            String lockKey = type.getValue()+key.toString();
            Lock tempLock = accounts.get(lockKey);
            if (null == tempLock) {
                tempLock = new ReentrantLock();
                accounts.put(lockKey, tempLock);
                accountsCount.put(lockKey, new AtomicInteger(0));
            }
            AtomicInteger atomicInteger = accountsCount.get(lockKey);
            atomicInteger.set(atomicInteger.incrementAndGet());
            accountsCount.put(lockKey, atomicInteger);
            System.out.println(Thread.currentThread().getName() + "  lock key==" + lockKey + " accounts " + accounts.keySet().toString() + " atomicInteger " + atomicInteger.intValue());
            return tempLock;
        }finally {
            lock.unlock();
        }

    }

    private static void releaseLockObject(KeyType type, Object key) {
        Lock lock = keyLockMap.get(type);
        try{
            lock.lock();
           String lockKey = type.getValue()+key.toString();
            AtomicInteger atomicInteger = accountsCount.get(lockKey);
            if (!Objects.isNull(atomicInteger)) {
                atomicInteger.set(atomicInteger.decrementAndGet());
                if (atomicInteger.intValue() == 0) {
                    accountsCount.remove(lockKey);
                    accounts.remove(lockKey);
                } else {
                    accountsCount.put(lockKey, atomicInteger);
                }
                System.out.println(Thread.currentThread().getName() + "  releaseLock key==" + lockKey + " accounts " + accounts.keySet().toString() + " atomicInteger " + atomicInteger.intValue());

            }
        }finally {
            lock.unlock();
        }
    }


    enum  KeyType {
        MEMBER("member"),
        PRODUCT_GROUP("productGroup"),
        ORDER_BATCH("orderBatch"),
        PRODUCT("product"),
        AREA("area");
        private String value;

        public String getValue() {
            return value;
        }

        KeyType(String value) {
            this.value = value;
        }
    }

}
