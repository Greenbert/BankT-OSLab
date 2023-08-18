package com.allen.fileIO;

import com.allen.pojo.Account;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface Handle {
    public void handle(String line, HashMap<String, Account> returnAcMap, List<String> checkAcList) throws IOException, InterruptedException;

    //这里所有操作到这个方法执行才运行结束，在这里执行后续方法
    public void returnAcMap(HashMap<String,Account> map);
}
