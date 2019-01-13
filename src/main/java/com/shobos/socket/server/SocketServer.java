package com.shobos.socket.server;

import com.shobos.socket.client.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * nio socket服务端
 */
public class SocketServer {


    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    //解码buffer
    private Charset cs = Charset.forName("UTF-8");
    //接受数据缓冲区
    private static ByteBuffer sBuffer = ByteBuffer.allocate(1024);
    //发送数据缓冲区
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);
    //选择器（叫监听器更准确些吧应该）
    private static Selector selector;


    public Map<String,SocketChannel> clientMaps = new HashMap<>();

    /**
     * 启动socket服务，开启监听
     *
     * @param port
     * @throws IOException
     */
    public void startSocketServer(int port) {
        try {
            //打开通信信道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //设置为非阻塞
            serverSocketChannel.configureBlocking(false);
            //获取套接字
            ServerSocket serverSocket = serverSocketChannel.socket();
            //绑定端口号
            serverSocket.bind(new InetSocketAddress(port));
            //打开监听器
            selector = Selector.open();
            //将通信信道注册到监听器
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            //监听器会一直监听，如果客户端有请求就会进入相应的事件处理
            while (true) {
                selector.select();//select方法会一直阻塞直到有相关事件发生或超时
                Set<SelectionKey> selectionKeys = selector.selectedKeys();//监听到的事件
                for (SelectionKey key : selectionKeys) {
                    handle(key);
                }
                selectionKeys.clear();//清除处理过的事件
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 处理不同的事件
     *
     * @param selectionKey
     * @throws IOException
     */
    private void handle(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
        String requestMsg = "";
        int count = 0;
        if (selectionKey.isAcceptable()) {
            //每有客户端连接，即注册通信信道为可读
            serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()) {
            socketChannel = (SocketChannel) selectionKey.channel();
            rBuffer.clear();
            count = socketChannel.read(rBuffer);
            //读取数据
            if (count > 0) {
                rBuffer.flip();
                requestMsg = String.valueOf(cs.decode(rBuffer).array());
            }


            //要求客户端传输数据格式：clientId#msgBody，不符合格式，不给予连接

            String[] param = requestMsg.split("#");

            String responseMsg = "";

            if(param == null && param.length < 1){
                responseMsg = "消息体不符合数据格式，格式内容参照：clientId#msgBody；客户端请求消息内容：" + requestMsg;

                //返回数据
                sBuffer = ByteBuffer.allocate(responseMsg.getBytes("UTF-8").length);
                sBuffer.put(responseMsg.getBytes("UTF-8"));
                sBuffer.flip();
                socketChannel.write(sBuffer);

                socketChannel.close();
            }else{

                String clientId = param[0];

                responseMsg = "已收到客户端"+clientId+"的消息:" + requestMsg;

                logger.info("收到客户端消息：" + socketChannel.getRemoteAddress() + responseMsg);

                //返回数据
                sBuffer = ByteBuffer.allocate(responseMsg.getBytes("UTF-8").length);
                sBuffer.put(responseMsg.getBytes("UTF-8"));
                sBuffer.flip();
                socketChannel.write(sBuffer);

                //判断客户端是否存在

                SocketChannel socketChannel1 = clientMaps.get(clientId);

                if(socketChannel1 == null){
                    clientMaps.put(clientId,socketChannel);
                }
            }

        }
    }

}
