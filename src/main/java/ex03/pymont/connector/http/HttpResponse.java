package ex03.pymont.connector.http;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Locale;

public class HttpResponse implements HttpServletResponse {
    // the default buffer size
    private static final int BUFFER_SIZE = 1024;



    private OutputStream outputStream;  //outputStream即为输出
    private byte[] buffer;
    private int bufferCount;    //缓冲区中的数量
    private HttpRequest request;
    private PrintWriter writer;

    //每一个响应对应一个返回的outputstream
    public HttpResponse(OutputStream output) {
        buffer = new byte[BUFFER_SIZE];
        this.outputStream = output;
    }

    /**
     *  该函数中，静态的文件流一定要保证close，即使抛出excception
     */
    public void sendStaticResource() {
        // 获取目标静态文件
        File file = new File(Constants.WEB_ROOT, request.getRequestURI());
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
                    "Content-Length: 23\r\n\r\n";
            try{
                bufferedSocketOut.write(errorMessage.getBytes());   //写入缓冲socket
                bufferedSocketOut.flush();      //一定要flush，否则可能不发送
            } catch ( Exception e){
                e.printStackTrace();
            }

        }

    }


    /**
     * write方法，写入buffer中
     * @param
     */
    public void write(int b) throws IOException{
        //检查缓冲区的长度
        if(bufferCount >= buffer.length){
            //清空缓冲区
            flushBuffer();
        }
        buffer[bufferCount++] = (byte) b;
    }

    @Override
    public void flushBuffer() throws IOException{
        if(bufferCount > 0){
            outputStream.write(buffer,0,bufferCount);
        }
        bufferCount = 0;
    }


    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String s) {
        return false;
    }

    @Override
    public String encodeURL(String s) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String s) {
        return null;
    }

    @Override
    public String encodeUrl(String s) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String s) {
        return null;
    }

    @Override
    public void sendError(int i, String s) throws IOException {

    }

    @Override
    public void sendError(int i) throws IOException {

    }

    @Override
    public void sendRedirect(String s) throws IOException {

    }

    @Override
    public void setDateHeader(String s, long l) {

    }

    @Override
    public void addDateHeader(String s, long l) {

    }

    @Override
    public void setHeader(String s, String s1) {

    }

    @Override
    public void addHeader(String s, String s1) {

    }

    @Override
    public void setIntHeader(String s, int i) {

    }

    @Override
    public void addIntHeader(String s, int i) {

    }

    @Override
    public void setStatus(int i) {

    }

    @Override
    public void setStatus(int i, String s) {

    }

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
