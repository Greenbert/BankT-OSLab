package com.allen.Service.BankService;

import com.allen.Bank;
import com.allen.Service.CustomClientHandler;
import com.allen.pojo.Account;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class socketTest {

    @Test
    public void socket() throws IOException, InterruptedException {
        socketSalary("000002,000003,000004,000005");
        socketAdd();
//        socketInRate();
//        socketSub();
        socketAdd();
        socketAdd();
        socketSalary("000002,000003");
        socketAdd();
        socketAdd();
//        socketTrans("000002");

        //加入等待时间防止由于socket发送接收或者进程排队延迟打印出错，等待足够时间后打印出正确结果
        TimeUnit.SECONDS.sleep(3);
        socketQuery();

    }

    //账户000001存款
    public void socketAdd() throws IOException {
        String content;
        Socket socket=null;
        OutputStream outputStream=null;
        InputStream inputStream=null;

        socket =new Socket("127.0.0.1",9999);
        String request = "";
        inputStream = socket.getInputStream();
        int count=0;
        byte[] bytes=new byte[1024];

        outputStream=socket.getOutputStream();

        content = "000001";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (request.equals("success")){
                break;
            }
        }

        content = "1";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (request.equals("请输入存款金额：")){
                break;
            }
        }

        content = "100";
        outputStream.write(content.getBytes());
        outputStream.flush();
    }

    //从账户000001取款

    public void socketSub() throws IOException {
        String content;
        Socket socket=null;
        OutputStream outputStream=null;
        InputStream inputStream=null;

        socket =new Socket("127.0.0.1",9999);
        String request = "";
        inputStream = socket.getInputStream();
        int count=0;
        byte[] bytes=new byte[1024];

        outputStream=socket.getOutputStream();

        content = "000001";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (request.equals("success")){
                break;
            }
        }

        content = "2";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (request.equals("请输入取款金额：")){
                break;
            }
        }

        content = "100";
        outputStream.write(content.getBytes());
        outputStream.flush();
    }

    //从账户000001转账
    public void socketTrans(String acID) throws IOException {
        String content;
        Socket socket=null;
        OutputStream outputStream=null;
        InputStream inputStream=null;

        socket =new Socket("127.0.0.1",9999);
        String request = "";
        inputStream = socket.getInputStream();
        int count=0;
        byte[] bytes=new byte[1024];

        outputStream=socket.getOutputStream();

        content = "000001";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (request.equals("success")){
                break;
            }
        }

        content = "3";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (request.equals("请输入转账金额：")){
                break;
            }
        }

        content = "100";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (request.equals("请输入对方账户ID：")){
                break;
            }
        }

        content = acID;
        outputStream.write(content.getBytes());
        outputStream.flush();
    }

    //从账户000001发工资
    public void socketSalary(String acIDs) throws IOException {
        String content;
        Socket socket=null;
        OutputStream outputStream=null;
        InputStream inputStream=null;

        socket =new Socket("127.0.0.1",9999);
        String request = "";
        inputStream = socket.getInputStream();
        int count=0;
        byte[] bytes=new byte[1024];

        outputStream=socket.getOutputStream();

        content = "000001";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (request.equals("success")){
                break;
            }
        }

        content = "4";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (request.equals("请输入转账金额：")){
                break;
            }
        }

        content = "100";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (request.equals("请输入对方账户ID（以逗号分隔）：")){
                break;
            }
        }

        content = acIDs;
        outputStream.write(content.getBytes());
        outputStream.flush();
    }

    //计算利息
    public void socketInRate() throws IOException {
        String content;
        Socket socket=null;
        OutputStream outputStream=null;
        InputStream inputStream=null;

        socket =new Socket("127.0.0.1",9999);
        String request = "";
        inputStream = socket.getInputStream();
        int count=0;
        byte[] bytes=new byte[1024];

        outputStream=socket.getOutputStream();

        content = "000001";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (request.equals("success")){
                break;
            }
        }

        content = "5";
        outputStream.write(content.getBytes());
        outputStream.flush();

    }

    //查询所有账户信息
    public void socketQuery() throws IOException {
        String content;
        Socket socket=null;
        OutputStream outputStream=null;
        InputStream inputStream=null;

        socket =new Socket("127.0.0.1",9999);
        String request = "";
        inputStream = socket.getInputStream();
        int count=0;
        byte[] bytes=new byte[1024];

        outputStream=socket.getOutputStream();

        content = "000001";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (request.equals("success")){
                break;
            }
        }

        content = "q";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (!request.equals("")){
                System.out.println(request);
                break;
            }
        }

    }

    //退出并保存
    public void exitSocket() throws IOException {
        String content;
        Socket socket=null;
        OutputStream outputStream=null;
        InputStream inputStream=null;

        socket =new Socket("127.0.0.1",9999);
        String request = "";
        inputStream = socket.getInputStream();
        int count=0;
        byte[] bytes=new byte[1024];

        outputStream=socket.getOutputStream();

        content = "1440768483";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (request.equals("success")){
                break;
            }
        }

        content = "exit";
        outputStream.write(content.getBytes());
        outputStream.flush();

        while ((count=inputStream.read(bytes))!=-1){
            request=new String(bytes,0,count,"utf-8");
            if (!request.equals("")){
                System.out.println(request);
                break;
            }
        }

    }
}
