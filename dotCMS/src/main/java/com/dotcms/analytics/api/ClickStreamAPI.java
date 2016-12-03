package com.dotcms.analytics.api;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dotcms.util.HttpRequestDataUtil;
import com.dotmarketing.beans.BrowserSniffer;
import com.dotmarketing.beans.Clickstream;
import com.dotmarketing.beans.Clickstream404;
import com.dotmarketing.beans.ClickstreamRequest;
import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.DotStateException;
import com.dotmarketing.business.web.HostWebAPI;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.ClickstreamRequestFactory;
import com.dotmarketing.filters.CMSFilter;
import com.dotmarketing.listeners.ClickstreamListener;
import com.dotmarketing.loggers.DatabaseClickstreamLogger;
import com.dotmarketing.util.BotChecker;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.CookieUtil;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;


public class ClickStreamAPI {



  public static final String CLICKSTREAM_SESSION_ATTR_KEY = "clickstream";

  /**
   * Adds a new request to the stream of clicks. The HttpServletRequest is converted to a
   * ClickstreamRequest object and added to the clickstream.
   *
   * @param request - The servlet request to be added to the clickstream.
   * @throws DotDataException An error occurred when interacting with the database.
   */
  public Clickstream build(HttpServletRequest request) throws DotDataException {

    if (request.getAttribute("CLICKSTREAM_RECORDED") != null) {
      return (Clickstream) request.getSession().getAttribute("clickstream");
    }
    request.setAttribute("CLICKSTREAM_RECORDED", true);


    Host host;
    try {
      host = WebAPILocator.getHostWebAPI().getCurrentHost(request);
    } catch (PortalException | SystemException | DotSecurityException e1) {
      host=APILocator.getHostAPI().findSystemHost();
    }



    String uri = (request.getAttribute(WebKeys.CLICKSTREAM_URI_OVERRIDE)!=null) 
        ? (String) request.getAttribute(WebKeys.CLICKSTREAM_URI_OVERRIDE)
            : (String) request.getAttribute(CMSFilter.CMS_FILTER_URI_OVERRIDE);

    HttpSession session = request.getSession(false);

    Clickstream clickstream = (Clickstream) request.getSession(true).getAttribute("clickstream");
    if (clickstream == null) {
      clickstream = new Clickstream();
      session.setAttribute("clickstream", clickstream);
    }

    String associatedIdentifier = request.getParameter("id");
    if (!UtilMethods.isSet(associatedIdentifier)) {
      associatedIdentifier = (String) request.getAttribute(WebKeys.CLICKSTREAM_IDENTIFIER_OVERRIDE);
    }
    if (!UtilMethods.isSet(associatedIdentifier)) {
      associatedIdentifier = APILocator.getIdentifierAPI().find(host, uri).getId();
    }

    clickstream.setLastPageId(associatedIdentifier);
    clickstream.setLastRequest(new Date());

    if (clickstream.getHostname() == null) {
      clickstream.setHostname(request.getRemoteHost());
    }
    if (clickstream.getRemoteAddress() == null) {
      try {
        InetAddress address = HttpRequestDataUtil.getIpAddress(request);
        if (UtilMethods.isSet(address)) {
          clickstream.setRemoteAddress(address.getHostAddress());
        }
      } catch (UnknownHostException e) {
        Logger.debug(ClickStreamAPI.class, "Could not retrieve IP address from request.");
      }
    }
    // Setup initial referrer
    if (clickstream.getInitialReferrer() == null) {
      if (request.getHeader("Referer") != null) {
        clickstream.setInitialReferrer(request.getHeader("Referer"));
      } else {
        clickstream.setInitialReferrer("");
      }
    }
    // if this is the first request in the click stream
    if (clickstream.getClickstreamRequests().size() == Config.getIntProperty("MIN_CLICKSTREAM_REQUESTS_TO_SAVE", 2)) {
      if (request.getHeader("User-Agent") != null) {
        clickstream.setUserAgent(request.getHeader("User-Agent"));
      } else {
        clickstream.setUserAgent("");
      }
      BrowserSniffer bs = new BrowserSniffer(request.getHeader("User-Agent"));
      session.setAttribute("browserSniffer", bs);
      clickstream.setBrowserName(bs.getBrowserName());
      clickstream.setOperatingSystem(bs.getOS());
      clickstream.setBrowserVersion(bs.getBrowserVersion());
      clickstream.setMobileDevice(bs.isMobile());
      clickstream.setBot(BotChecker.isBot(request));
      clickstream.setFirstPageId(associatedIdentifier);
      clickstream.setHostId(host.getIdentifier());

    }

    // Set the cookie id to the long lived cookie
    if (!UtilMethods.isSet(clickstream.getCookieId())) {

      String _dotCMSID = "";
      if (!UtilMethods.isSet(UtilMethods.getCookieValue(request.getCookies(),
          com.dotmarketing.util.WebKeys.LONG_LIVED_DOTCMS_ID_COOKIE))) {
        CookieUtil.createDMIDCookie();

      }
      _dotCMSID =
          UtilMethods.getCookieValue(request.getCookies(), com.dotmarketing.util.WebKeys.LONG_LIVED_DOTCMS_ID_COOKIE);
      clickstream.setCookieId(_dotCMSID);
    }

    // set the user if we have it
    if (session.getAttribute(WebKeys.CMS_USER) != null && clickstream.getUserId() == null) {
      User user = (User) session.getAttribute(WebKeys.CMS_USER);
    }

    ClickstreamRequest cr = ClickstreamRequestFactory.getClickstreamRequest(request, clickstream.getLastRequest());
    clickstream.setNumberOfRequests(clickstream.getNumberOfRequests() + 1);
    cr.setRequestOrder(clickstream.getNumberOfRequests());


    cr.setHostId(host.getIdentifier());
    cr.setAssociatedIdentifier(associatedIdentifier);


    // prevent dupe entries into the clickstream table - just retun if the user is on the same page
    if (clickstream.getClickstreamRequests() != null && clickstream.getClickstreamRequests().size() > 0) {
      ClickstreamRequest last =
          clickstream.getClickstreamRequests().get(clickstream.getClickstreamRequests().size() - 1);
      if (last != null && cr.getAssociatedIdentifier().equals(last.getAssociatedIdentifier())) {
        return clickstream;
      }
    }

    clickstream.addClickstreamRequest(cr);
    return clickstream;



  }




}
