package com.allen.fileIO;

import com.allen.pojo.Account;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class readCache {
    public HashMap<String, Account> recovery() throws IOException, InterruptedException, ParseException {
        HashMap<String,Account> map = new HashMap<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        File baseFile = new File("cache");
        File[] files = baseFile.listFiles();
        for (File file : files) {
            String lastLine = "";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String currentLine = "";
            while ((currentLine = bufferedReader.readLine()) != null) {
                if (lastLine.equals("")){
                    lastLine = currentLine;
                }else {
                    String[] spc = currentLine.split(",");
                    String[] spl = lastLine.split(",");
                    Date dtc = df.parse(spc[2]);
                    Date dtl = df.parse(spl[2]);

                    if (dtc.getTime() > dtl.getTime()) {
                        lastLine = currentLine;
                    }
                }

            }
            String[] spe = lastLine.split(",");
            //向map写入最后一次账户信息
            map.put(spe[0],new Account(spe[0],new BigDecimal(spe[1])));
            bufferedReader.close();
        }
        return map;

    }
}
