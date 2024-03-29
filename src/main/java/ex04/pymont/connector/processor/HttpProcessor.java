package ex04.pymont.connector.processor;



import ex04.pymont.connector.http.*;
import ex04.pymont.connector.http.request.HttpRequest;
import ex04.pymont.connector.http.request.HttpRequestStream;
import util.RequestUtil;
import util.StringManager;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class HttpProcessor {

    //错误消息映射器
    // Socket类的错误信息转换器，一个包下一个对象，共同使用，
    // 只读不写线程安全
    protected static StringManager sm = StringManager.getManager(Constants.Package);
    //---------------------------实例属性-------------------------
    private HttpConnector connector;    //关联的connector
    private HttpRequest request;
    private HttpResponse response;  //请求
    private boolean http11;         //是否按照http11处理
    private boolean keepAlive;          //是否支持keepAlive
    private boolean sendAck;            // 对于 Expect: 100-continue 默认不支持



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
            request = new HttpRequest(socketInputStream);
            response = new HttpResponse(outputStream);

            //填充Request
            parseRequest(socketInputStream);
            //填充请求头
            parseHeaders(socketInputStream);
            Map a =request.getParameterMap();

            //check if this is a request for a servlet or a static resource
            //a request for a servlet begins with "/servlet/"
            if (request.getRequestURI().startsWith("/servlet/")) {
                ServletProcessor processor = new ServletProcessor();
                processor.process(request, response);
                response.getWriter().flush();
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
            if(httpHeader.valueEnd == 0 || httpHeader.nameEnd == 0){
                break;
            }
            // 获取header的name和value，header永远小写！！！
            String key = new String(httpHeader.name,0,httpHeader.nameEnd).toLowerCase();
            //  value可能大写也可能小写
            String value = new String(httpHeader.value,0,httpHeader.valueEnd);
            request.addHeader(key,value);   //加到整体的header中


            if (key.equals(DefaultHeaders.AUTHORIZATION_NAME)) {
                request.setAuthorization(value);
            } else if(key.equals( DefaultHeaders.COOKIE_NAME)) {
                List<Cookie> cookieList = RequestUtil.parseCookieHeader(value);
                //由于cookie中可能包含session，所以需要如下处理设置session
                for (Cookie cookie : cookieList) {
                    if (cookie.getName().equals( DefaultHeaders.SESSION_COOKIE_NAME)) {
                        // 目前仅仅支持 session存放在 cookie中，该函数永远返回false
                        if (request.isRequestedSessionIdFromCookie()) {
                            //session在cookie
                            request.setRequestedSessionCookie(true);
                            //session在url
                            request.setRequestedSessionURL(false);
                            //设置最后的session
                            request.setRequestedSessionId(cookie.getValue());
                        }
                    }
                    // 把该cookie添加到request中存cookie里
                    request.addCookie(cookie);
                }
            } else if( key.equals(DefaultHeaders.CONTENT_LENGTH_NAME)) {
                try {
                    int n = Integer.parseInt(value);
                    request.setContentLength(n);
                } catch (Exception e) {
                    throw new ServletException(sm.getString("httpProcessor.parseHeaders.contentLength"));
                }
            }   else if (key.equals(DefaultHeaders.CONTENT_TYPE_NAME)) {
                request.setContentType(value);
            }   else if (key.equals( DefaultHeaders.CONNECTION_NAME)){
                //判断value的情况，不为close
                keepAlive = !value.equals(DefaultHeaders.CONNECTION_CLOSE_VALUE);
            }  else if (key.equals(DefaultHeaders.TRANSFER_ENCODING_NAME)){
                request.setTransferEncoding(value);
            }

        }
    }

    public void parseRequest(SocketInputStream socketInputStream)
            throws IOException, ServletException    {
        //初始化请求行容器
        HttpRequestLine requestLine = new HttpRequestLine();
        //填充requestline容器
        socketInputStream.readRequestLine(requestLine);

        String method = new String(requestLine.method,0,requestLine.methodEnd);
        String uri = new String(requestLine.uri,0,requestLine.uriEnd);
        String protocol = new String(requestLine.protocol,0,requestLine.protocolEnd);
        //  处理method，没有什么需要注意的
        if (method.length() < 1) {
            throw new ServletException("Missing HTTP request method");
        }
        else if (uri.length() < 1) {
            throw new ServletException("Missing HTTP request URI");
        }

        // 根据protocol处理一些 属性
        // Now check if the connection should be kept alive after parsing the
        // request.
        if ( protocol.equals("HTTP/1.1") ) {
            http11 = true;
            keepAlive = false;  //默认都为false
        } else {
            http11 = false;
            // For HTTP/1.0, 默认不支持keepAlive功能
            keepAlive = false;
        }

        // 进行uri解析,若存在uri包含参数
        int question = uri.indexOf("?");
        if(question >= 0){
            //存在 ,首先更新 QueryString,uri查询串
            request.setQueryString( uri.substring(question+1));
            uri = uri.substring(0,question); //重设uri
        }

        // 进行uri中jsessionid的检测，uri可能包含jsessionid
        // http://xxxx.rowse.jsp;jsessionid=5AC6268960?curAlbumID=9

        // TODO: 2019/12/10  目前不支持将session放在uri

        //进行uri标准化
        String normalizedUri = RequestUtil.normalize(uri);

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


    public static void main(String[] args){
        ServerSocket serverSocket = null;
        int port = 8080;
        try{
            serverSocket = new ServerSocket(port,1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1); //若出现问题则直接退出
        }
        while(true){
            try{
                Socket socket = serverSocket.accept();
                HttpProcessor httpProcessor = new HttpProcessor(null);
                SocketInputStream socketInputStream = null;
                OutputStream outputStream = null;

                try{
                    socketInputStream = new SocketInputStream( socket.getInputStream(),2048);

                    //创建一个请求
                    httpProcessor.request = new HttpRequest(socketInputStream);

                    //填充Request
                    httpProcessor.parseRequest(socketInputStream);
                    //填充请求头
                    httpProcessor.parseHeaders(socketInputStream);
                    HttpRequestStream httpRequestStream = new HttpRequestStream(httpProcessor.request);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] load = new byte[1024];
                    int len ;
                    while ( (len = httpRequestStream.read(load)) != -1 ){
                        byteArrayOutputStream.write(load,0,len);
                    }
                    System.out.println( new String(byteArrayOutputStream.toByteArray(),httpProcessor.request.getCharacterEncoding()));
                    byteArrayOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e){
                e.printStackTrace();
                continue;
            }

        }


    }

}
