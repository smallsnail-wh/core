package com.dotcms.visitor.business;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.dotcms.repackage.org.apache.logging.log4j.util.Strings;
import com.dotcms.util.DotPreconditions;
import com.dotcms.util.HttpRequestDataUtil;
import com.dotcms.visitor.domain.ImmutableVisitor;
import com.dotcms.visitor.domain.ImmutableVisitorRequest;
import com.dotcms.visitor.domain.Visitor;
import com.dotcms.visitor.domain.VisitorRequest;
import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.web.LanguageWebAPI;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.portlets.personas.model.IPersona;
import com.dotmarketing.portlets.personas.model.Persona;
import com.dotmarketing.tag.model.Tag;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.TagUtil;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

import eu.bitwalker.useragentutils.UserAgent;

public class VisitorAPIImpl implements VisitorAPI {

    private LanguageWebAPI languageWebAPI = WebAPILocator.getLanguageWebAPI();

    @Override
    public void setLanguageWebAPI(LanguageWebAPI languageWebAPI) {
        this.languageWebAPI = languageWebAPI;
    }

    @Override
    public Optional<Visitor> getVisitor(HttpServletRequest request) {
        return getVisitor(request, Config.getBooleanProperty("CREATE_VISITOR_OBJECT_IN_SESSION", true));
    }

    @Override
    public Optional<Visitor> getVisitor(HttpServletRequest request, boolean create) {

        DotPreconditions.checkNotNull(request, IllegalArgumentException.class, "Null Request");

        Optional<Visitor> visitorOpt;

        if(!create) {
            HttpSession session = request.getSession(false);

            if(Objects.isNull(session)) {
                visitorOpt = Optional.empty();
            } else {
                visitorOpt = Optional.ofNullable((Visitor) session.getAttribute(WebKeys.VISITOR));
            }

        } else {
            // lets create a session if not already created
            HttpSession session = request.getSession();
            Visitor visitor = (Visitor) session.getAttribute(WebKeys.VISITOR);

            if(Objects.isNull(visitor)) {
                visitor = createVisitor(request);
                session.setAttribute(WebKeys.VISITOR, visitor);
            }

            visitorOpt = Optional.of(visitor);
        }
        
        // If we are forcing a persona on a visitor
        if(visitorOpt.isPresent()){
			if(Objects.nonNull(request.getParameter(WebKeys.CMS_PERSONA_PARAMETER))){
				Visitor visitor = visitorOpt.get();
				try{		
					User user = com.liferay.portal.util.PortalUtil.getUser(request);
					Persona p = APILocator.getPersonaAPI().find(request.getParameter(WebKeys.CMS_PERSONA_PARAMETER), user, true);
					visitor = ImmutableVisitor.copyOf(visitor).withPersona(p);
				}
				catch(Exception e){
				  visitor = ImmutableVisitor.copyOf(visitor).withPersona(null);
				}
			}
        }

        return visitorOpt;
    }

    public VisitorRequest visitorRequest(HttpServletRequest request) {

      HttpSession session = request.getSession(false);
      long languageId = WebAPILocator.getLanguageWebAPI().getLanguage(request).getId();

      String uri = request.getRequestURI();
      if (request.getAttribute(WebKeys.CLICKSTREAM_URI_OVERRIDE) != null) {
        uri = (String) request.getAttribute(WebKeys.CLICKSTREAM_URI_OVERRIDE);
      }

      Host host;
      try {
        host = WebAPILocator.getHostWebAPI().getCurrentHost(request);
      } catch (Exception e) {
        try {
          host=APILocator.getHostAPI().findSystemHost();
        } catch (DotDataException e1) {
          host = new Host();
        }
      }

      String userId = "anonymous";
      try {
        userId = WebAPILocator.getUserWebAPI().getLoggedInUser(request).getUserId();
      } catch (Exception e) {
        
      }

      
      VisitorRequest vr = ImmutableVisitorRequest.builder()
      .protocol(request.getProtocol())
      .serverName(request.getServerName())
      .serverPort(request.getServerPort())
      .queryString(request.getQueryString())
      .userId(userId)
      .uri(uri)
      .languageId(languageId)
      .hostId(host.getIdentifier()).build();
      return vr;
    }
    
    
    
    public Visitor setPersona(Visitor visitor,IPersona persona) {

      //Validate if we must accrue the Tags for this "new" Persona
      if ( persona != null &&
              (visitor.persona() == null || !visitor.persona().getIdentifier().equals(persona.getIdentifier())) ) {

          try {
              //The Persona changed for this Visitor, we must accrue the tags associated to this new Persona
              List<Tag> personaTags = APILocator.getTagAPI().getTagsByInode(persona.getInode());

              String foundTags = TagUtil.tagListToString(personaTags);
              //Accrue these found tags to this visitor object
              TagUtil.accrueTagsToVisitor(visitor, foundTags);
          } catch (DotDataException e) {
              Logger.error(this, "Unable to retrieve Tags associated to Persona [" + persona.getInode() + "].", e);
          }

      }
      return visitor;

  }
    
    
    
    
    private Visitor createVisitor(HttpServletRequest request) {

        InetAddress ipAddress = lookupIPAddress(request);

        Language language = languageWebAPI.getLanguage(request);

        Locale locale = new Locale(language.getLanguageCode(), language.getCountryCode());

        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));

        UUID dmid = lookupDMID(request);

        boolean isNewVisitor = isNewVisitor(request);

       Visitor visitor = ImmutableVisitor.builder()
          .ipAddress(ipAddress)
          .language(language)
          .locale(locale)
          .dmid(dmid.toString())
          .newVisitor(isNewVisitor)
          .referrer(request.getHeader("Referer"))
          .userAgent(userAgent)
          .build();

        return  visitor;

    }

    private boolean isNewVisitor(HttpServletRequest request) {
        String dmid = UtilMethods.getCookieValue(request.getCookies(),
                com.dotmarketing.util.WebKeys.LONG_LIVED_DOTCMS_ID_COOKIE);

        return Strings.isEmpty(dmid);
    }

    private InetAddress lookupIPAddress(HttpServletRequest request) {

        InetAddress address = null;
        try {
            address = HttpRequestDataUtil.getIpAddress(request);
        } catch(UnknownHostException e) {
            Logger.error(VisitorAPIImpl.class, "Could not get the IP Address from the request", e);
        }

        return address;
    }

    private UUID lookupDMID(HttpServletRequest request) {
        UUID dmid = null;
        String dmidStr = UtilMethods.getCookieValue(request.getCookies(), WebKeys.LONG_LIVED_DOTCMS_ID_COOKIE);

        if(Strings.isBlank(dmidStr))
            return null;

        try {
            dmid = UUID.fromString(dmidStr);
        } catch(IllegalArgumentException e) {
            Logger.error(VisitorAPIImpl.class, "Invalid dmid cookie value", e);
        }

        return dmid;
    }

    private URI lookupReferrer(HttpServletRequest request) {
        URI referrer = null;
        try {
            String referrerStr = request.getHeader("Referer");
            if(Strings.isNotBlank(referrerStr)) {
                referrer = new URI(referrerStr);
            }
        } catch(URISyntaxException e) {
            Logger.error(VisitorAPIImpl.class, "Invalid Referrer sent in request", e);
        }

        return referrer;
    }


}
