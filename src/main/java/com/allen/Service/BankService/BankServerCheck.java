package com.allen.Service.BankService;

import com.allen.Bank;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class BankServerCheck {
    private Socket socket;

    public BankServerCheck(Socket socket) {
        this.socket = socket;
    }

    public String check() throws IOException, InterruptedException {
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        //预读判断账户ID
        int tmpCount = 0;
        String tmpResp = null;
        byte[] tmpBytes = new byte[1024];
        while ((tmpCount = inputStream.read(tmpBytes)) != -1){
            tmpResp=new String(tmpBytes, 0, tmpCount, "utf-8");
            System.out.println(tmpResp);
            if (Bank.getMyAccount(tmpResp)==1){
                outputStream.write("success".getBytes());
                outputStream.flush();
                return tmpResp;
            } else if (Bank.getMyAccount(tmpResp) == 0) {
                outputStream.write("failed".getBytes());
                outputStream.flush();
            }
        }
        return null;
    }
}
