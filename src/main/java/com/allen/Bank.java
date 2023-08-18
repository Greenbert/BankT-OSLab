package com.allen;

import com.allen.BankThread.BankThreadManage;
import com.allen.Service.BankServerHandler;
import com.allen.Util.FileUtil;
import com.allen.fileIO.readAccount;
import com.allen.fileIO.readCache;
import com.allen.pojo.Account;
import com.allen.pojo.Custom;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Bank {
    public static  final  int port =8088;
    public static ServerSocket serverSocket = null;
    public static HashMap<String,Account> account;

    public static String query(String accountid){
        return account.get(accountid).getBalance().toString();
    }
    public static void main(String[] args) throws Exception {

        //当出现故障后，可以导入缓存的账户信息
        readCache readCache = new readCache();
        account = readCache.recovery();
        System.out.println(account.toString());
        ServerSocket serverSocket = null;
        try {
            serverSocket=new ServerSocket(9999);

            //启动线程管理中心
            new Thread(new BankThreadManage()).start();
            //每一次有客户端连接服务器，开启一个BankServerHandler线程，对应一个客户端
            while (true){
                Socket socket = serverSocket.accept();  //阻塞
                System.out.println("客户端"+socket.getRemoteSocketAddress().toString()+"来连接了");
                ExecutorService executorService = Executors.newFixedThreadPool(10);
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
                threadPoolExecutor.setCorePoolSize(10);

                executorService.execute(new Thread(new BankServerHandler(socket)));
                executorService.shutdown();


            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocket!=null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Account getAccountMap(String acID){
        return account.get(acID);
    }

    public static Account getAccount(String acID) throws InterruptedException {
        if (account.containsKey(acID)){
            //直接执行
            return getAccountMap(acID);
        }
        List<String> list = new ArrayList<>();
        list.add(acID);
        readAccount read = new readAccount();
        read.getFileAccountMap(list);

        while (true){
            if (read.isDone() == 1){
                break;
            }
            TimeUnit.SECONDS.sleep(1);
        }
        //读取文件后运行
        return getAccountMap(acID);
    }
    public static void query(){
        System.out.println(account.toString());
    }

    public static List<Account> getAccounts(List<String> idList){
        List<Account> accountList = new ArrayList<>();
        for (String id : idList){
            accountList.add(account.get(id));
        }
        return accountList;
    }

    public static void updateAccounts(List<Account> actList){

        for (Account ac : actList){
            account.get(ac.getAccountid()).setBalance(ac.getBalance());
        }

        System.out.println(account.toString());
    }

    public static int getMyAccountMap(String acID){

        if (account.containsKey(acID)){
            return 1;
        }
        return 0;
    }

    public static String getInfo(){
        return account.toString();
    }

    public static int getMyAccount(String acID) throws InterruptedException {
        if (account.containsKey(acID)){
            //直接执行
            return getMyAccountMap(acID);
        }
        List<String> list = new ArrayList<>();
        list.add(acID);
        readAccount read = new readAccount();
        read.getFileAccountMap(list);

        while (true){
            if (read.isDone() == 1){
                break;
            }
            TimeUnit.SECONDS.sleep(1);
        }
        //读取文件后运行
        return getMyAccountMap(acID);
    }

    //IHandle调用更新map
    public static void refreshMap(HashMap<String,Account> map){
        for (HashMap.Entry<String,Account> entry : map.entrySet()){
            if (!account.containsKey(entry.getKey())){
                account.put(entry.getKey(),entry.getValue());
            }
        }
    }

    //多账户操作前先一起加进list
    public static void setAcNotInMap(List<String> acList) throws InterruptedException {
        List<String> ready = new ArrayList<>();
        for (String ac : acList){
            if (!account.containsKey(ac)){
                ready.add(ac);
            }
        }
        if (!ready.isEmpty()){
            readAccount read = new readAccount();
            read.getFileAccountMap(ready);

            while (true){
                if (read.isDone() == 1){
                    break;
                }
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }
}
