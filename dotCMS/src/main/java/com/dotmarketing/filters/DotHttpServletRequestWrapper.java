package com.dotmarketing.filters;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

public class DotHttpServletRequestWrapper extends HttpServletRequestWrapper {

  public DotHttpServletRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  @Override
  public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {

    return super.authenticate(response);
  }

  @Override
  public String getAuthType() {

    return super.getAuthType();
  }

  @Override
  public String getContextPath() {

    return super.getContextPath();
  }

  @Override
  public Cookie[] getCookies() {

    return super.getCookies();
  }

  @Override
  public long getDateHeader(String name) {

    return super.getDateHeader(name);
  }

  @Override
  public String getHeader(String name) {

    return super.getHeader(name);
  }

  @Override
  public Enumeration<String> getHeaderNames() {

    return super.getHeaderNames();
  }

  @Override
  public Enumeration<String> getHeaders(String name) {

    return super.getHeaders(name);
  }

  @Override
  public int getIntHeader(String name) {

    return super.getIntHeader(name);
  }

  @Override
  public String getMethod() {

    return super.getMethod();
  }

  @Override
  public Part getPart(String name) throws IllegalStateException, IOException, ServletException {

    return super.getPart(name);
  }

  @Override
  public Collection<Part> getParts() throws IllegalStateException, IOException, ServletException {

    return super.getParts();
  }

  @Override
  public String getPathInfo() {

    return super.getPathInfo();
  }

  @Override
  public String getPathTranslated() {

    return super.getPathTranslated();
  }

  @Override
  public String getQueryString() {

    return super.getQueryString();
  }

  @Override
  public String getRemoteUser() {

    return super.getRemoteUser();
  }

  @Override
  public String getRequestURI() {

    return super.getRequestURI();
  }

  @Override
  public StringBuffer getRequestURL() {

    return super.getRequestURL();
  }

  @Override
  public String getRequestedSessionId() {

    return super.getRequestedSessionId();
  }

  @Override
  public String getServletPath() {

    return super.getServletPath();
  }

  @Override
  public HttpSession getSession() {

    return super.getSession();
  }

  @Override
  public HttpSession getSession(boolean create) {

    return super.getSession(create);
  }

  @Override
  public Principal getUserPrincipal() {

    return super.getUserPrincipal();
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {

    return super.isRequestedSessionIdFromCookie();
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {

    return super.isRequestedSessionIdFromURL();
  }

  @Override
  public boolean isRequestedSessionIdFromUrl() {

    return super.isRequestedSessionIdFromUrl();
  }

  @Override
  public boolean isRequestedSessionIdValid() {

    return super.isRequestedSessionIdValid();
  }

  @Override
  public boolean isUserInRole(String role) {

    return super.isUserInRole(role);
  }

  @Override
  public void login(String username, String password) throws ServletException {

    super.login(username, password);
  }

  @Override
  public void logout() throws ServletException {

    super.logout();
  }

}
