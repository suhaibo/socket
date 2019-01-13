package com.shobos.socket;


import com.shobos.socket.server.SocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SocketApplication {

    private static int port = 9092;

    public static void main(String[] args) {
        SpringApplication.run(SocketApplication.class, args);

        System.out.println("port ================= " + port);

        //起socket服务
        SocketServer server = new SocketServer();
        server.startSocketServer(port);
    }




}

