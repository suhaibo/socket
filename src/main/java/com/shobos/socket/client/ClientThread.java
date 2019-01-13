package com.shobos.socket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(ClientThread.class);

    private Socket socket;
    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            try {
                // 用来接收服务端到消息内容
                while (true) {
                    String msg=br.readLine();
                    if(!StringUtils.isEmpty(msg)){
                        logger.info("收到信息，内容为：" + msg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
