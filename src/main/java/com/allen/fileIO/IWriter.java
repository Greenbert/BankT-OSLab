package com.allen.fileIO;

import com.allen.pojo.Account;

import java.io.*;
import java.util.HashMap;
import java.util.List;

//系统操作后写入缓存文件
public class IWriter implements Handle{

    public void handle(String line, HashMap<String, Account> changeAcMap, List<String> writeAcList) throws IOException, InterruptedException {

        String[] sp = line.split(",");

        String out;
        String index = sp[0].substring(0,4);
        File file = new File("G:\\Program\\ideaProject\\testIO\\cache\\" + index + "_cache" + ".csv");
        if (!file.exists()){
            file.createNewFile();
        }
        if (writeAcList.contains(sp[0])){



//            FileWriter fw = new FileWriter("G:\\Program\\ideaProject\\testIO\\cache\\" + index + "_cache" + ".csv");
//            for ()
//            bw.flush();
//            bw.close();
        }

    }

    public void returnAcMap(HashMap<String,Account> map){
        System.out.println(map.toString());
    }
}
