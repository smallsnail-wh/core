package com.dotcms.visitor.business;

import com.dotcms.visitor.domain.Visitor;
import com.dotmarketing.business.web.LanguageWebAPI;
import com.dotmarketing.portlets.personas.model.IPersona;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface VisitorAPI {

    void setLanguageWebAPI(LanguageWebAPI languageWebAPI);

    Optional<Visitor> getVisitor(HttpServletRequest request);

    Optional<Visitor> getVisitor(HttpServletRequest request, boolean create);

    Visitor setPersona(Visitor visitor, IPersona persona);

}
