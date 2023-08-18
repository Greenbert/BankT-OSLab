package com.allen.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 账户类
 */
public class Account implements Serializable {
    private String accountid;
    private  static  final long serialVersionUID=2563638533397908121L;
    private BigDecimal balance;
    private String customid;
    private String subjectid;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountid='" + accountid + '\'' +
                ", balance=" + balance +
                ", customid='" + customid + '\'' +
                ", subjectid='" + subjectid + '\'' +
                '}';
    }

    public Account(String accountid, BigDecimal balance) {
        this.accountid = accountid;
        this.balance = balance;
    }

    public String getAccountid() {
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public synchronized BigDecimal getBalance() {
        return balance;
    }

    public synchronized void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCustomid() {
        return customid;
    }

    public void setCustomid(String customid) {
        this.customid = customid;
    }

    public String getSubjectid() {
        return subjectid;
    }

    public void setSubjectid(String subjectid) {
        this.subjectid = subjectid;
    }
}
