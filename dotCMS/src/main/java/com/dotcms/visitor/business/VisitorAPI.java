package com.dotcms.visitor.business;

import java.util.Collection;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.dotcms.visitor.domain.Visitor;
import com.dotcms.visitor.domain.Visitor;
import com.dotcms.visitor.domain.VisitorRequest;
import com.dotmarketing.business.web.LanguageWebAPI;
import com.dotmarketing.portlets.personas.model.IPersona;

public interface VisitorAPI {

    void setLanguageWebAPI(LanguageWebAPI languageWebAPI);

    Optional<Visitor> getVisitor(HttpServletRequest request);

    Optional<Visitor> getVisitor(HttpServletRequest request, boolean create);


    Visitor setVisitor( HttpServletRequest request,Visitor visitor);

    VisitorRequest visitorRequest(HttpServletRequest request);

    Visitor setPersona(Visitor visitor, IPersona persona);
    Visitor setPersona(HttpServletRequest request,IPersona persona );

    
    Visitor accrueTags(Visitor visitor, String tags);

    Visitor accrueTags(HttpServletRequest request, Collection<String> tags);

    Visitor accrueTags(Visitor visitor, Collection<String> tags);

    Visitor accrueTags(HttpServletRequest request, String tags);

    String lookupDMID(HttpServletRequest request);



}
