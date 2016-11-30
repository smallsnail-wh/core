package com.dotcms.analytics;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;

import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.filters.CMSFilter;
import com.dotmarketing.util.WebKeys;

@WebFilter(urlPatterns = "/*",
    dispatcherTypes = {javax.servlet.DispatcherType.REQUEST, javax.servlet.DispatcherType.FORWARD})

public class AnalyticLoggingFilter implements Filter {

  @Override
  public void destroy() {
    System.err.println("AnalyticLoggingFilter KILLED");

  }
  final SimpleDateFormat sdf =
      new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
  @Override
  public void doFilter(ServletRequest sreq, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    chain.doFilter(sreq, res);
    if(sreq instanceof     HttpServletRequest){
      //configureLogger();
      HttpServletRequest req = (HttpServletRequest) sreq;
      
      
      
      HttpSession session = req.getSession(false);
      long languageId = WebAPILocator.getLanguageWebAPI().getLanguage(req).getId();

      String uri = (req.getAttribute(WebKeys.CLICKSTREAM_URI_OVERRIDE)!=null) ? (String) req.getAttribute(WebKeys.CLICKSTREAM_URI_OVERRIDE): (String) req.getAttribute(CMSFilter.CMS_FILTER_URI_OVERRIDE);

      
      
      StringWriter sw = new StringWriter();
      sw.append(req.getRemoteAddr())
      .append('\t')
      .append(String.valueOf(System.currentTimeMillis()))
      .append('\t')
      .append(req.getMethod())
      .append('\t')
      .append(uri)
      .append('\t')
      .append((String) req.getAttribute(WebKeys.CLICKSTREAM_IDENTIFIER_OVERRIDE));

      Logger.info(sw.toString());
    }

  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {
    configureLogger();
  }

  private void configureLogger() {
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    //FileWriter fw = new FileWriter("/tmp/analyticlogger.log", true, true);
    ConsoleWriter fw = new ConsoleWriter();
    Configurator.currentConfig().removeAllWriters()
      .addWriter(fw)
    .formatPattern("{message}")
    .activate();
    
    

  }


}
