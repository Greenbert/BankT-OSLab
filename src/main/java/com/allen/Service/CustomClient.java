package com.allen.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class CustomClient  {

    public static String cusID;

    public static void main(String[] args) {
        Socket socket=null;
        OutputStream outputStream=null;
        try {
            socket =new Socket("127.0.0.1",9999);
            new Thread(new CustomClientHandler(socket)).start();  //循环读
            outputStream=socket.getOutputStream();
            Scanner scanner=new Scanner(System.in);

            System.out.println("请输入账户ID: ");
            cusID = scanner.nextLine();
            outputStream.write(cusID.getBytes());
            outputStream.flush();

            while (true){
                if (CustomClientHandler.getState()){
                    break;
                }
                TimeUnit.SECONDS.sleep(1);
            }

            System.out.println("//**************************************//");
            System.out.println("********银行记账系统（GreenBot）********");
            System.out.println("//**************************************//");
            System.out.println("//**********请输入您要进行的操作***********//");
            System.out.println("//**********1：存款*********************//");
            System.out.println("//**********2：取款*********************//");
            System.out.println("//**********3：转账*********************//");
            System.out.println("//**********4：发工资*******************//");
            System.out.println("//**********5：发利息*******************//");
            System.out.println("//**********0：退卡********************//");
            System.out.println("//**********请选择：*******************//");
            while (true){
                String s = scanner.nextLine();
                outputStream.write(s.getBytes());
                outputStream.flush();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
