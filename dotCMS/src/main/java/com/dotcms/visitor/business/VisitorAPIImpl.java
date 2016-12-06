package com.dotcms.visitor.business;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.dotcms.util.DotPreconditions;
import com.dotcms.util.HttpRequestDataUtil;
import com.dotcms.visitor.domain.ImmutableVisitor;
import com.dotcms.visitor.domain.ImmutableVisitorRequest;
import com.dotcms.visitor.domain.Visitor;
import com.dotcms.visitor.domain.VisitorRequest;
import com.dotcms.visitor.domain.VisitorWrapper;
import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.web.LanguageWebAPI;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.portlets.personas.model.IPersona;
import com.dotmarketing.portlets.personas.model.Persona;
import com.dotmarketing.tag.model.Tag;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.TagUtil;
import com.dotmarketing.util.UUIDUtil;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;

import eu.bitwalker.useragentutils.UserAgent;

public class VisitorAPIImpl implements VisitorAPI {

  private LanguageWebAPI languageWebAPI = WebAPILocator.getLanguageWebAPI();
  private static final String ANON_USER = "anonymous";
  private static final VisitorEvents events= new VisitorEvents();
  @Override
  public void setLanguageWebAPI(LanguageWebAPI languageWebAPI) {
    this.languageWebAPI = languageWebAPI;
  }

  @Override
  public Optional<Visitor>  getVisitor(HttpServletRequest request) {
    return getVisitor(request, Config.getBooleanProperty("CREATE_VISITOR_OBJECT_IN_SESSION", false));
  }

  @Override
  public Optional<Visitor>  getVisitor(HttpServletRequest request, boolean persistInSession) {

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

    return Optional.of(new VisitorWrapper(visitor));
  }

  
  private Visitor updateVisitor(HttpServletRequest request,final Visitor origVisitor){
    
    
    Visitor visitor=origVisitor;
    
    // if we change userId (aka login)
    String userId = ANON_USER;
    try {
      userId = WebAPILocator.getUserWebAPI().getLoggedInUser(request).getUserId();
    } catch (Exception e) {
      
    }
    if(!userId.equals(visitor.userId())){
      visitor = ImmutableVisitor.copyOf(visitor).withUserId(userId);
    }
    

    // If we are forcing a persona on a visitor
    if(request.getParameter(WebKeys.CMS_PERSONA_PARAMETER)!=null){
      try {
        User user = com.liferay.portal.util.PortalUtil.getUser(request);
        Persona p = APILocator.getPersonaAPI().find(request.getParameter(WebKeys.CMS_PERSONA_PARAMETER), user, true);
        visitor = setPersona(visitor, p);
      } catch (Exception e) {
        visitor = setPersona(visitor, null);
      }
    }
    
    
    events.updated(origVisitor, visitor);

    
    
    return new VisitorWrapper(visitor);
  }
  
  
  
  
  
  
  
  @Override
  public VisitorRequest visitorRequest(HttpServletRequest request) {


    long languageId = WebAPILocator.getLanguageWebAPI().getLanguage(request).getId();

    String uri=request.getRequestURI();
    try {
      uri = URLDecoder.decode(
          (request.getAttribute("javax.servlet.forward.request_uri")!=null) 
              ? (String) request.getAttribute("javax.servlet.forward.request_uri") 
                      : request.getRequestURI()
          , "UTF-8");
    } catch (UnsupportedEncodingException e2) {
      Logger.warn(this.getClass(), e2.getMessage());
    }
    
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

    VisitorRequest vr = ImmutableVisitorRequest.builder()
        .protocol(request.getProtocol())
        .serverName(request.getServerName())
        .serverPort(request.getServerPort())
        .queryString(request.getQueryString())
        .userId(visitor.userId())
        .contentId(contentId)
        .uri(uri)
        .pageId(pageId)
        .languageId(languageId)
        .hostId(host.getIdentifier())
        .userAgentHeader( request.getHeader("User-Agent"))
        .userAgent(visitor.getUserAgent())
        .dmid(visitor.dmid())
        .ipAddress(visitor.ipAddress())
        .referer(request.getHeader("referer"))
        .build();
    return vr;
  }


  private Visitor setPersona(Visitor visitor, IPersona persona) {
    // Validate if we must accrue the Tags for this "new" Persona
    if (persona != null
        && (visitor.persona() == null || !visitor.persona().getIdentifier().equals(persona.getIdentifier()))) {

      try {
        // The Persona changed for this Visitor, we must accrue the tags associated to this new
        // Persona
        List<Tag> personaTags = APILocator.getTagAPI().getTagsByInode(persona.getInode());

        String foundTags = TagUtil.tagListToString(personaTags);
        // Accrue these found tags to this visitor object
        TagUtil.accrueTagsToVisitor(visitor, foundTags);
      } catch (DotDataException e) {
        Logger.error(this, "Unable to retrieve Tags associated to Persona [" + persona.getInode() + "].", e);
      }
    }

    return ImmutableVisitor.copyOf(visitor).withPersona(persona);
  }

  @Override
  public Visitor setPersona(IPersona persona, HttpServletRequest request) {
    Visitor visitor = getVisitor(request).get();
    return setPersona(visitor, persona);
  }

  @Override
  public Visitor setVisitor(Visitor visitor, HttpServletRequest request) {
    request.getSession().setAttribute(WebKeys.VISITOR, visitor);
    return visitor;
  }



  private Visitor createVisitor(HttpServletRequest request) {

    String ipAddress = lookupIPAddress(request);

    Language language = languageWebAPI.getLanguage(request);

    Locale locale = new Locale(language.getLanguageCode(), language.getCountryCode());

    UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));

    String dmid = lookupDMID(request);
    boolean isNewVisitor = (dmid == null);
    if (dmid == null) {
      dmid = UUIDUtil.uuidTimeBased();
    }

    Visitor visitor =
        ImmutableVisitor.builder()
        .ipAddress(ipAddress)
        .language(language)
        .locale(locale)
        .dmid(dmid.toString())
        .newVisitor(isNewVisitor)
        .referer(request.getHeader("Referer"))
        .userAgent(userAgent).build();
    events.created(visitor);
    return new VisitorWrapper(visitor);

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

  private String lookupDMID(HttpServletRequest request) {
    String dmidStr = UtilMethods.getCookieValue(request.getCookies(), WebKeys.LONG_LIVED_DOTCMS_ID_COOKIE);
    return dmidStr;
  }




}
