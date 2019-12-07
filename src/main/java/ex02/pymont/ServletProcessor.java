package ex02.pymont;


import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

public class ServletProcessor {


    /**
     * 最简单的servlet匹配 匹配格式 /servlet/servlet名字
     * 定下来去 webroot目录下寻找servlet的字节码
     * @param request
     * @param response
     */
    public void process(Request request,Response response){
        String uri = request.getUri();
        String servletName = uri.substring( uri.lastIndexOf("/")+1);

        URLClassLoader loader = null;  //提升作用域
        try{
            //此处为CanonicalPath，代表标准路径
            String jarDir = new File( HttpServer.WEB_ROOT).getCanonicalPath();
            URL[] urls = new URL[]{
                    new URL("file:///"+jarDir+"/")
            };
            loader = new URLClassLoader(urls);      //传入一个jar包或目录（路径）
        } catch (Exception e){
            e.printStackTrace();
        }
        try{
            if(loader != null ){
                Class<?> clz = loader.loadClass(servletName);
                Servlet servlet = (Servlet) clz.newInstance();
                servlet.service( request,response);
            } else{
                throw new RuntimeException("妈的没有加载出来loader");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
