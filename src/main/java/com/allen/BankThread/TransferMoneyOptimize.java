package com.allen.BankThread;

import com.allen.Service.BankServerHandler;
import com.allen.pojo.Account;

import java.io.IOException;
import java.math.BigDecimal;

/**
 *
 * 转账类优化 -> 通过hash算法
 */
public class TransferMoneyOptimize {

    /** hash 冲突时使用第三个锁 */
    private static final Object conflictShareLock = new Object();

    /**
     * 转账方法
     *
     * @param accountFrom       转账方
     * @param accountTo         接收方
     * @param amt               转账金额
     * @throws Exception
     */
    public static void transferMoney(Account accountFrom,
                                     Account accountTo,
                                     BigDecimal amt) throws Exception {
        // 计算hash值
        int accountFromHash = System.identityHashCode(accountFrom);
        int accountToHash = System.identityHashCode(accountTo);
        // 如下三个分支能一定控制账户之间的转账是不会产生死锁的
        if (accountFromHash > accountToHash) {
            synchronized (accountFrom) {
                synchronized (accountTo) {
                    transferMoneyHandler(accountFrom, accountTo, amt);
                }
            }
        } else if (accountToHash > accountFromHash) {
            synchronized (accountTo) {
                synchronized (accountFrom) {
                    transferMoneyHandler(accountFrom, accountTo, amt);
                }
            }
        } else {
            // 解决hash冲突
            synchronized (conflictShareLock) {
                synchronized (accountFrom) {
                    synchronized (accountTo) {
                        transferMoneyHandler(accountFrom, accountTo, amt);
                    }
                }
            }
        }

    }

    public static void transferMoneyHandler(Account fromAccount,Account toAccount, BigDecimal money) throws IOException {
        //转账
        //对fromAc
        int flag = 1;
        if(fromAccount.getBalance().subtract(money).compareTo(BigDecimal.ZERO)==-1){

            flag = 0;
            BankServerHandler.setOpStateSingle("转账失败");
        }
        if (flag==1){
            BigDecimal balance = fromAccount.getBalance();
            fromAccount.setBalance(balance.subtract(money));

            BigDecimal toBalance = toAccount.getBalance();

            toAccount.setBalance(money.add(toBalance));

            BankServerHandler.setOpStateSingle("转账成功,余额为："+fromAccount.getBalance());
        }
    }


}