package ex02.pymont;

import javax.servlet.Servlet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    /**
     * 表示静态资源所在的根目录
     */
    public static final String WEB_ROOT =
            System.getProperty("user.dir")+ File.separator+"webroot/";


    /**
     * 表示Tomcat的关闭命令
     */
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
    private boolean isShutDown = false;

    public void await(){
        ServerSocket serverSocket = null;
        int port = 8080;
        try{
            serverSocket = new ServerSocket(port,1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1); //若出现问题则直接退出
        }
        while(!isShutDown){
            try{
                Socket socket = serverSocket.accept();  //开启端口等待tcp连接建立
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                //根据inputStream创建一个请求
                Request request = new Request(inputStream);
                //根据OutputStream创建一个响应
                Response response = new Response(outputStream);
                response.setRequest(request);
                //填充匹配请求对象的内容
                request.parse();


                //判断是静态资源还是servlet
                if( request.getUri().startsWith("/servlet")  ){
                    ServletProcessor processor = new ServletProcessor();
                    processor.process(request,response);
                } else{
                    StaticResourceProcessor processor = new StaticResourceProcessor();
                    processor.process(request,response);
                }


                //资源释放
                socket.close();
                //若为uri为/SHUTDOWN,则关闭tomcat，退出
                isShutDown = request.getUri().equals(SHUTDOWN_COMMAND);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        HttpServer server = new HttpServer();
        System.out.println("当前的工作目录如下："+WEB_ROOT);
        server.await();
    }



}
