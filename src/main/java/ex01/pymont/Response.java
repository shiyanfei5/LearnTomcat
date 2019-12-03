package ex01.pymont;

import java.io.IOException;
import java.io.OutputStream;

public class Response {

    private static final int BUFFER_SIZE = 2048;
    private Request request;    //响应对应的请求
    private OutputStream outputStream;  //outputStream即为输出


    public void setRequest(Request request) {
        this.request = request;
    }
    public Response(OutputStream outStream){
        outputStream = outputStream;
    }

    public void sendStaticResource() throws IOException{
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        String fileLocation = request.getUri();









    }


}
