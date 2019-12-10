package ex03.pymont.connector.http;

/** this class copies methods from org.apache.catalina.connector.HttpRequestBase
 *  and org.apache.catalina.connector.http.HttpRequestImpl.
 *  The HttpRequestImpl class employs a pool of HttpHeader objects for performance
 *  These two classes will be explained in Chapter 4.
 */


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
  private int contentLength;
  private InetAddress inetAddress;
  private InputStream input;
  private String method;
  private String protocol;
  private String queryString;
  private String requestURI;
  private String serverName;
  private int serverPort;
  private Socket socket;
  private boolean requestedSessionCookie;
  private String requestedSessionId;
  private boolean requestedSessionURL;
  protected List<Cookie> cookies ;


  public void addCookie(Cookie cookie){
    if(cookies == null){
      cookies = new ArrayList<>();
    }
    cookies.add(cookie);
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
    return null;
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
    return null;
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
    return null;
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
    return null;
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
