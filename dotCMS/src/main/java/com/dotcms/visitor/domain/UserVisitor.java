package com.dotcms.visitor.domain;

import javax.annotation.Nullable;

import com.liferay.portal.model.User;


public class UserVisitor extends AbstractVisitor {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static final String USER = "USER";

  public UserVisitor(Visitor visitor) {
    super(visitor);
  }

  public UserVisitor(Visitor visitor, User user) {
    super(visitor);
    
    map.putIfAbsent(USER, user);
  }


  public User user() {
    return (User) map.get(USER);
  }
}
