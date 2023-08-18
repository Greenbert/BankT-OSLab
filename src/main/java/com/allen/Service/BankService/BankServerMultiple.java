package com.allen.Service.BankService;

import com.allen.Bank;
import com.allen.Service.BankServerHandler;
import com.allen.pojo.Account;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class BankServerMultiple implements Runnable {
    private int operation;
    //批量操作 1向多个账户转账 2计算利息为多个账户存款
    private String fromAccountID;

    private List<String> toAccountIDs;

    private BigDecimal money;

    private Account fromAccount;

    private List<Account> toAccounts;

    private BigDecimal interestRate = BigDecimal.valueOf(1.03);

    public BankServerMultiple(int operation, String fromAccountID, List<String> toAccountIDs, BigDecimal money) {
        this.operation = operation;
        this.fromAccountID = fromAccountID;
        this.toAccountIDs = toAccountIDs;
        this.money = money;
    }

    @Override
    public void run() {
//        fromAccount = Bank.getAccount(fromAccountID);
//        toAccounts = Bank.getAccounts(toAccountIDs);
        try {
            Bank.setAcNotInMap(toAccountIDs);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (operation == 1){


                for (String ac : toAccountIDs){
                    new Thread(new BankServerSingle(3,fromAccountID,ac,money)).start();
                }


        } else if (operation == 2) {
            //计算利息为多个账户存款

            for (String ac : toAccountIDs){
                new Thread(new BankServerSingle(4,ac,ac,interestRate)).start();
            }

        }
    }

}