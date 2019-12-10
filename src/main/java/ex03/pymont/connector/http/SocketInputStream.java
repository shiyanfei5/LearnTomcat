package ex03.pymont.connector.http;

import ex02.pymont.Request;
import ex02.pymont.Response;
import ex02.pymont.ServletProcessor;
import ex02.pymont.StaticResourceProcessor;
import util.StringManager;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketInputStream extends InputStream {

    // -------------------------------------------------------------- Constants

    // Socket类的错误信息转换器，一个包下一个对象，共同使用，
    // 只读不写线程安全
    protected static StringManager sm = StringManager.getManager(Constants.Package);

    //-------------------------------------------------------------------------

    /**
     * CR.
     */
    private static final byte CR = (byte) '\r';


    /**
     * LF.
     */
    private static final byte LF = (byte) '\n';


    /**
     * SP.
     */
    private static final byte SP = (byte) ' ';


    /**
     * HT.
     */
    private static final byte HT = (byte) '\t';


    /**
     * COLON.
     */
    private static final byte COLON = (byte) ':';


    /**
     * Lower case offset.
     */
    private static final int LC_OFFSET = 'A' - 'a';


    /**
     * Internal buffer.
     */
    protected byte buf[];


    /**
     * 长度
     */
    protected int count ;


    /**
     * 下一个有效位置
     */
    protected int pos ;


    /**
     * Underlying input stream.
     */
    protected InputStream is;


    // ----------------------------------------------------------- Constructors

    public SocketInputStream(InputStream is, int bufferSize) {
        this.is = is;
        buf = new byte[bufferSize];
    }

    @Override
    public int read() throws IOException {
        // 说明buffer已经读完了
        if(remaining() <= 0){
            // 重新读
            fill();
            //判断读完后是否可用
            if( remaining() <= 0){
                return -1;
            }
        }
        return buf[pos++] & 0xff;
    }

    private void fill() throws IOException{
        //  缓冲区内读取字节
        int len = is.read(buf);
        //  当确实读取字节时
        pos = 0;
        if(len > 0){
            count = len;
        } else{
            // 读取失败（未读取或 len返回 -1)
            count = 0;
        }
    }

    /**
     * 该函数用于判断  缓冲区可读字节数量
     * 下列情况导致可读数量为0
     * 1.缓冲区已被读完
     * 2.读取socket流失败
     * @return
     */
    private int remaining(){
        return count-pos;
    }


    /**
     * 业务实现，填充httpRequestLine(请求首行)
     */
    public void readRequestLine(HttpRequestLine requestLine) throws IOException {
        //从非空格行开始读取
        if (requestLine.methodEnd > 0 || requestLine.protocolEnd > 0 || requestLine.uriEnd > 0) {
            requestLine.recycle();  //回收之前的内容,进行清空
        }
        int chr = 0;
        do {
            try {
                chr = read();
                if(chr == -1){
                    throw  new IOException();
                }
            } catch (IOException e) {
                throw new EOFException
                        (sm.getString("requestStream.readline.error"));
            }
        } while (chr == CR || chr == LF);
        // 从这开始 chr为具体内容
        pos--;      //退回 空格字符

        //开始进入业务
        int readCount = 0; //下次要插入的位置
        while (true) {
            // 判断当前读取
            chr = read();
            // 结束符标志，舍弃读取并退出
            if (chr == SP) {
                requestLine.methodEnd = readCount - 1;
                break;
            }
            //否则，首先判断 requestline容器是否满员，满员则 扩容
            if (readCount >= requestLine.method.length) {
                requestLine.extendMethod(2);
            }
            requestLine.method[readCount] = (char) chr;
            readCount++;
        }
        readCount = 0;
        while (true) {
            // 判断当前读取
            chr = read();
            // 结束符标志，舍弃读取并退出
            if (chr == SP) {
                requestLine.uriEnd = readCount - 1;
                break;
            }
            //否则，首先判断 requestline容器是否满员，满员则 扩容
            if (readCount >= requestLine.uri.length) {
                requestLine.extendUri(2);
            }
            requestLine.uri[readCount] = (char)chr;
            readCount++;
        }
        readCount = 0;
        while (true) {
            // 判断当前读取
            chr = read();
            // 结束符标志，完成了该行的读取
            if (chr == CR) {
                chr = read();
                if(chr == LF){
                    requestLine.protocolEnd = readCount - 1;
                    break;
                }
            }
            //否则，首先判断 requestline容器是否满员，满员则 扩容
            if (readCount >= requestLine.protocol.length) {
                requestLine.extendProtocol(2);
            }
            requestLine.protocol[readCount] = (char) chr;
            readCount++;
        }
    }


    /**
     * 业务实现，填充HttpHeader(请求头)
     */
    public void readHeader(HttpHeader header) throws IOException{
        // 清空header体
        if(header.valueEnd >0 || header.nameEnd >0){
            header.recycle();
        }
        //去除请求体的空格
        int chr = 0;
        do {
            try {
                chr = read();
                if(chr == -1){
                    throw  new IOException();
                }
            } catch (IOException e) {
                throw new EOFException
                        (sm.getString("requestStream.readline.error"));
            }
        } while (chr == CR || chr == LF);
        // 从这开始 chr为具体内容
        pos--;      //退回 空格字符

        int readCount = 0; //下次要插入的位置
        while(true){
            chr = read();
            // 满足该条件退出,到了":"
            if( chr == COLON){
                chr = read();   //再读一个，读到的是" "的话进行退出
                if(chr == SP){
                    header.nameEnd = readCount - 1;
                    break;
                }
            }
            //检查是否需要扩容
            if( readCount >= header.name.length){
                header.extendName(2);   //2倍扩容
            }
            header.name[readCount] = (char)chr;
            readCount++;
        }
        readCount = 0;
        while(true){
            chr = read();
            if(chr == CR){
                chr = read();
                if( chr == LF){
                    header.valueEnd = readCount - 1;
                    break;
                }
            }
            //检查是否需要扩容
            if( readCount >= header.value.length){
                header.extendValue(2);   //2倍扩容
            }
            header.value[readCount] = (char)chr;
            readCount++;
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
                Socket socket = serverSocket.accept();  //开启端口等待tcp连接建立
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                HttpRequestLine httpRequestLine = new HttpRequestLine();
                HttpHeader httpHeader = new HttpHeader();
                SocketInputStream socketInputStream = new SocketInputStream(inputStream,2048);
                socketInputStream.readRequestLine(httpRequestLine);
                socketInputStream.readHeader(httpHeader);

                //资源释放
                socket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }


    }
}
