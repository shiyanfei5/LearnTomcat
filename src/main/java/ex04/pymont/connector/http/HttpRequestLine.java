package ex04.pymont.connector.http;


import util.StringManager;

import java.io.IOException;

/**
 * HTTP首行 读取
 *
 * @author Remy Maucherat
 * @version $Revision: 1.6 $ $Date: 2002/03/18 07:15:40 $
 *
 */

public final class HttpRequestLine {


    // -------------------------------------------------------------- Constants


    public static final int INITIAL_METHOD_SIZE = 8;
    public static final int INITIAL_URI_SIZE = 64;
    public static final int INITIAL_PROTOCOL_SIZE = 8;
    public static final int MAX_METHOD_SIZE = 1024;
    public static final int MAX_URI_SIZE = 32768;
    public static final int MAX_PROTOCOL_SIZE = 1024;
    //注册一个错误信息映射器
    private static StringManager sm = new StringManager(Constants.Package);

    // ----------------------------------------------------- Instance Variables


    public char[] method;   //存储method内容
    public int methodEnd;   // 结束位置
    public char[] uri;      //存储 uri内容
    public int uriEnd;      // 结束位置
    public char[] protocol;
    public int protocolEnd;

    // ----------------------------------------------------------- Constructors
    public HttpRequestLine() {

        this(new char[INITIAL_METHOD_SIZE], 0, new char[INITIAL_URI_SIZE], 0,
             new char[INITIAL_PROTOCOL_SIZE], 0);

    }


    public HttpRequestLine(char[] method, int methodEnd,
                           char[] uri, int uriEnd,
                           char[] protocol, int protocolEnd) {

        this.method = method;
        this.methodEnd = methodEnd;
        this.uri = uri;
        this.uriEnd = uriEnd;
        this.protocol = protocol;
        this.protocolEnd = protocolEnd;

    }



    // ------------------------------------------------------------- Properties


    // --------------------------------------------------------- Public Methods
    public void extendMethod(int rate ) throws IOException{
        if ((rate * method.length) <= HttpRequestLine.MAX_METHOD_SIZE) {
            char[] newBuffer = new char[rate * method.length];
            System.arraycopy(method, 0, newBuffer, 0, method.length);
            method =  newBuffer;
        } else {
            throw new IOException
                    (sm.getString("requestStream.readline.toolong"));
        }
    }

    public void extendUri(int rate ) throws IOException{
        if ((rate * uri.length) <= HttpRequestLine.MAX_URI_SIZE) {
            char[] newBuffer = new char[rate * uri.length];
            System.arraycopy(uri, 0, newBuffer, 0, uri.length);
            uri =  newBuffer;
        } else {
            throw new IOException
                    (sm.getString("requestStream.readline.toolong"));
        }
    }

    public void extendProtocol(int rate ) throws IOException{
        if ((rate * protocol.length) <= HttpRequestLine.MAX_PROTOCOL_SIZE) {
            char[] newBuffer = new char[rate * protocol.length];
            System.arraycopy(protocol, 0, newBuffer, 0, protocol.length);
            protocol =  newBuffer;
        } else {
            throw new IOException
                    (sm.getString("requestStream.readline.toolong"));
        }
    }


    /**
     * Release all object references, and initialize instance variables, in
     * preparation for reuse of this object.
     */
    public void recycle() {

        methodEnd = 0;
        uriEnd = 0;
        protocolEnd = 0;

    }


    /**
     * Test if the uri includes the given char array.
     */
    public int indexOf(char[] buf) {
        return indexOf(buf, buf.length);
    }


    /**
     * Test if the value of the header includes the given char array.
     */
    public int indexOf(char[] buf, int end) {
        char firstChar = buf[0];
        int pos = 0;
        while (pos < uriEnd) {
            pos = indexOf(firstChar, pos);
            if (pos == -1)
                return -1;
            if ((uriEnd - pos) < end)
                return -1;
            for (int i = 0; i < end; i++) {
                if (uri[i + pos] != buf[i])
                    break;
                if (i == (end-1))
                    return pos;
            }
            pos++;
        }
        return -1;
    }


    /**
     * Test if the value of the header includes the given string.
     */
    public int indexOf(String str) {
        return indexOf(str.toCharArray(), str.length());
    }


    /**
     * Returns the index of a character in the value.
     */
    public int indexOf(char c, int start) {
        for (int i=start; i<uriEnd; i++) {
            if (uri[i] == c)
                return i;
        }
        return -1;
    }


    // --------------------------------------------------------- Object Methods


    public int hashCode() {
        // FIXME
        return 0;
    }


    public boolean equals(Object obj) {
        return false;
    }


}
