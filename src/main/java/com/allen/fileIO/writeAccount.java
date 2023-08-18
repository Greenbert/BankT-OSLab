package com.allen.fileIO;

import com.allen.pojo.Account;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class writeAccount {
    public static MultipleFileReader multipleFileReader = null;

    //传入修改过的List和Map
    public void setFileAccountMap(HashMap<String, Account> acMap, List<String> acList){


        IWriter iWriter = new IWriter();

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
        MultipleFileReader.Builder builder = new MultipleFileReader.Builder("G:\\Program\\resultSet\\result_" + fileIndex + ".csv",iWriter);
        builder.withTreahdSize(10)
                .withCharset("gbk")
                .withBufferSize(1024*1024*10)
                .withSetAcIdList(acList)
                .withSetAcMap(acMap)
                .withSetCurList(curList);
        multipleFileReader = builder.build();
        multipleFileReader.start();

    }


    //每一次操作都向缓存文件写入
    public void addCache(HashMap<String,Account> map) throws IOException {
        Date curTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStamp = simpleDateFormat.format(curTime);
        for (Map.Entry<String,Account> item : map.entrySet()){
            Account a = item.getValue();
            String out = item.getKey() + "," + a.getBalance().toString() + "," + timeStamp + "\n";
            File file = new File("cache\\" + item.getKey() + ".csv");
            if (!file.exists()){
                file.createNewFile();
            }
            FileWriter fw = new FileWriter("cache\\" + item.getKey() + ".csv",true);
            fw.write(out);
            fw.flush();
            fw.close();
        }

    }

    private static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    //退出系统存储进文件
    public void fileMerge() throws IOException, InterruptedException, ParseException {
        List<String> list = new ArrayList<String>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        File baseFile = new File("cache");
        File[] files = baseFile.listFiles();
        List<String> allLine = new ArrayList<>();
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
            //向缓存写入最后一次账户信息
            allLine.add(spe[0] + "," + spe[1]);
            bufferedReader.close();
            //清空cache文件夹
            file.delete();
        }

        //通过缓存，写入文件
        handleLine(allLine);

    }

    //将结果转为缓存，准备写入文件
    public static void writeIn(List<String> lines) throws IOException, InterruptedException {

        String[] sp = lines.get(0).split(",");

        String out;
        String index = sp[0].substring(0,4);
        File file = new File("D:\\Program\\BankData\\test\\" + index + "_cache" + ".csv");
        if (!file.exists()){
            file.createNewFile();
        }

        File rFile = new File("D:\\Program\\BankData\\resultSet\\result_" + index + ".csv");
        BufferedReader br = new BufferedReader(new FileReader(rFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        String currentLine = "";
        while ((currentLine = br.readLine()) != null) {
            if (lines.size()>0){
                for (String s : lines){
                    String[] spl = s.split(",");
                    if (currentLine.split(",")[0].equals(spl[0])){
                        currentLine = s;

                    }

                }
            }
            bw.write(currentLine + "\n");

        }
        bw.flush();
        bw.close();
        br.close();

    }

    public static void handleLine(List<String> allLine) throws IOException, InterruptedException {
        HashMap<String,List<String>> map = new HashMap<>();
        for (String line : allLine){
            int flag = 0;
            String index = line.substring(0,4);
            for(HashMap.Entry<String,List<String>> entry : map.entrySet()) {
                if (entry.getKey().equals(index)){
                    map.get(index).add(line);
                    flag = 1;
                    break;
                }
            }
            if (flag == 0){
                List<String> tmp = new ArrayList<>();
                tmp.add(line);
                map.put(index,tmp);
            }

        }

        //写成缓存
        for(HashMap.Entry<String,List<String>> entry : map.entrySet()) {
//            System.out.println(entry.getKey() + ":" + entry.getValue().toString());
            writeIn(entry.getValue());
        }

        //写回文件
        for(HashMap.Entry<String,List<String>> entry : map.entrySet()) {
            File fromFile = new File("D:\\Program\\BankData\\test\\" + entry.getKey() + "_cache" + ".csv");

            File tofile = new File("D:\\Program\\BankData\\tmpResultSet\\result_" + entry.getKey() + ".csv");

            copyFileUsingFileChannels(fromFile,tofile);
            fromFile.delete();
        }

    }
}
