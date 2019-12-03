package ex01.pymont;

import java.io.*;

public class Response {

    private static final int BUFFER_SIZE = 2048;
    private Request request;    //响应对应的请求
    private OutputStream outputStream;  //outputStream即为输出


    public void setRequest(Request request) {
        this.request = request;
    }
    public Response(OutputStream outStream){
        outputStream = outStream;
    }

    /**
     *  该函数中，静态的文件流一定要保证close，即使抛出excception
     */
    public void sendStaticResource() {
        // 获取目标静态文件
        File file = new File(HttpServer.WEB_ROOT, request.getUri());
        // 使用Buffer封装socket流，加速速度
        BufferedOutputStream bufferedSocketOut = new BufferedOutputStream(outputStream);

        if(file.exists()){
            //使用Buffer封装加快速率 ,借助 try-resource实现
            try(BufferedInputStream bufferedInputStream =
                        new BufferedInputStream( new FileInputStream(file) )
            ) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int len;
                while ((len = bufferedInputStream.read(buffer)) != -1) {
                    bufferedSocketOut.write(buffer, 0, len);
                }
                bufferedSocketOut.flush();      //一定要flush，因为 缓冲区仅在满了的时候才发送

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            String  errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
                    "Content-Type: text/html\r\n"+
                    "Content-Length: 23\r\n"+
                    "\r\n";
            try{
                bufferedSocketOut.write(errorMessage.getBytes());   //写入缓冲socket
                bufferedSocketOut.flush();      //一定要flush，否则可能不发送
            } catch ( Exception e){
                e.printStackTrace();
            }

        }

    }


}
