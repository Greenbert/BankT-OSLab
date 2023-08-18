package com.allen.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class CustomClientHandler implements Runnable{
    private Socket socket;

    private static boolean state = false;
    public CustomClientHandler(Socket socket) {
        this.socket = socket;
    }
    //
    @Override
    public void run() {
        InputStream inputStream=null;
        try {
            String request = "";
            inputStream = socket.getInputStream();
            int count=0;
            byte[] bytes=new byte[1024];
            while ((count=inputStream.read(bytes))!=-1){
                request=new String(bytes,0,count,"utf-8");
                if (request.equals("success")){
                    state = true;
                    TimeUnit.SECONDS.sleep(1);
                }
                System.out.println("\n收到服务器消息----->"+request);
                System.out.print("请输入要发送的消息：");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean getState(){
        return state;
    }
}
