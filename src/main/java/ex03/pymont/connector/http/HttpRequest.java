package ex03.pymont.connector.http;

/** this class copies methods from org.apache.catalina.connector.HttpRequestBase
 *  and org.apache.catalina.connector.http.HttpRequestImpl.
 *  The HttpRequestImpl class employs a pool of HttpHeader objects for performance
 *  These two classes will be explained in Chapter 4.
 */


import util.RequestUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Principal;

import java.util.*;

public class HttpRequest implements HttpServletRequest {

  private String contentType;
  private String characterEncoding; //编码字符集
  private int contentLength;
  private InetAddress inetAddress;
  private InputStream input;
  private String method;
  private String protocol;
  private String queryString;   //uri的查询字符串
  private String requestURI;
  private String serverName;
  private int serverPort;
  private Socket socket;
  private boolean requestedSessionCookie; //cookie存放session
  private String requestedSessionId;
  private boolean requestedSessionURL; //url存放session
  protected List<Cookie> cookies ;
  private boolean parsed;   //是否已经提取过 请求体
  private Map parameterMap;


  //设置session是否存放cookie
  public void setRequestedSessionCookie(boolean flag) {
    this.requestedSessionCookie = flag;
  }
  //设置session是否存放url
  public void setRequestedSessionURL(boolean flag) {
    requestedSessionURL = flag;
  }
  //设置session
  public void setRequestedSessionId(String requestedSessionId) {
    this.requestedSessionId = requestedSessionId;
  }

  public void addCookie(Cookie cookie){
    if(cookies == null){
      cookies = new ArrayList<>();
    }
    cookies.add(cookie);
  }

  /**
   * Parse the parameters of this request, if it has not already occurred.
   * If parameters are present in both the query string and the request
   * content, they are merged.
   */
  protected void parseParameters() {
    // 若已经完成了parsed，则return
    if (parsed)  return;
    if ( parameterMap == null){
      parameterMap = new HashMap();
    }
    String encoding = getCharacterEncoding(); //获取编码

    // 从uri的QueryString中获取参数
    String queryString = getQueryString();
    try {
      RequestUtil.parseParameters(results, queryString, encoding);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    // Parse any parameters specified in the input stream
    String contentType = getContentType();
    if ("POST".equals(getMethod()) && (getContentLength() > 0)
            && (this.stream == null)
            && "application/x-www-form-urlencoded".equals(contentType)) {

      try {
        int max = getContentLength();
        int len = 0;
        byte buf[] = new byte[getContentLength()];
        ServletInputStream is = getInputStream();
        while (len < max) {
          int next = is.read(buf, len, max - len);
          if (next < 0 ) {
            break;
          }
          len += next;
        }
        is.close();
        if (len < max) {
          // FIX ME, mod_jk when sending an HTTP POST will sometimes
          // have an actual content length received < content length.
          // Checking for a read of -1 above prevents this code from
          // going into an infinite loop.  But the bug must be in mod_jk.
          // Log additional data when this occurs to help debug mod_jk
          StringBuffer msg = new StringBuffer();
          msg.append("HttpRequestBase.parseParameters content length mismatch\n");
          msg.append("  URL: ");
          msg.append(getRequestURL());
          msg.append(" Content Length: ");
          msg.append(max);
          msg.append(" Read: ");
          msg.append(len);
          msg.append("\n  Bytes Read: ");
          if ( len > 0 ) {
            msg.append(new String(buf,0,len));
          }
          log(msg.toString());
          throw new RuntimeException
                  (sm.getString("httpRequestBase.contentLengthMismatch"));
        }
        RequestUtil.parseParameters(results, buf, encoding);
      } catch (UnsupportedEncodingException ue) {
        ;
      } catch (IOException e) {
        throw new RuntimeException
                (sm.getString("httpRequestBase.contentReadFail") +
                        e.getMessage());
      }
    }

    // Store the final results
    results.setLocked(true);
    parsed = true;
    parameters = results;

  }

  public void setContentType(String conType) {
    if( conType == null)
      return ;   //为null 直接返回
    int semicolon = conType.indexOf(';'); //;的位置
    if ( semicolon  >= 0){
      //说明包含了 charset
      characterEncoding = RequestUtil.parseCharacterEncoding(contentType);
      contentType = conType.substring(0,semicolon).trim();
    } else{
      contentType =  conType.trim();
    }
  }

  @Override
  public String getAuthType() {
    return null;
  }

  @Override
  public Cookie[] getCookies() {
    return new Cookie[0];
  }

  @Override
  public long getDateHeader(String s) {
    return 0;
  }

  @Override
  public String getHeader(String s) {
    return null;
  }

  @Override
  public Enumeration getHeaders(String s) {
    return null;
  }

  @Override
  public Enumeration getHeaderNames() {
    return null;
  }

  @Override
  public int getIntHeader(String s) {
    return 0;
  }

  @Override
  public String getMethod() {
    return null;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public void setRequestURI(String requestURI) {
    this.requestURI = requestURI;
  }

  @Override
  public String getPathInfo() {
    return null;
  }

  @Override
  public String getPathTranslated() {
    return null;
  }

  @Override
  public String getContextPath() {
    return null;
  }

  @Override
  public String getQueryString() {
    return queryString;
  }

  public void setQueryString(String strings){
    queryString = strings;
  }
  @Override
  public String getRemoteUser() {
    return null;
  }

  @Override
  public boolean isUserInRole(String s) {
    return false;
  }

  @Override
  public Principal getUserPrincipal() {
    return null;
  }

  @Override
  public String getRequestedSessionId() {
    return null;
  }

  @Override
  public String getRequestURI() {
    return requestURI;
  }

  @Override
  public StringBuffer getRequestURL() {
    return null;
  }

  @Override
  public String getServletPath() {
    return null;
  }

  @Override
  public HttpSession getSession(boolean b) {
    return null;
  }

  @Override
  public HttpSession getSession() {
    return null;
  }

  @Override
  public boolean isRequestedSessionIdValid() {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromUrl() {
    return false;
  }

  @Override
  public Object getAttribute(String s) {
    return null;
  }

  @Override
  public Enumeration getAttributeNames() {
    return null;
  }

  @Override
  public String getCharacterEncoding() {
    if(characterEncoding == null){
      return "ISO-8859-1";
    }
    return characterEncoding;
  }

  @Override
  public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

  }

  @Override
  public int getContentLength() {
    return 0;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    return null;
  }

  @Override
  public String getParameter(String s) {
    return null;
  }

  @Override
  public Enumeration getParameterNames() {
    return null;
  }

  @Override
  public String[] getParameterValues(String s) {
    return new String[0];
  }

  @Override
  public Map getParameterMap() {
    return null;
  }

  @Override
  public String getProtocol() {
    return null;
  }

  @Override
  public String getScheme() {
    return null;
  }

  @Override
  public String getServerName() {
    return null;
  }

  @Override
  public int getServerPort() {
    return 0;
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return null;
  }

  @Override
  public String getRemoteAddr() {
    return null;
  }

  @Override
  public String getRemoteHost() {
    return null;
  }

  @Override
  public void setAttribute(String s, Object o) {

  }

  @Override
  public void removeAttribute(String s) {

  }

  @Override
  public Locale getLocale() {
    return null;
  }

  @Override
  public Enumeration getLocales() {
    return null;
  }

  @Override
  public boolean isSecure() {
    return false;
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String s) {
    return null;
  }

  @Override
  public String getRealPath(String s) {
    return null;
  }








}
