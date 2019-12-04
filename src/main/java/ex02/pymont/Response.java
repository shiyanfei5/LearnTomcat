package ex02.pymont;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.*;
import java.util.Locale;

public class Response implements ServletResponse {

    private static final int BUFFER_SIZE = 2048;
    private Request request;    //响应对应的请求
    private OutputStream outputStream;  //outputStream即为输出
    private PrintWriter writer;


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

    //******************implements ServletResponse

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        //第二个参数为true时打开auto-flush，
        //auto-flush表示println方法自动flush，但是print不会flush
        // TODO: 2019/12/4 当service调用print时不会flush缓冲区 
        writer = new PrintWriter(outputStream,true);
        return writer;
    }

    @Override
    public void setContentLength(int i) {

    }

    @Override
    public void setContentType(String s) {

    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
