package ex03.pymont.connector.processor;



import ex03.pymont.connector.http.*;
import util.RequestUtil;
import util.StringManager;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class HttpProcessor {

    //错误消息映射器
    // Socket类的错误信息转换器，一个包下一个对象，共同使用，
    // 只读不写线程安全
    protected static StringManager sm = StringManager.getManager(Constants.Package);
    //---------------------------实例属性-------------------------
    private HttpConnector connector;    //关联的connector
    private HttpRequest request;
    private HttpResponse response;  //请求

    public HttpProcessor(HttpConnector connector) {
        this.connector = connector;
    }

    //******开始最麻烦的 http请求解析************
    public void process(Socket socket){
        SocketInputStream socketInputStream = null;
        OutputStream outputStream = null;

        try{
            socketInputStream = new SocketInputStream( socket.getInputStream(),2048);
            outputStream = socket.getOutputStream();

            //创建一个请求
            request = new HttpRequest();
            response = new HttpResponse(outputStream);

            //填充Request
            parseRequest(socketInputStream);
            //填充请求头
            parseHeaders(socketInputStream);

            //check if this is a request for a servlet or a static resource
            //a request for a servlet begins with "/servlet/"
            if (request.getRequestURI().startsWith("/servlet/")) {
                ServletProcessor processor = new ServletProcessor();
                processor.process(request, response);
            }
            else {
                StaticResourceProcessor processor = new StaticResourceProcessor();
                processor.process(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void parseHeaders(SocketInputStream inputStream)
        throws IOException, ServletException{
        //初始化容器
        HttpHeader httpHeader = new HttpHeader();

        while(true){

            inputStream.readHeader(httpHeader);
            //判断是否已经结束
            if(httpHeader.valueEnd == -1 || httpHeader.nameEnd == -1){
                break;
            }
            // 获取key和value
            String key = new String(httpHeader.name,0,httpHeader.nameEnd+1);
            String value = new String(httpHeader.value,0,httpHeader.valueEnd+1);

            if(key.equals("Cookie")) {
                List<Cookie> cookieList = RequestUtil.parseCookieHeader(value);
                //由于cookie中可能包含session，所以需要如下处理设置session
                for (Cookie cookie : cookieList) {
                    if (cookie.getName().equals("jsessionid")) {
                        // 目前仅仅支持 session存放在 cookie中，该函数永远返回false
                        if (!request.isRequestedSessionIdFromCookie()) {
                            //session在cookie
                            request.setRequestedSessionCookie(true);
                            //session在url
                            request.setRequestedSessionURL(false);
                            //设置最后的session
                            request.setRequestedSessionId(cookie.getValue());
                        }
                    }
                    // 把该cookie添加到request中
                    request.addCookie(cookie);
                }
            } else if(  key.equals("Content-Length")) {
                try {
                    int n = Integer.parseInt(value);
                } catch (Exception e) {
                    throw new ServletException(sm.getString("httpProcessor.parseHeaders.contentLength"));
                }
            }   else if (key.equals("content-type")) {
                request.setContentType(value);
            }

        }
    }








    public void parseRequest(SocketInputStream socketInputStream)
            throws IOException, ServletException    {
        //初始化请求行容器
        HttpRequestLine requestLine = new HttpRequestLine();
        //填充requestline容器
        socketInputStream.readRequestLine(requestLine);

        String method = new String(requestLine.method,0,requestLine.methodEnd+1);
        String uri = new String(requestLine.uri,0,requestLine.uriEnd+1);
        String protocol = new String(requestLine.protocol,0,requestLine.protocolEnd+1);
        if (method.length() < 1) {
            throw new ServletException("Missing HTTP request method");
        }
        else if (uri.length() < 1) {
            throw new ServletException("Missing HTTP request URI");
        }

        // 进行uri解析,若存在uri包含参数
        int question = uri.indexOf("?");
        if(question >= 0){
            //存在
            uri = uri.substring(0,question);
            request.setQueryString( uri.substring(question+1));
        }

        // 进行uri中jsessionid的检测，uri可能包含jsessionid
        // http://xxxx.rowse.jsp;jsessionid=5AC6268960?curAlbumID=9
        // TODO: 2019/12/10  目前不支持将session放在uri


        //进行uri标准化
        String normalizedUri = normalize(uri);

        //设置最终属性
        request.setMethod(method);
        request.setProtocol(protocol);
        //若uri标准化后不为null
        if(normalizedUri != null){
            request.setRequestURI(normalizedUri);
        } else{
            request.setRequestURI(uri);
        }

    }

    /**
     * Return a context-relative path, beginning with a "/", that represents
     * the canonical version of the specified path after ".." and "." elements
     * are resolved out.  If the specified path attempts to go outside the
     * boundaries of the current context (i.e. too many ".." path elements
     * are present), return <code>null</code> instead.
     *
     * @param path Path to be normalized
     */
    protected String normalize(String path) {
        if (path == null)
            return null;
        // Create a place for the normalized path
        String normalized = path;

        // Normalize "/%7E" and "/%7e" at the beginning to "/~"
        if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
            normalized = "/~" + normalized.substring(4);

        // Prevent encoding '%', '/', '.' and '\', which are special reserved
        // characters
        if ((normalized.indexOf("%25") >= 0)
                || (normalized.indexOf("%2F") >= 0)
                || (normalized.indexOf("%2E") >= 0)
                || (normalized.indexOf("%5C") >= 0)
                || (normalized.indexOf("%2f") >= 0)
                || (normalized.indexOf("%2e") >= 0)
                || (normalized.indexOf("%5c") >= 0)) {
            return null;
        }

        if (normalized.equals("/."))
            return "/";

        // Normalize the slashes and add leading slash if necessary
        if (normalized.indexOf('\\') >= 0)
            normalized = normalized.replace('\\', '/');
        if (!normalized.startsWith("/"))
            normalized = "/" + normalized;

        // Resolve occurrences of "//" in the normalized path
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index) +
                    normalized.substring(index + 1);
        }

        // Resolve occurrences of "/./" in the normalized path
        while (true) {
            int index = normalized.indexOf("/./");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index) +
                    normalized.substring(index + 2);
        }

        // Resolve occurrences of "/../" in the normalized path
        while (true) {
            int index = normalized.indexOf("/../");
            if (index < 0)
                break;
            if (index == 0)
                return (null);  // Trying to go outside our context
            int index2 = normalized.lastIndexOf('/', index - 1);
            normalized = normalized.substring(0, index2) +
                    normalized.substring(index + 3);
        }

        // Declare occurrences of "/..." (three or more dots) to be invalid
        // (on some Windows platforms this walks the directory tree!!!)
        if (normalized.indexOf("/...") >= 0)
            return (null);

        // Return the normalized path that we have completed
        return (normalized);

    }

}
