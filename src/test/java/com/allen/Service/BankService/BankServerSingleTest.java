package com.allen.Service.BankService;

import com.allen.Bank;
import com.allen.BankThread.BankThreadManage;
import com.allen.Service.BankServerHandler;
import com.allen.Util.FileUtil;
import com.allen.pojo.Account;
import com.allen.pojo.Custom;
import org.junit.Before;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.testng.Assert.*;

public class BankServerSingleTest {
    /**
     * Race Condition: 多个线程同时访问相同的资源并进行读写操作
     * -Mutex（互斥）：两个或多个进程彼此之间没有内在的制约关系，但是由于要抢占使用某个临界资源（不能被多个进程同时使用的资源，如打印机，变量）而产生制约关系。
     * -Synchronization（同步）：两个或多个进程彼此之间存在内在的制约关系（前一个进程执行完，其他的进程才能执行），如严格轮转法。
     *
     */

    //operation--1为自己存款 2为自己取款 3为转账
    @Test
    public void testAdd() throws Exception {
        HashMap<String,Account> account = new HashMap<String,Account>();
        FileUtil fileUtil = new FileUtil();

        List<Account> accountList = fileUtil.Deserialize(Account.class);
        System.out.println(accountList);

        for (Account ac:accountList){
            account.put(ac.getAccountid(),ac);
        }

        //启动线程管理中心
        new Thread(new BankThreadManage()).start();

        BankThreadManage.addThread(new Thread(new BankServerSingle(1, "000001", "000001", BigDecimal.valueOf(100)),"single"));
        BankThreadManage.addThread(new Thread(new BankServerSingle(1, "000001", "000001",BigDecimal.valueOf(100)),"single"));
        System.out.println(account);
    }

}