package ex01.pymont;

import java.io.IOException;
import java.io.InputStream;

public class Request {

    // socket输入流，读取tcp数据
    private InputStream inputStream;
    // http请求url
    private String uri;

    public Request(InputStream in){ inputStream = in;}


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * 根据请求接收的inputStream，匹配请求相关属性
     * 如url，请求参数等内容
     */
    public void parse() {
        // Read a set of characters from the socket
        StringBuffer request = new StringBuffer(2048);
        int i;
        byte[] buffer = new byte[2048];
        try {
            i = inputStream.read(buffer);
        }
        catch (IOException e) {
            e.printStackTrace();
            i = -1;
        }
        for (int j=0; j<i; j++) {
            request.append((char) buffer[j]);
        }
        System.out.print(request.toString());
        uri = parseUri(request.toString());
    }

    private String parseUri(String requestString) {
        int index1, index2;
        index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1);
            if (index2 > index1)
                return requestString.substring(index1 + 1, index2);
        }
        return null;
    }


}
