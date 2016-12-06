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

import com.dotcms.visitor.domain.Visitor;
import com.dotcms.visitor.domain.VisitorRequest;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.filters.CMSFilter;
import com.dotmarketing.util.WebKeys;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebFilter(urlPatterns = "/*",
    dispatcherTypes = {javax.servlet.DispatcherType.REQUEST, javax.servlet.DispatcherType.FORWARD})

public class AnalyticLoggingFilter implements Filter {

  static final ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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
      HttpServletRequest request =(HttpServletRequest)sreq;
      Visitor visitor = APILocator.getVisitorAPI().getVisitor(request).get();
      VisitorRequest vr = APILocator.getVisitorAPI().visitorRequest(request);
      

      
      StringWriter sw = new StringWriter();
      
      sw.append("ip:")
      .append(vr.ipAddress())
      .append('\t')
      .append("time:")
      .append(String.valueOf(System.currentTimeMillis()))
      .append('\t')
      .append("dmid:")
      .append(vr.dmid())
      .append('\t')
      .append("uri:")
      .append(vr.uri())
      .append('\t')
      .append("referer:")
      .append(vr.referer())
      .append('\t')
      .append("host:")
      .append(vr.hostId())
      .append('\t')
      .append("pageId:")
      .append(vr.pageId())
      .append('\t')
      .append("contentId:")
      .append(vr.contentId());

      Logger.info(sw.toString());
      Logger.info(vr);
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
