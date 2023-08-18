package com.allen.Service.BankService;

import com.allen.Bank;
import com.allen.BankThread.TransferMoneyOptimize;
import com.allen.Service.BankServerHandler;
import com.allen.Util.TimeUtil;
import com.allen.fileIO.writeAccount;
import com.allen.pojo.Account;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class BankServerSingle implements Runnable {
    private int operation;
    //1为自己存款 2为自己取款 3为转账 4发利息
    private String fromAccountID;

    private String toAccountID;

    private BigDecimal money;

//    private HashMap<String, Account> accounts;

    private Account fromAccount;

    private Account toAccount;

    public BankServerSingle(int operation, String fromAccountID, String toAccountID, BigDecimal money) {
        this.operation = operation;
        this.fromAccountID = fromAccountID;
        this.toAccountID = toAccountID;
        this.money = money;
    }

    @Override
    public void run() {
        try {
            fromAccount = Bank.getAccount(fromAccountID);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            toAccount = Bank.getAccount(toAccountID);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (operation == 1){
            //对自己账户存款

            addMoney(toAccountID,money);

            writeAccount w = new writeAccount();
            try {
                w.addCache(Bank.account);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                BankServerHandler.setOpStateSingle("已存入 "+money.toString()+"元，"+"账户余额为："+toAccount.getBalance());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else if (operation == 2) {
            //对自己账户取款

            int sub = subMoney(fromAccountID,money);

            if (sub == 1){
                try {
                    writeAccount w = new writeAccount();
                    try {
                        w.addCache(Bank.account);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    BankServerHandler.setOpStateSingle("取款金额："+money.toString()+"余额为："+fromAccount.getBalance().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else if (sub == 0) {
                try {
                    BankServerHandler.setOpStateSingle("金额不足");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        } else if (operation == 3) {

            TransferMoneyOptimize tp = new TransferMoneyOptimize();
            try {
                if (toAccount!=null){
                    tp.transferMoney(fromAccount,toAccount,money);
                    writeAccount w = new writeAccount();
                    try {
                        w.addCache(Bank.account);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    BankServerHandler.setOpStateSingle("转账账户：" + toAccountID + "不存在！");
                    System.out.println("所花时间：");
                    System.out.println(System.currentTimeMillis()- TimeUtil.getStartTime());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Bank.query();

        } else if (operation == 4) {
            try {
                calInterest();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Bank.query();
        }
    }

    private void addMoney(String acId,BigDecimal money){

        BigDecimal balance = toAccount.getBalance();

        toAccount.setBalance(money.add(balance));

        Bank.query();
    }

    private int subMoney(String acId,BigDecimal money){

        int flag = 1;
        if(fromAccount.getBalance().subtract(money).compareTo(BigDecimal.ZERO)==-1){

            flag = 0;
        }
        if (flag==1){
            BigDecimal balance = fromAccount.getBalance();
            fromAccount.setBalance(balance.subtract(money));
            Bank.query();
        }
        return flag;
        //flag为1表示余额足够，操作成功

    }

    private void calInterest() throws IOException {

        if (toAccount!=null){
            BigDecimal balance = toAccount.getBalance();

            toAccount.setBalance(balance.multiply(money));

            BankServerHandler.setOpStateSingle(" 本次利息发放成功，账户：" + toAccountID + "，余额：" + toAccount.getBalance());
            writeAccount w = new writeAccount();
            try {
                w.addCache(Bank.account);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            BankServerHandler.setOpStateSingle("转账账户不存在：" + toAccountID);
        }

    }
}