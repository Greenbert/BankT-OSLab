package com.allen.Service;

import com.allen.Bank;
import com.allen.BankThread.BankThreadManage;
import com.allen.Service.BankService.BankServerCheck;
import com.allen.Service.BankService.BankServerMultiple;
import com.allen.Service.BankService.BankServerSender;
import com.allen.Service.BankService.BankServerSingle;
import com.allen.Util.TimeUtil;
import com.allen.fileIO.writeAccount;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class BankServerHandler implements Runnable {
    private static Socket socket;

    private static String opStateSingle;

    private String cusId = null;


    public BankServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        Scanner scan = new Scanner(System.in);
        try {
            String response = "";
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            BankServerCheck bc = new BankServerCheck(socket);
            cusId = bc.check();

            int count = 0;
            String content = null;
            byte[] bytes = new byte[1024];
            while ((count = inputStream.read(bytes)) != -1) {
                response=new String(bytes, 0, count, "utf-8");
                System.out.println(response);
                if(response.equals("0")){
                    content="请保管好银行卡，欢迎下次光临";
                    outputStream.write(content.getBytes());
                    outputStream.flush();
                    break;
                }
                if(response.equals("1")){
                    //存款
                    content = "请输入存款金额：";
                    outputStream.write(content.getBytes());
                    outputStream.flush();

                    String resp = new String(bytes, 0, inputStream.read(bytes), "utf-8");
//                    String str2 = scan.nextLine();
                    BankThreadManage.addThread(new Thread(new BankServerSingle(1,cusId,cusId,new BigDecimal(resp)),"single"));

                }
                if(response.equals("2")){
                    //取款
                    content = "请输入取款金额：";
                    outputStream.write(content.getBytes());
                    outputStream.flush();

                    String resp = new String(bytes, 0, inputStream.read(bytes), "utf-8");

                    BankThreadManage.addThread(new Thread(new BankServerSingle(2,cusId,cusId,new BigDecimal(resp)),"single"));

                    System.out.println(opStateSingle);
                }
                if(response.equals("3")){
                    //转账

                    content = "请输入转账金额：";
                    outputStream.write(content.getBytes());
                    outputStream.flush();

                    String respMoney = new String(bytes, 0, inputStream.read(bytes), "utf-8");

                    content = "请输入对方账户ID：";
                    outputStream.write(content.getBytes());
                    outputStream.flush();

                    String respAc = new String(bytes, 0, inputStream.read(bytes), "utf-8");

                    BankThreadManage.addThread(new Thread(new BankServerSingle(3,cusId,respAc,new BigDecimal(respMoney)),"single"));
                    TimeUtil.setStartTime(System.currentTimeMillis());
                    System.out.println(opStateSingle);
                }
                if(response.equals("4")){
                    //发工资

                    content = "请输入转账金额：";
                    outputStream.write(content.getBytes());
                    outputStream.flush();

                    String respMoney = new String(bytes, 0, inputStream.read(bytes), "utf-8");

                    content = "请输入对方账户ID（以逗号分隔）：";
                    outputStream.write(content.getBytes());
                    outputStream.flush();

                    String respAc = new String(bytes, 0, inputStream.read(bytes), "utf-8");
                    String[] acList = respAc.split(",");
                    List<String > idList = new ArrayList<>();
                    for (int i = 0; i < acList.length; i++) {
                        idList.add(acList[i].toString());
                    }

                    BankThreadManage.addThread(new Thread(new BankServerMultiple(1,cusId,idList,new BigDecimal(respMoney)),"multiple"));
                    System.out.println(opStateSingle);
                }
                if(response.equals("5")){
                    //发利息

                    content = "系统正在计算利息";
                    outputStream.write(content.getBytes());
                    outputStream.flush();

                    List<String> toAcs = new ArrayList<>();
                    toAcs.add("1179572757");
                    toAcs.add("1007260597");
                    toAcs.add("1007486919");

                    BankThreadManage.addThread(new Thread(new BankServerMultiple(2,"0",toAcs,BigDecimal.valueOf(0)),"multiple"));
                    System.out.println(opStateSingle);
                }
                if(response.equals("q")){
                    content= Bank.getInfo();
                    outputStream.write(content.getBytes());
                    outputStream.flush();
                    break;
                }
                if(response.equals("exit")){
                    writeAccount w = new writeAccount();
                    w.fileMerge();
                    System.exit(0);
                }
                content="请继续操作";
                outputStream.write(content.getBytes());
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void setOpStateSingle(String opState) throws IOException {
        opStateSingle = opState;
        OutputStream outputStream = BankServerHandler.socket.getOutputStream();

        outputStream.write(opState.getBytes());
        outputStream.flush();
    }
}
