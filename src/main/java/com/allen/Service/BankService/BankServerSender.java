package com.allen.Service.BankService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class BankServerSender {
    private Socket socket;

    public BankServerSender(Socket socket) {
        this.socket = socket;
    }

    public void send(String state) throws IOException {
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        //预读判断账户ID
        int tmpCount = 0;
        String tmpResp = null;
        byte[] tmpBytes = new byte[1024];
        while ((tmpCount = inputStream.read(tmpBytes)) != -1){
            tmpResp=new String(tmpBytes, 0, tmpCount, "utf-8");
            if (!state.equals("")){
                outputStream.write(state.getBytes());
                outputStream.flush();
                break;
            } else {
                outputStream.write("消息接收失败".getBytes());
                outputStream.flush();
                break;
            }
        }
    }
}
