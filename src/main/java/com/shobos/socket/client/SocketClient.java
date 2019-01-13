package com.shobos.socket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class SocketClient {

    private static String clientId = UUID.randomUUID().toString();

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    public static final String IP = "localhost";

    public static final int PORT = 9092;

    public static void main(String[] args){
        try {
            Socket socket = new Socket(IP, PORT);

            //开启多线程接收信息，并解析
            ClientThread thread=new ClientThread(socket);
            thread.start();

            //主线程用来发送信息
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  //从控制台输入
            PrintWriter out=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
            out.println("client = " + clientId + "，send connect request");
            while(true)
            {
                String s=br.readLine();
                s = clientId + "#" + s;
                if(!StringUtils.isEmpty(s)){
                    out.println(s);
                    out.flush();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

}

