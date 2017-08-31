package com.oyh.test;

import com.oyh.common.util.AccountLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Created by hang.ouyang on 2017/8/23 21:52.
 */
public class AccountLockTest {

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < 100; j++) {
                                Lock lock = AccountLock.lockMember(j);
                                try {
                                    lock.lock();
                                    TimeUnit.SECONDS.sleep((int)(Math.random()*10));
                                } catch (InterruptedException e) {
                                    System.out.println(e.toString());
                                }finally {
                                    lock.unlock();
                                    AccountLock.releaseLockMember(j);
                                }
                            }
                        }
                    }).start();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < 100; j++) {
                                Lock lock = AccountLock.lockProduct(j);
                                try {
                                    lock.lock();
                                    TimeUnit.SECONDS.sleep((int)(Math.random()*10));
                                } catch (InterruptedException e) {
                                    System.out.println(e.toString());
                                }finally {
                                    lock.unlock();
                                    AccountLock.releaseLockProduct(j);
                                }
                            }
                        }
                    }).start();
                }
            });
            thread.start();
        }

        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < 100; j++) {
                                Lock lock = AccountLock.lockMember(j);
                                try {
                                    lock.lock();
                                    TimeUnit.SECONDS.sleep((int)(Math.random()*10));
                                } catch (InterruptedException e) {
                                    System.out.println(e.toString());
                                }finally {
                                    lock.unlock();
                                    AccountLock.releaseLockMember(j);
                                }
                            }
                        }
                    }).start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < 100; j++) {
                                Lock lock = AccountLock.lockProduct(j);
                                try {
                                    lock.lock();
                                    TimeUnit.SECONDS.sleep((int)(Math.random()*10));
                                } catch (InterruptedException e) {
                                    System.out.println(e.toString());
                                }finally {
                                    lock.unlock();
                                    AccountLock.releaseLockProduct(j);
                                }
                            }
                        }
                    }).start();
                }
            });
            thread.start();
        }

//        try {
//            TimeUnit.MINUTES.sleep(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }



}
