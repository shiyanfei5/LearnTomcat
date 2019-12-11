package ex03.pymont.connector.http;


import ex03.pymont.connector.processor.HttpProcessor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * connector支持多线程，要实现Runnable接口
 * 其只处理socket通信的启停
 */
public class HttpConnector implements Runnable{


    private boolean stopped;    //是否停止
    private String scheme = "http";


    /**
     * connector将socket流 交给processor
     */
    @Override
    public void run() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try{
            serverSocket = new ServerSocket(port,1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1); //若出现问题则直接退出
        }

        while(!stopped){
            try{
                Socket socket = serverSocket.accept();
                HttpProcessor httpProcessor = new HttpProcessor(this);
                httpProcessor.process(socket);
            } catch (IOException e){
                e.printStackTrace();
                continue;
            }
        }
    }
}
