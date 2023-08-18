package com.allen.fileIO;

import com.allen.Bank;
import com.allen.pojo.Account;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

//查找方法
public class IHandle implements Handle{

    public void handle(String line, HashMap<String, Account> returnAcMap, List<String> checkAcList) throws IOException, InterruptedException {

        String[] sp = line.split(",");

        if (checkAcList.contains(sp[0])){
            returnAcMap.put(sp[0],new Account(sp[0], new BigDecimal(sp[1])));
        }

    }

    public void returnAcMap(HashMap<String,Account> map){
        System.out.println(map.toString());
        Bank.refreshMap(map);
//        System.out.println(map.get("1286662463").getBalance());
//        testMain.setMap(map);

    }
}
