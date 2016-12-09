package com.dotcms.visitor.business;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.dotcms.util.DotPreconditions;
import com.dotcms.util.HttpRequestDataUtil;
import com.dotcms.visitor.domain.DMIDVisitor;
import com.dotcms.visitor.domain.ImmutableVisitor;
import com.dotcms.visitor.domain.ImmutableVisitorRequest;
import com.dotcms.visitor.domain.PersonifiedVisitor;
import com.dotcms.visitor.domain.TaggedVisitor;
import com.dotcms.visitor.domain.UserVisitor;
import com.dotcms.visitor.domain.Visitor;
import com.dotcms.visitor.domain.VisitorRequest;
import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.web.LanguageWebAPI;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.filters.CMSFilter;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.portlets.personas.model.IPersona;
import com.dotmarketing.portlets.personas.model.Persona;
import com.dotmarketing.tag.model.Tag;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UUIDUtil;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.google.common.collect.Lists;
import com.liferay.portal.model.User;

import eu.bitwalker.useragentutils.UserAgent;

public class VisitorAPIImpl implements VisitorAPI {

  private LanguageWebAPI languageWebAPI = WebAPILocator.getLanguageWebAPI();
  private static final String ANON_USER = "anonymous";
  private static final VisitorEvents events = new VisitorEvents();

  @Override
  public void setLanguageWebAPI(LanguageWebAPI languageWebAPI) {
    this.languageWebAPI = languageWebAPI;
  }

  @Override
  public Optional<Visitor> getVisitor(HttpServletRequest request) {
    return getVisitor(request, Config.getBooleanProperty("CREATE_VISITOR_OBJECT_IN_SESSION", false));
  }

  @Override
  public Optional<Visitor> getVisitor(HttpServletRequest request, boolean persistInSession) {

    DotPreconditions.checkNotNull(request, IllegalArgumentException.class, "Null Request");


    Visitor visitor = null;
    HttpSession session = request.getSession(persistInSession);
    if (!Objects.isNull(session)) {
      visitor = (Visitor) session.getAttribute(WebKeys.VISITOR);
    }

    if (visitor == null) {
      visitor = createVisitor(request);
    }

    visitor = updateVisitor(request, visitor);


    request.setAttribute(WebKeys.VISITOR, visitor);
    if (!Objects.isNull(session)) {
      session.setAttribute(WebKeys.VISITOR, visitor);
    }

    return Optional.of(visitor);
  }


  private Visitor updateVisitor(HttpServletRequest request, final Visitor origVisitor) {


    Visitor visitor = origVisitor;

    // if we change userId (aka login)
    User user = null;
    try {
      user = WebAPILocator.getUserWebAPI().getLoggedInUser(request);
    } catch (Exception e) {

    }
    if (user!=null && !user.equals(new UserVisitor(visitor).user())) {
      visitor = new UserVisitor(visitor, user);
    }


    // If we are forcing a persona on a visitor
    if (request.getParameter(WebKeys.CMS_PERSONA_PARAMETER) != null) {
      try {
        User myUser = com.liferay.portal.util.PortalUtil.getUser(request);
        Persona persona =
            APILocator.getPersonaAPI().find(request.getParameter(WebKeys.CMS_PERSONA_PARAMETER), myUser, true);
        visitor = new PersonifiedVisitor(visitor, persona);
      } catch (Exception e) {
        visitor = new PersonifiedVisitor(visitor, null);
      }
    }



    return visitor;

  }



  @Override
  public VisitorRequest visitorRequest(HttpServletRequest request) {


    long languageId = WebAPILocator.getLanguageWebAPI().getLanguage(request).getId();

    String uri = request.getRequestURI();
    try {
      uri = URLDecoder.decode((request.getAttribute("javax.servlet.forward.request_uri") != null)
          ? (String) request.getAttribute("javax.servlet.forward.request_uri") : request.getRequestURI(), "UTF-8");
    } catch (UnsupportedEncodingException e2) {
      Logger.warn(this.getClass(), e2.getMessage());
    }

    CMSFilter.IAm iAm = request.getAttribute(CMSFilter.CMS_FILTER_IAM) != null
        ? (CMSFilter.IAm) request.getAttribute(CMSFilter.CMS_FILTER_IAM) : CMSFilter.IAm.NOTHING_IN_THE_CMS;
    Visitor visitor = getVisitor(request, false).get();

    Host host;
    try {
      host = WebAPILocator.getHostWebAPI().getCurrentHost(request);
    } catch (Exception e) {
      try {
        host = APILocator.getHostAPI().findSystemHost();
      } catch (DotDataException e1) {
        host = new Host();
      }
    }
    String pageId = (String) request.getAttribute(WebKeys.HTMLPAGE_ID);
    String contentId = (String) request.getAttribute(WebKeys.URLMAPPED_ID);

    String dmid = new DMIDVisitor(visitor).dmid();
    
    User user = new UserVisitor(visitor).user();
    String userid = (user!=null) ? user.getUserId() : "anon";
    
    VisitorRequest vr = ImmutableVisitorRequest.builder().protocol(request.getProtocol())
        .serverName(request.getServerName())
        .serverPort(request.getServerPort())
        .queryString(request.getQueryString())
        .userId(userid)
        .contentId(contentId)
        .visitor(visitor)
        .uri(uri)
        .iAm(iAm)
        .pageId(pageId)
        .languageId(languageId)
        .hostId(host.getIdentifier())
        .userAgentHeader(request.getHeader("User-Agent"))
        .userAgent(visitor.userAgent())
        .dmid(dmid)
        .ipAddress(visitor.ipAddress())
        .referer(request.getHeader("referer")).build();
    return vr;
  }


  @Override
  public Visitor setPersona(Visitor visitor, IPersona persona) {
    // Validate if we must accrue the Tags for this "new" Persona
    visitor = new PersonifiedVisitor(visitor, persona);
    try {
      // The Persona changed for this Visitor, we must accrue the tags associated to this new
      // Persona
      List<Tag> personaTags = APILocator.getTagAPI().getTagsByInode(persona.getInode());
      List<String> tagStr = new ArrayList<>();
      for (Tag tag : personaTags) {
        tagStr.add(tag.getTagName());
      }

      // Accrue these found tags to this visitor object
      visitor = new TaggedVisitor(visitor, tagStr);
      visitor = new PersonifiedVisitor(visitor, persona);
    } catch (DotDataException e) {
      Logger.error(this, "Unable to retrieve Tags associated to Persona [" + persona.getInode() + "].", e);
    }

    return visitor;

  }

  @Override
  public Visitor setPersona(HttpServletRequest request, IPersona persona) {
    Visitor visitor = getVisitor(request).get();

    visitor = setPersona(visitor, persona);
    return visitor;
  }

  @Override
  public Visitor setVisitor(HttpServletRequest request, Visitor visitor) {

    request.getSession().setAttribute(WebKeys.VISITOR, visitor);
    return visitor;
  }



  private Visitor createVisitor(HttpServletRequest request) {

    String ipAddress = lookupIPAddress(request);

    Language language = languageWebAPI.getLanguage(request);

    Locale locale = new Locale(language.getLanguageCode(), language.getCountryCode());

    UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));

    

    Visitor visitor = ImmutableVisitor.builder().ipAddress(ipAddress).language(language).locale(locale)
        .referer(request.getHeader("Referer")).userAgent(userAgent).build();

    String dmid = lookupDMID(request);


    visitor = new DMIDVisitor(visitor, dmid);
    
    
    return new TaggedVisitor(visitor);

  }


  private String lookupIPAddress(HttpServletRequest request) {

    String address = null;
    try {
      address = HttpRequestDataUtil.getIpAddress(request).toString();
    } catch (UnknownHostException e) {
      Logger.error(VisitorAPIImpl.class, "Could not get the IP Address from the request", e);
    }

    return address;
  }

  @Override
  public String lookupDMID(HttpServletRequest request) {
    String dmidStr = UtilMethods.getCookieValue(request.getCookies(), WebKeys.LONG_LIVED_DOTCMS_ID_COOKIE);
    return dmidStr==null ? UUIDUtil.uuidTimeBased() : dmidStr;
  }

  /**
   * Method that accrues a given {@link Tag} List to the current {@link Visitor}
   *
   * @param request HttpServletRequest object required in order to find the current {@link Visitor}
   * @param tags {@link Tag} list to accrue
   */
  @Override
  public Visitor accrueTags(HttpServletRequest request, Collection<String> tags) {
    Visitor visitor = getVisitor(request).get();
    return accrueTags(visitor, tags);

  }
  
  @Override
  public Visitor accrueTags(HttpServletRequest request, String tags) {
    Visitor visitor = getVisitor(request).get();
    return accrueTags(visitor, tags);

  }

  @Override
  public Visitor accrueTags(Visitor visitor, Collection<String> tags) {

    return new TaggedVisitor(visitor, tags);


  }

  /**
   * Method that accrues a given String of tag names with a CSV format to the given {@link Visitor}
   *
   * @param visitor {@link Visitor} to accrue the given tags
   * @param tags String of tag names with a CSV format to accrue
   */
  @Override
  public Visitor accrueTags(Visitor visitor, String tags) {

    return new TaggedVisitor(visitor, tags);

  }

}
