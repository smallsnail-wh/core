package com.dotcms.visitor.domain;

import java.util.HashMap;
import java.util.Map;

public class CopyVisitor extends AbstractVisitor {

  private static final long serialVersionUID = 1L;

  public CopyVisitor(Visitor from, Visitor to) {
    super(to, (from instanceof AbstractVisitor) ? ((AbstractVisitor)from).map() : new HashMap<>());
  }


}
