package ex04.pymont.connector.http;


/**
 * HTTP 协议常用的 请求头和特殊请求头
 */

public final class DefaultHeaders {


    // -------------------------------------------------------------- Constants


    public static final String AUTHORIZATION_NAME = "authorization";
    public static final String ACCEPT_LANGUAGE_NAME = "accept-language";
    public static final String COOKIE_NAME = "cookie";
    public static final String CONTENT_LENGTH_NAME = "content-length";
    public static final String CONTENT_TYPE_NAME = "content-type";
    public static final String HOST_NAME = "host";
    public static final String CONNECTION_NAME = "connection";
    public static final String CONNECTION_CLOSE_VALUE = "close";
    public static final String EXPECT_NAME = "expect";
    public static final String EXPECT_100_VALUE = "100-continue";
    public static final String TRANSFER_ENCODING_NAME = "transfer-encoding";
    public static final String SESSION_COOKIE_NAME = "jsessionid";

    public static final HttpHeader CONNECTION_CLOSE =
        new HttpHeader("connection", "close");
    public static final HttpHeader EXPECT_CONTINUE =
        new HttpHeader("expect", "100-continue");
    public static final HttpHeader TRANSFER_ENCODING_CHUNKED =
        new HttpHeader("transfer-encoding", "chunked");


    // ----------------------------------------------------------- Constructors


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------------- Properties


    // --------------------------------------------------------- Public Methods


    // --------------------------------------------------------- Object Methods


}
