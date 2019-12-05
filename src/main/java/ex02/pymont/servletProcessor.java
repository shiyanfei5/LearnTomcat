package ex02.pymont;

public class servletProcessor {


    /**
     * 最简单的servlet匹配 匹配格式 /servlet/servlet名字
     * 定下来去 webroot目录下寻找servlet的字节码
     * @param request
     * @param response
     */
    public void process(Request request,Response response){
        String uri = request.getUri();
        String servletName = uri.substring( uri.lastIndexOf("/")+1);




    }
}
