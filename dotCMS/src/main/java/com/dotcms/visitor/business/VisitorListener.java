package com.dotcms.visitor.business;

import com.dotcms.visitor.domain.Visitor;

public interface VisitorListener {
    void visitorUpdated(Visitor oldVisitor, Visitor upVisitor);
    void visitorCreated(Visitor visitor);
    void visitorDestroyed(Visitor visitor);
}
