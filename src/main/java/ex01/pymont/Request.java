package ex01.pymont;

import java.io.InputStream;

public class Request {

    // socket输入流，读取tcp数据
    private InputStream inputStream;
    // http请求url
    private String uri;

    public Request(InputStream inputStream){}


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
    public void parse(){}


}
