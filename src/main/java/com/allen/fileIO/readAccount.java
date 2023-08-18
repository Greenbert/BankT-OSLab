package com.allen.fileIO;

import com.allen.pojo.Account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class readAccount {
    public static MultipleFileReader multipleFileReader = null;

    public void getFileAccountMap(List<String> acList){
        HashMap<String, Account> acMap = new HashMap<>();

        IHandle iHandle = new IHandle();

        String firstAcid = acList.get(0);

        String fileIndex = firstAcid.substring(0,4);

        List<String> curList = new ArrayList<>();

        for (String ac : acList){
            if (ac.matches(fileIndex + "(.*)")){
                curList.add(ac);
            }
        }

        acList.removeIf(curList::contains);

        //acList中第一类地址
        //"G:\\Program\\resultSet\\result_" + index + ".csv"
        MultipleFileReader.Builder builder = new MultipleFileReader.Builder("D:\\Program\\BankData\\resultSet\\result_" + fileIndex + ".csv",iHandle);
        builder.withTreahdSize(10)
                .withCharset("gbk")
                .withBufferSize(1024*1024*10)
                .withSetAcIdList(acList)
                .withSetAcMap(acMap)
                .withSetCurList(curList);
        multipleFileReader = builder.build();
        multipleFileReader.start();

    }

    public int isDone(){
        return multipleFileReader.getIsDone();
    }

}
